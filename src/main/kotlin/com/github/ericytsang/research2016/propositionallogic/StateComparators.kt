//package com.github.ericytsang.research2016.propositionallogic
//
//import com.github.ericytsang.lib.collections.getRandom
//import java.util.Collections
//import java.util.Comparator
//import java.util.LinkedHashMap
//
//class WeightedHammingDistanceComparator(beliefState:Set<Proposition>,val weights:Map<Variable,Int>):ByDistanceComparator(beliefState)
//{
//    override fun computeDistanceTo(state:State):Int
//    {
//        return beliefStateModels.map {weightedHammingDistance(state,it)}.min() ?: 0
//    }
//
//    /**
//     * returns the weighted hamming distance between this [state1] and
//     * [state2]; the number of mappings of variables to truth values that
//     * they do not match multiplied by their respective weights, then summed
//     * together.
//     */
//    fun weightedHammingDistance(state1:State,state2:State):Int
//    {
//        val commonKeys = if (state1.keys.size < state2.keys.size)
//        {
//            state1.keys.intersect(state2.keys)
//        }
//        else
//        {
//            state2.keys.intersect(state1.keys)
//        }
//
//        return commonKeys
//            // only consider the basic propositions that situations disagree on
//            .filter {state1[it] != state2[it]}
//            // sum them by their weights
//            .sumBy {weights[it] ?: 0}
//    }
//}
//
//class OrderedSetsComparator(beliefState:Set<Proposition>,val orderedSets:List<Proposition>):ByDistanceComparator(beliefState)
//{
//    companion object
//    {
//        fun makeRandom(beliefState:Set<Proposition>,variables:Set<Variable>,numBuckets:Int):OrderedSetsComparator
//        {
//            val allStates = State.permutationsOf(variables)
//                .toMutableList()
//                .apply {Collections.shuffle(this)}
//            val buckets = Array<MutableSet<State>>(numBuckets,{mutableSetOf()})
//            allStates.forEach {buckets.getRandom().add(it)}
//            return OrderedSetsComparator(beliefState,buckets.filter {it.isNotEmpty()}.map {Or.make(it.map {Proposition.makeDnf(it)})!!})
//        }
//    }
//
//    override fun computeDistanceTo(state:State):Int
//    {
//        val completeOrderedSets = listOf(And.make(beliefState.toList()) ?: Proposition.CONTRADICTION)+orderedSets+Proposition.TAUTOLOGY
//        return completeOrderedSets.indexOfFirst {(it.and(Proposition.makeDnf(state))).isSatisfiable}
//    }
//}
