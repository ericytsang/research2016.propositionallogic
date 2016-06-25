package com.github.ericytsang.research2016.propositionallogic

/**
 * Created by Eric on 5/25/2016.
 */
open class RevisionTest
{
    protected val p = Variable.make("p")
    protected val q = Variable.make("q")
    protected val r = Variable.make("r")

    protected fun reviseTest(beliefState:Set<Proposition>,sentence:Proposition,beliefRevisionStrategy:BeliefRevisionStrategy,expected:Set<State>)
    {
        val actual = beliefRevisionStrategy
            .revise(beliefState,sentence)
            .let {And.make(it.toList())}
            .models
        println("actual: $actual")
        println("expected: $expected")
        assert(actual == expected)
    }
}
