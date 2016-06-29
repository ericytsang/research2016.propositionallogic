package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.permutedIterator
import com.github.ericytsang.lib.collections.toIterable
import com.github.ericytsang.research2016.propositionallogic.AnnouncementResolutionStrategy.ProblemInstance
import java.util.LinkedHashMap

/**
 * Created by surpl on 6/20/2016.
 */
interface AnnouncementResolutionStrategy
{
    fun resolve(problemInstances:List<ProblemInstance>):Proposition?
    data class ProblemInstance(val initialBeliefState:Set<Proposition>,val targetBeliefState:Proposition,val beliefRevisionStrategy:BeliefRevisionStrategy)
    {
        fun reviseBy(sentence:Proposition):Set<Proposition>
        {
            return beliefRevisionStrategy.revise(initialBeliefState,sentence)
        }
        fun isSolvedBy(sentence:Proposition):Boolean
        {
            val resultK = And.make(reviseBy(sentence)) ?: contradiction
            return if (targetBeliefState.isSatisfiable)
            {
                resultK.isSatisfiable && resultK isSubsetOf targetBeliefState
            }
            else
            {
                resultK.isContradiction
            }
        }
    }
}

class BruteForceAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<ProblemInstance>):Proposition?
    {
        // generate generalized announcements
        val announcements = problemInstances
            // get all underlying input variables
            .flatMap {it.initialBeliefState+it.targetBeliefState}.flatMap {it.variables}
            // generate all the possible states that involve the variables
            .let {State.generateFrom(it.toSet())}
            // turn each state into a conjunction of a combination variables and
            // negation of variables
            .map {Proposition.makeFrom(it)}
            // go through every possible subset of conjunctions that can be made and
            // turn each into a disjunction of conjunctions
            .map {setOf(null,it)}.permutedIterator().toIterable()
            .map {it.filterNotNull().toSet()}
            .map()
            {
                conjunctionList ->
                if (conjunctionList.isEmpty())
                {
                    contradiction
                }
                else
                {
                    Or.make(conjunctionList)!!
                }
            }

        // find the announcement that works and return it; null if none work
        return announcements
            .find()
            {
                announcement ->
                problemInstances.all {it.isSolvedBy(announcement)}
            }
    }
}

class ByDistanceAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<ProblemInstance>):Proposition?
    {
        // get all the distance comparators
        val distanceComparators = try
        {
            problemInstances
                .map {it to it.beliefRevisionStrategy as ComparatorBeliefRevisionStrategy}
                .associate {it.first to it.second.situationSorterFactory(it.first.initialBeliefState) as ByDistanceComparator}
        }
        catch (ex:ClassCastException)
        {
            throw IllegalArgumentException("${ByDistanceAnnouncementResolutionStrategy::class.java.simpleName} " +
                "only supports ${ProblemInstance::class.java} objects with belief " +
                "revision strategies of type ${ComparatorBeliefRevisionStrategy::class.java.simpleName} with " +
                "comparators of type ${ByDistanceComparator::class.java.simpleName}")
        }

        val unionOfTargetKs = problemInstances
            .map {it.targetBeliefState}
            .let {Or.make(it) ?: contradiction}
            .models

        val instanceDistanceToModels = problemInstances
            .associate()
            {
                problemInstance ->
                problemInstance to unionOfTargetKs
                    .groupBy {distanceComparators[problemInstance]!!.getDistanceTo(it)}
                    .mapValues {it.value.toSet()}
            }
        fun ProblemInstance.getDistanceToModelsMap():Map<Int,Set<State>> = instanceDistanceToModels[this]!!
        fun ProblemInstance.getModelsAtDistance(distance:Int):Set<State> = instanceDistanceToModels[this]!![distance] ?: emptySet()
        fun ProblemInstance.getModelsInRange(range:IntRange):Set<State> = instanceDistanceToModels[this]!!.entries.filter {it.key in range}.flatMap {it.value}.toSet()

        val allAnnouncements:Map<ProblemInstance,Map<Int,Announcement>> = problemInstances
            .associate()
            {
                problemInstance ->
                val distanceToAnnouncementMap = problemInstance.getDistanceToModelsMap().entries.associate()
                {
                    val distance = it.key
                    val (modelsAtDistanceInTargetK,modelsAtDistanceNotInTargetK) = problemInstance
                        .getModelsAtDistance(distance)
                        .partition {(Proposition.makeFrom(it) and problemInstance.targetBeliefState).isSatisfiable}
                    val rejectedStates = problemInstance
                        .getModelsInRange(0..distance-1)
                        .plus(modelsAtDistanceNotInTargetK)
                        .toSet()
                    val acceptedStates = modelsAtDistanceInTargetK
                        .toSet()
                    val announcement = Announcement(rejectedStates,acceptedStates)
                    distance to announcement
                }
                problemInstance to distanceToAnnouncementMap
            }

        val instanceToSearchDistanceMap = LinkedHashMap<ProblemInstance,Int>()
        fun ProblemInstance.getSearchDistance():Int? = instanceToSearchDistanceMap[this]
        fun ProblemInstance.setSearchDistance(distance:Int)
        {
            instanceToSearchDistanceMap[this] = distance
        }
        val selectedAnnouncements:MutableMap<ProblemInstance,Announcement> = LinkedHashMap()
        var unsolvedProblemInstances = problemInstances.toSet()

        // while there are unsolved problem instances
        while (true)
        {
            // for each unsolved problem instance, find the announcement of the
            // smallest distance that, in conjunction with the existing
            // announcements, would solve the problem instance...if none exists,
            // it is unsolvable.
            val newAnnouncements = unsolvedProblemInstances.map()
            {
                problemInstance ->

                // expand or initialize search distance
                var searchDistance = problemInstance.getSearchDistance()
                if (searchDistance != null)
                {
                    searchDistance = allAnnouncements[problemInstance]!!.keys.filter {it > searchDistance!!}.sorted().firstOrNull() ?: return null
                }
                else
                {
                    searchDistance = allAnnouncements[problemInstance]!!.keys.min()!!
                }
                problemInstance.setSearchDistance(searchDistance)

                // get the announcement for the instance at the distance
                val distanceToAnnouncementMap = allAnnouncements[problemInstance]!!
                return@map problemInstance to distanceToAnnouncementMap[searchDistance]!!
            }

            // check and update which problem instances are unsolved
            selectedAnnouncements.putAll(newAnnouncements)
            val allRejectedStates = selectedAnnouncements
                .values
                .flatMap {it.rejectedStates}
                .map {Proposition.makeFrom(it)}
            val allAcceptedStates = selectedAnnouncements
                .values
                .flatMap {it.acceptedStates}
                .map {Proposition.makeFrom(it)}
            val announcement = ((Or.make(allRejectedStates) ?: contradiction).not and (Or.make(allAcceptedStates) ?: contradiction))
            unsolvedProblemInstances = problemInstances
                .filter {!it.isSolvedBy(announcement)}.toSet()

            // return the announcement if all problem instances are solved by it
            if (unsolvedProblemInstances.isEmpty())
            {
                return announcement
            }
        }
    }

    private data class Announcement(val rejectedStates:Set<State>,val acceptedStates:Set<State>)
}

fun findAllAnnouncements(problemInstances:List<ProblemInstance>):Set<Proposition>
{
    // generate generalized announcements
    val announcements = problemInstances
        // get all underlying input variables
        .flatMap {it.initialBeliefState+it.targetBeliefState}.flatMap {it.variables}
        // generate all the possible states that involve the variables
        .let {State.generateFrom(it.toSet())}
        // turn each state into a conjunction of a combination variables and
        // negation of variables
        .map {Proposition.makeFrom(it)}
        // go through every possible subset of conjunctions that can be made and
        // turn each into a disjunction of conjunctions
        .map {setOf(null,it)}.permutedIterator().toIterable()
        .map {it.filterNotNull().toSet()}
        .map()
        {
            conjunctionList ->
            if (conjunctionList.isEmpty())
            {
                contradiction
            }
            else
            {
                Or.make(conjunctionList)!!
            }
        }

    // find the announcement that works and return it; null if none work
    return announcements
        .filter()
        {
            announcement ->
            problemInstances.all {it.isSolvedBy(announcement)}
        }
        .toSet()
}
