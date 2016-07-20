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
            .let {State.permutationsOf(it.toSet())}
            // turn each state into a conjunction of a combination variables and
            // negation of variables
            .map {Proposition.fromState(it)}
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

class OrderedAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<ProblemInstance>):Proposition?
    {
        val instanceToStatePartitionsMap = run()
        {
            val comparators = try
            {
                problemInstances
                    .map {it to it.beliefRevisionStrategy as ComparatorBeliefRevisionStrategy}
                    .associate {it.first to it.second.situationSorterFactory(it.first.initialBeliefState)}
            }
            catch (ex:ClassCastException)
            {
                throw IllegalArgumentException("${OrderedAnnouncementResolutionStrategy::class.java.simpleName} " +
                    "only supports ${ProblemInstance::class.java} objects with belief " +
                    "revision strategies of type ${ComparatorBeliefRevisionStrategy::class.java.simpleName} with " +
                    "comparators of type ${ByDistanceComparator::class.java.simpleName}")
            }

            val unionOfTargetKs = problemInstances
                .map {it.targetBeliefState}
                .let {Or.make(it) ?: contradiction}
                .models

            problemInstances.associate()
            {
                problemInstance ->
                val comparator = comparators[problemInstance]!!
                val sortedStates = unionOfTargetKs
                    .sortedWith(comparator)
                val partitionedStates = mutableListOf<Set<State>>()

                if (sortedStates.isNotEmpty())
                {
                    // todo refactor loop
                    var markerState = sortedStates.first()
                    var equalStates = mutableSetOf<State>()
                    for (state in sortedStates)
                    {
                        if (comparator.compare(state,markerState) != 0)
                        {
                            partitionedStates.add(equalStates)
                            equalStates = mutableSetOf()
                            markerState = state
                        }
                        equalStates.add(state)
                    }
                    partitionedStates.add(equalStates)
                }

                problemInstance to partitionedStates
            }
        }
        fun ProblemInstance.getStatePartitions():List<Set<State>> = instanceToStatePartitionsMap[this]!!
        fun ProblemInstance.getStatesInPartition(partition:Int):Set<State> = instanceToStatePartitionsMap[this]!![partition]
        fun ProblemInstance.getStatesInPartitions(partitions:IntRange):Set<State> = instanceToStatePartitionsMap[this]!!.filterIndexed {i,it -> i in partitions}.flatMap {it}.toSet()

        val instanceToSearchDistanceMap = LinkedHashMap<ProblemInstance,Int>()
        fun ProblemInstance.getSearchDistance():Int = instanceToSearchDistanceMap[this] ?: 0
        fun ProblemInstance.setSearchDistance(distance:Int)
        {
            instanceToSearchDistanceMap[this] = distance
        }

        val instanceToAcceptedStatesMap = LinkedHashMap<ProblemInstance,Set<State>>()
        fun ProblemInstance.getAcceptedStates():Set<State> = instanceToAcceptedStatesMap[this]!!
        fun ProblemInstance.setAcceptedStates(states:Set<State>)
        {
            instanceToAcceptedStatesMap[this] = states
        }

        val instanceToRejectedStatesMap = LinkedHashMap<ProblemInstance,Set<State>>()
        fun ProblemInstance.getRejectedStates():Set<State> = instanceToRejectedStatesMap[this]!!
        fun ProblemInstance.setRejectedStates(states:Set<State>)
        {
            instanceToRejectedStatesMap[this] = states
        }

        var unsolvedProblemInstances = problemInstances.toSet()

        // while there are unsolved problem instances
        while (true)
        {
            // for each unsolved problem instance, find the announcement of the
            // smallest distance that, in conjunction with the existing
            // announcements, would solve the problem instance...if none exists,
            // it is unsolvable.
            unsolvedProblemInstances.forEach()
            {
                problemInstance ->

                // expand search distance
                val searchDistance = problemInstance.getSearchDistance()
                problemInstance.setSearchDistance(searchDistance+1)

                if (searchDistance !in problemInstance.getStatePartitions().indices)
                {
                    return null
                }

                // find out which states to accept and which to reject
                val acceptedStates = problemInstance
                    .getStatesInPartition(searchDistance)
                    .map {Proposition.fromState(it)}
                    .let {Or.make(it) ?: contradiction}
                    .let {it and problemInstance.targetBeliefState}
                    .models
                val rejectedStates = problemInstance
                    .getStatesInPartitions(0..searchDistance)
                    .map {Proposition.fromState(it)}
                    .let {Or.make(it) ?: contradiction}
                    .let {it and (Or.make(acceptedStates.map {Proposition.fromState(it)})?.not ?: contradiction)}
                    .models
                problemInstance.setAcceptedStates(acceptedStates)
                problemInstance.setRejectedStates(rejectedStates)
                Unit
            }

            // check and update which problem instances are unsolved
            //selectedAnnouncements.putAll(newAnnouncements)
            val allRejectedStates = problemInstances
                .flatMap {it.getRejectedStates()}
                .toSet()
            val allAcceptedStates = problemInstances
                .flatMap {it.getAcceptedStates()}
                .filter {it !in allRejectedStates}
                .map {Proposition.fromState(it)}
                .toSet()
            val announcement = Or.make(allAcceptedStates) ?: contradiction

            // return the announcement if all problem instances are solved by it
            unsolvedProblemInstances = problemInstances
                .filter {!it.isSolvedBy(announcement)}
                .toSet()
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
        .let {State.permutationsOf(it.toSet())}
        // turn each state into a conjunction of a combination variables and
        // negation of variables
        .map {Proposition.fromState(it)}
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
