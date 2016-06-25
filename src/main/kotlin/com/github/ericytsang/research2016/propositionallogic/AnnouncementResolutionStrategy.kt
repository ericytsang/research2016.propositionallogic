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
    }
}

class SimpleAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<AnnouncementResolutionStrategy.ProblemInstance>):Proposition?
    {
        // list of all initial belief states
        val initialKs = problemInstances.map {it.targetBeliefState}

        // the base announcement is a disjunction of all target belief states
        val baseAnnouncement = initialKs
            // remove all belief states that are satisfiable by another belief state
            .filter {k -> initialKs.minus(k).all {anotherK -> !(k isSatisfiedBy anotherK)}}
            // turn remaining belief states into a disjunction
            .let {Or.make(it)}

        // generate generalized announcements
        val announcements = problemInstances
            // get all underlying input variables
            .flatMap {it.initialBeliefState+it.targetBeliefState}.flatMap {it.variables}
            // only keep the variables that are not mentioned in the base announcement
            .filter {it !in baseAnnouncement.variables}.toSet()
            // generate all the possible states that involve the variables
            .let {State.generateFrom(it)}
            // make a sentence from the state and concatenate it with the base announcement
            .map {baseAnnouncement and Proposition.makeFrom(it)}

        // find the announcement that works and return it; null if none work
        return announcements.find()
        {
            announcement ->
            problemInstances
                // perform the belief revision (K * announcement) for each problem instance
                .associate {problem -> problem.targetBeliefState to problem.reviseBy(announcement)}
                // make sure all target belief states are satisfied by their corresponding revision result
                .let {targetsToResults -> targetsToResults.all {it.key isSatisfiedBy And.make(it.value)}}
        }
    }
}

//// todo
//class ByDistanceAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
//{
//    override fun resolve(problemInstances:List<AnnouncementResolutionStrategy.ProblemInstance>):Proposition?
//    {
//        val distanceComparators = try
//        {
//            problemInstances
//                .map {it to it.beliefRevisionStrategy as ComparatorBeliefRevisionStrategy}
//                .associate {it.first to it.second.situationSorterFactory(it.first.initialBeliefState) as ByDistanceComparator}
//        }
//        catch (ex:ClassCastException)
//        {
//            throw IllegalArgumentException("${ByDistanceAnnouncementResolutionStrategy::class.java.simpleName} " +
//                "only supports ${AnnouncementResolutionStrategy.ProblemInstance::class.java} objects with belief " +
//                "revision strategies of type ${ComparatorBeliefRevisionStrategy::class.java.simpleName} with " +
//                "comparators of type ${ByDistanceComparator::class.java.simpleName}")
//        }
//
//        val announcement = problemInstances
//            .
//
//        distanceComparators.values.first().getDistanceTo()
//
//        // list of all initial belief states
//        val initialKs = problemInstances.map {it.targetBeliefState}
//
//        // the base announcement is a disjunction of all target belief states
//        val baseAnnouncement = initialKs
//            // remove all belief states that are satisfiable by another belief state
//            .filter {k -> initialKs.minus(k).all {anotherK -> !(k isSatisfiedBy anotherK)}}
//            // turn remaining belief states into a disjunction
//            .let {Or.make(it)}
//
//        // generate generalized announcements
//        val announcements = problemInstances
//            // get all underlying input variables
//            .flatMap {it.initialBeliefState+it.targetBeliefState}.flatMap {it.variables}
//            // only keep the variables that are not mentioned in the base announcement
//            .filter {it !in baseAnnouncement.variables}.toSet()
//            // generate all the possible states that involve the variables
//            .let {State.generateFrom(it)}
//            // make a sentence from the state and concatenate it with the base announcement
//            .map {baseAnnouncement and Proposition.makeFrom(it)}
//
//        // find the announcement that works and return it; null if none work
//        return announcements.find()
//        {
//            announcement ->
//            problemInstances
//                // perform the belief revision (K * announcement) for each problem instance
//                .associate {problem -> problem.targetBeliefState to problem.reviseBy(announcement)}
//                // make sure all target belief states are satisfied by their corresponding revision result
//                .let {targetsToResults -> targetsToResults.all {it.key isSatisfiedBy And.make(it.value)}}
//        }
//    }
//}

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
                Or.make(conjunctionList)
            }
        }

    // find the announcement that works and return it; null if none work
    return announcements
        .filter()
        {
            announcement ->
            problemInstances
                // perform the belief revision (K * announcement) for each problem instance
                .associate {problem -> problem.targetBeliefState to problem.reviseBy(announcement)}
                // make sure all target belief states are satisfied by their corresponding revision result
                .let {targetsToResults -> targetsToResults.all {it.key isSatisfiedBy And.make(it.value)}}
        }
        .toSet()
}
