package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.permutedIterator
import com.github.ericytsang.lib.collections.toIterable

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
            return if (targetBeliefState.models.isNotEmpty())
            {
                resultK.models.isNotEmpty() && resultK isSubsetOf targetBeliefState
            }
            else
            {
                resultK.models.isEmpty()
            }
        }
    }
}

// todo
class ByDistanceAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<AnnouncementResolutionStrategy.ProblemInstance>):Proposition?
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
                "only supports ${AnnouncementResolutionStrategy.ProblemInstance::class.java} objects with belief " +
                "revision strategies of type ${ComparatorBeliefRevisionStrategy::class.java.simpleName} with " +
                "comparators of type ${ByDistanceComparator::class.java.simpleName}")
        }

        val unionOfTargetKs = problemInstances
            .map {it.targetBeliefState}
            .let {Or.make(it) ?: contradiction}
            .models

        val distanceToModels = problemInstances
            .associate()
            {
                problemInstance ->
                problemInstance to unionOfTargetKs
                    .groupBy {distanceComparators[problemInstance]!!.getDistanceTo(it)}
                    .mapValues {it.value.toSet()}
            }

        val otherAnnouncements:MutableMap<AnnouncementResolutionStrategy.ProblemInstance,Proposition> = mutableMapOf()
        var unsolvedProblemInstances = problemInstances.toSet()

        // while there are unsolved problem instances
        while (unsolvedProblemInstances.isNotEmpty())
        {
            // for each unsolved problem instance, find the announcement of the
            // smallest distance that, in conjunction with the existing
            // announcements, would solve the problem instance...if none exists,
            // it is unsolvable.
            val newAnnouncements = unsolvedProblemInstances.map()
            {
                problemInstance ->
                val potentialAnnouncement = distanceToModels[problemInstance]!!.entries
                    // sort state sets by distance
                    .sortedBy {it.key}
                    // transform state sets into announcements
                    .map()
                    {
                        val disjunctionOfModelsAtDistance = Or.make(it.value.map {Proposition.makeFrom(it)})!!
                        val announcement = disjunctionOfModelsAtDistance.not or problemInstance.targetBeliefState
                        (otherAnnouncements+(problemInstance to announcement)).let {And.make(it.values)}!!
                    }
                    // find the announcement that would satisfies this problem instance
                    .find {problemInstance.isSolvedBy(it)} ?: return null
                problemInstance to potentialAnnouncement
            }

            // check and update which problem instances are unsolved
            otherAnnouncements.putAll(newAnnouncements)
            val announcement = And.make(otherAnnouncements.values)!!
            unsolvedProblemInstances = problemInstances
                .filter {!it.isSolvedBy(announcement)}.toSet()
        }

        // return the announcement
        return And.make(otherAnnouncements.values)!!
    }
}

fun findAllAnnouncements(problemInstances:List<AnnouncementResolutionStrategy.ProblemInstance>):Set<Proposition>
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
