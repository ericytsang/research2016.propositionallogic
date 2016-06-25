package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.getRandom
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
abstract class ByDistanceComparator(val beliefState:Set<Proposition>):Comparator<State>
{
    /**
     * all models of the receiver.
     */
    protected val beliefStateModels:Set<State> = run()
    {
        beliefState.let()
        {
            if (!it.iterator().hasNext())
            {
                contradiction
            }
            else
            {
                And.make(it.toList())
            }
        }.models
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
}

class WeightedHammingDistanceComparator(beliefState:Set<Proposition>,val weights:Map<Variable,Int>):ByDistanceComparator(beliefState)
{
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
}

class OrderedSetsComparator(beliefState:Set<Proposition>,val orderedSets:List<Proposition>):ByDistanceComparator(beliefState)
{
    companion object
    {
        fun makeRandom(beliefState:Set<Proposition>,variables:Set<Variable>,numBuckets:Int):OrderedSetsComparator
        {
            val allStates = State.generateFrom(variables)
                .toMutableList()
                .apply {Collections.shuffle(this)}
            val buckets = Array<MutableSet<State>>(numBuckets,{mutableSetOf()})
            allStates.forEach {buckets.getRandom().add(it)}
            return OrderedSetsComparator(beliefState,buckets.map {Or.Companion.make(it.map {Proposition.makeFrom(it)})})
        }
    }

    override fun computeDistanceTo(state:State):Int
    {
        val completeOrderedSets = listOf(And.make(beliefState.toList()))+orderedSets+tautology
        return completeOrderedSets.indexOfFirst {(it and Proposition.makeFrom(state)).isSatisfiable}
    }
}
