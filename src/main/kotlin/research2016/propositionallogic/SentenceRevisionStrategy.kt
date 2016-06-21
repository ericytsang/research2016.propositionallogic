package research2016.propositionallogic

/**
 * Created by surpl on 6/13/2016.
 */
interface SentenceRevisionStrategy
{
    fun revise(sentence:Proposition):Proposition
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
            .plus(Or.make(partitions.toList()).not)
            // get the intersecting partitions
            .filter {(sentence and it).isSatisfiable}
            // take the union of them by or-ing them together and return that
            .let {Or.make(it)}
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
}

val noTrustSentenceRevisionStrategy = PartitionedTrustSentenceRevisionStrategy(setOf(tautology))

class CompleteTrustSentenceRevisionStrategy:TrustPartitionSentenceRevisionStrategy()
{
    override fun partitions(sentence:Proposition):Set<Proposition>
    {
        return setOf(sentence)
    }
}
