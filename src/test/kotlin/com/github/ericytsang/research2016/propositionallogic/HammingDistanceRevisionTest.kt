package com.github.ericytsang.research2016.propositionallogic

import org.junit.Test

/**
 * Created by surpl on 5/15/2016.
 */
class HammingDistanceRevisionTest():RevisionTest()
{
    /**
     * test case where sentence models are a subset of belief state models.
     */
    @Test
    fun reviseSubSetTest()
    {
        val beliefState = setOf(Proposition.TAUTOLOGY)
        val sentence = p and q and r
        val expected = (p and q and r).models
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}),expected)
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
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}),expected)
    }

    /**
     * test case where sentence models don't intersect with belief state models.
     */
    @Test
    fun reviseContradictionTest()
    {
        val beliefState = setOf(p and q)
        val sentence = And.make(beliefState.toList())!!.not
        val expected = Xor(p,q).models
        reviseTest(beliefState,sentence,ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}),expected)
    }
}
