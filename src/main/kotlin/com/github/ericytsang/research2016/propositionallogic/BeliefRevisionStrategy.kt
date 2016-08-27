package com.github.ericytsang.research2016.propositionallogic

import java.util.Comparator

interface BeliefRevisionStrategy
{
    val friendlyName:String
    fun revise(beliefState:Set<Proposition>,sentence:Proposition):Set<Proposition>
    override fun hashCode():Int
    override fun equals(other:Any?):Boolean
}

class TrustSensitiveBeliefRevisionStrategy(val beliefRevisionStrategy:BeliefRevisionStrategy,val trustPartitionSentenceRevisionStrategy:TrustPartitionSentenceRevisionStrategy):BeliefRevisionStrategy
{
    override val friendlyName:String get() = "trust-sensitive ${beliefRevisionStrategy.friendlyName}"
    override fun revise(beliefState:Set<Proposition>,sentence:Proposition):Set<Proposition>
    {
        val revisedSentence = trustPartitionSentenceRevisionStrategy.revise(sentence)
        return beliefRevisionStrategy.revise(beliefState,revisedSentence)
    }
    override fun hashCode():Int = beliefRevisionStrategy.hashCode()+trustPartitionSentenceRevisionStrategy.hashCode()
    override fun equals(other:Any?):Boolean
    {
        return other is TrustSensitiveBeliefRevisionStrategy &&
            other.beliefRevisionStrategy == beliefRevisionStrategy &&
            other.trustPartitionSentenceRevisionStrategy == trustPartitionSentenceRevisionStrategy
    }
}

/**
 * class that uses an instance of the [Comparator] to order instances of the
 * [State] in order to do the belief revision.
 *
 * @param situationSorterFactory used to create the [Comparator]. it will be
 * given the initial belief state as an argument, and must return the
 * appropriate [Comparator] which will be used to sort the [State]s.
 */
class ComparatorBeliefRevisionStrategy(val situationSorterFactory:(Set<Proposition>)->Comparator):BeliefRevisionStrategy
{
    override val friendlyName:String get() = situationSorterFactory(emptySet()).friendlyName
    override fun revise(beliefState:Set<Proposition>,sentence:Proposition):Set<Proposition>
    {
        // create the situation sorter
        val situationSorter = situationSorterFactory(beliefState)

        // create a tautology that uses all the variables in all the formulas
        // e.g. (a or -a) and (b or -b) and (c or -c) and...
        val basicPropositionTautologies = (setOf(sentence)+beliefState)
            // get all basic propositions involved
            .flatMap {it.variables}.toSet()
            // make each one into a tautology
            .map {it or it.not}
            // and them together
            .let {And.make(it)!!}

        // all models of the sentence..and'd together with
        // basicPropositionTautologies to make sure the resulting models
        // contains a mapping for all variables
        val sentenceModels = (sentence and basicPropositionTautologies).models

        // find the "first" model in the ordering or models O(n)
        val nearestModel = sentenceModels.minWith(situationSorter)
            ?: return setOf(contradiction)

        // keep only the ones with the least distance according to the sorter
        val nearestModels = sentenceModels
            .filter {situationSorter.compare(nearestModel,it) == 0}

        // convert into a conjunctive normal form proposition and return
        return setOf(Or.make(nearestModels.map {Proposition.fromState(it)})!!)
    }
    override fun hashCode():Int = situationSorterFactory(emptySet()).hashCode()
    override fun equals(other:Any?):Boolean
    {
        return other is ComparatorBeliefRevisionStrategy &&
            other.situationSorterFactory(emptySet()) == situationSorterFactory(emptySet())
    }

    interface Comparator:java.util.Comparator<State>
    {
        val friendlyName:String
        override fun hashCode():Int
        override fun equals(other:Any?):Boolean
    }
}

class SatisfiabilityBeliefRevisionStrategy():BeliefRevisionStrategy
{
    override val friendlyName:String get() = "satisfiability"
    override fun revise(beliefState:Set<Proposition>,sentence:Proposition):Set<Proposition>
    {
        return beliefState.plus(sentence).filter {(it and sentence).isSatisfiable}.toSet()
    }
    override fun hashCode():Int = 0
    override fun equals(other:Any?):Boolean
    {
        return other is SatisfiabilityBeliefRevisionStrategy
    }
}
