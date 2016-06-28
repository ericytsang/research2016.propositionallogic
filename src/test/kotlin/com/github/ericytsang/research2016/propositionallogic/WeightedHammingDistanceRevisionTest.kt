package com.github.ericytsang.research2016.propositionallogic

import org.junit.Test

/**
 * Created by surpl on 5/15/2016.
 */
class WeightedHammingDistanceRevisionTest():RevisionTest()
{
    /**
     * test case where sentence models are a subset of belief state models.
     */
    @Test
    fun reviseSubSetTest()
    {
        val beliefState = setOf(tautology)
        val sentence = p and q and r
        val expected = (p and q and r).models
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({WeightedHammingDistanceComparator(it,mapOf(p to 3,q to 2,r to 1))}),expected)
    }

    /**
     * test case where sentence models intersect with belief state models.
     */
    @Test
    fun reviseIntersectTest()
    {
        val beliefState = setOf(p or q)
        val sentence = q or r
        val expected = ((p or q) and (q or r)).models
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({WeightedHammingDistanceComparator(it,mapOf(p to 3,q to 2,r to 1))}),expected)
    }

    /**
     * test case where sentence models don't intersect with belief state models.
     */
    @Test
    fun reviseContradictionTest()
    {
        val beliefState = setOf(p and q)
        val sentence = And.make(beliefState)!!.not
        val expected = (p and q.not).models
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({WeightedHammingDistanceComparator(it,mapOf(p to 3,q to 2,r to 1))}),expected)
    }
}
