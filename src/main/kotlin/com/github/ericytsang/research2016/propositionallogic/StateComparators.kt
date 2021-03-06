package com.github.ericytsang.research2016.propositionallogic

import java.util.Collections
import java.util.Comparator
import java.util.LinkedHashMap

/**
 * [beliefState] is the belief state that is being revised.
 *
 * the [ByDistanceComparator] is a [Comparator] that can compare [State]
 * with one another to specify an ordering.
 *
 * this implementation assumes that each [State] is a specific distance away
 * from the [beliefState], and implements the [compare] function with this
 * assumption.
 */
abstract class ByDistanceComparator(val beliefState:Set<Proposition>):ComparatorBeliefRevisionStrategy.Comparator
{
    /**
     * all models of the receiver.
     */
    protected val beliefStateModels:Set<State> = run()
    {
        beliefState.let {And.make(it.toList()) ?: contradiction}.models
    }

    /**
     * used to cache previous calculations produced by the [computeDistanceTo]
     * function.
     */
    private val cachedCalculations = LinkedHashMap<State,Int>()

    override fun compare(state1:State,state2:State):Int
    {
        val situation1Distance = getDistanceTo(state1)
        val situation2Distance = getDistanceTo(state2)
        return situation1Distance-situation2Distance
    }

    fun getDistanceTo(state:State):Int
    {
        return cachedCalculations.getOrPut(state)
        {
            computeDistanceTo(state)
        }
    }

    /**
     * returns the distance from the [state] to the [beliefState].
     */
    protected abstract fun computeDistanceTo(state:State):Int
}

class HammingDistanceComparator(beliefState:Set<Proposition>):ByDistanceComparator(beliefState)
{
    override val friendlyName:String = "hamming distance"

    override fun computeDistanceTo(state:State):Int
    {
        return beliefStateModels.map {hammingDistance(state,it)}.min() ?: 0
    }

    /**
     * returns the hamming distance between this [state1] and [state2];
     * the number of mappings of variables to truth values that they do not
     * match.
     */
    fun hammingDistance(state1:State,state2:State):Int
    {
        val commonKeys = if (state1.keys.size < state2.keys.size)
        {
            state1.keys.intersect(state2.keys)
        }
        else
        {
            state2.keys.intersect(state1.keys)
        }

        return commonKeys.count {state1[it] != state2[it]}
    }

    override fun hashCode():Int = 0

    override fun equals(other:Any?):Boolean
    {
        return other is HammingDistanceComparator
    }
}

class WeightedHammingDistanceComparator(beliefState:Set<Proposition>,val weights:Map<Variable,Int>):ByDistanceComparator(beliefState)
{
    override val friendlyName:String = "weighted hamming distance"

    override fun computeDistanceTo(state:State):Int
    {
        return beliefStateModels.map {weightedHammingDistance(state,it)}.min() ?: 0
    }

    /**
     * returns the weighted hamming distance between this [state1] and
     * [state2]; the number of mappings of variables to truth values that
     * they do not match multiplied by their respective weights, then summed
     * together.
     */
    fun weightedHammingDistance(state1:State,state2:State):Int
    {
        val commonKeys = if (state1.keys.size < state2.keys.size)
        {
            state1.keys.intersect(state2.keys)
        }
        else
        {
            state2.keys.intersect(state1.keys)
        }

        return commonKeys
            // only consider the basic propositions that situations disagree on
            .filter {state1[it] != state2[it]}
            // sum them by their weights
            .sumBy {weights[it] ?: 0}
    }

    override fun hashCode():Int = weights.hashCode()

    override fun equals(other:Any?):Boolean
    {
        return other is WeightedHammingDistanceComparator &&
            other.weights == weights
    }
}

class OrderedSetsComparator(beliefState:Set<Proposition>,val orderedSets:List<Proposition>):ByDistanceComparator(beliefState)
{
    companion object
    {
        fun makeRandom(beliefState:Set<Proposition>,variables:Set<Variable>,numBuckets:Int):OrderedSetsComparator
        {
            val allStates = State.permutationsOf(variables)
                .toMutableList()
                .apply {Collections.shuffle(this)}
            val buckets = Array<MutableSet<State>>(numBuckets,{mutableSetOf()})
            allStates.forEach {buckets.getRandom().add(it)}
            return OrderedSetsComparator(beliefState,buckets.filter {it.isNotEmpty()}.map {Or.make(it.map {Proposition.fromState(it)})!!})
        }
    }

    override val friendlyName:String = "ordered sentences"

    override fun computeDistanceTo(state:State):Int
    {
        val completeOrderedSets = listOf(And.make(beliefState) ?: contradiction)+orderedSets+tautology
        return completeOrderedSets.indexOfFirst {(it and Proposition.fromState(state)).isSatisfiable}
    }

    override fun hashCode():Int = orderedSets.hashCode()

    override fun equals(other:Any?):Boolean
    {
        return other is OrderedSetsComparator &&
            other.orderedSets == orderedSets
    }
}
