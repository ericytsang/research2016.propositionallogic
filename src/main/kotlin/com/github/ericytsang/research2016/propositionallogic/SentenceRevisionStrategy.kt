package com.github.ericytsang.research2016.propositionallogic

/**
 * Created by surpl on 6/13/2016.
 */
interface SentenceRevisionStrategy
{
    fun revise(sentence:Proposition):Proposition
    override fun hashCode():Int
    override fun equals(other:Any?):Boolean
}

abstract class TrustPartitionSentenceRevisionStrategy:SentenceRevisionStrategy
{
    protected abstract fun partitions(sentence:Proposition):Set<Proposition>

    final override fun revise(sentence:Proposition):Proposition
    {
        val partitions = partitions(sentence)
        if (partitions.isEmpty())
        {
            throw IllegalArgumentException("must define at least one partition")
        }
        return partitions
            // add partition that catches anything that does not satisfy defined partitions
            .plus((Or.make(partitions.toList())?:contradiction).not)
            // get the intersecting partitions
            .filter {(sentence and it).isSatisfiable}
            // take the union of them by or-ing them together and return that
            .let {Or.make(it)!!}
    }
}

class PartitionedTrustSentenceRevisionStrategy(val partitions:Set<Proposition>):TrustPartitionSentenceRevisionStrategy()
{
    init
    {
        if (partitions.isEmpty())
        {
            throw IllegalArgumentException("must define at least one partition")
        }
    }

    override fun partitions(sentence:Proposition):Set<Proposition>
    {
        return partitions
    }

    override fun hashCode():Int = partitions.hashCode()

    override fun equals(other:Any?):Boolean
    {
        return other is PartitionedTrustSentenceRevisionStrategy &&
            other.partitions == partitions
    }
}

val noTrustSentenceRevisionStrategy = PartitionedTrustSentenceRevisionStrategy(setOf(tautology))

class CompleteTrustSentenceRevisionStrategy:TrustPartitionSentenceRevisionStrategy()
{
    override fun partitions(sentence:Proposition):Set<Proposition>
    {
        return setOf(sentence)
    }

    override fun hashCode():Int = 0

    override fun equals(other:Any?):Boolean = other is CompleteTrustSentenceRevisionStrategy
}
