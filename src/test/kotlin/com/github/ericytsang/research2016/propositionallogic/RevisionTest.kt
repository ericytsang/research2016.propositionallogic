package com.github.ericytsang.research2016.propositionallogic

/**
 * Created by Eric on 5/25/2016.
 */
open class RevisionTest
{
    val Proposition.not:Proposition get() = not()
    infix fun Proposition.or(that:Proposition):Proposition = or(that)
    infix fun Proposition.and(that:Proposition):Proposition = and(that)
    infix fun Proposition.oif(that:Proposition):Proposition = oif(that)
    infix fun Proposition.iff(that:Proposition):Proposition = iff(that)
    infix fun Proposition.xor(that:Proposition):Proposition = xor(that)
    infix fun Proposition.nand(that:Proposition):Proposition = nand(that)

    protected val p = Variable.fromString("p")
    protected val q = Variable.fromString("q")
    protected val r = Variable.fromString("r")

    protected fun reviseTest(beliefState:Set<Proposition>,sentence:Proposition,beliefRevisionStrategy:BeliefRevisionStrategy,expected:Set<State>)
    {
        val actual = beliefRevisionStrategy
            .revise(beliefState,sentence)
            .let {And.make(it.toList()) ?: Proposition.CONTRADICTION}
            .models
        println("actual: $actual")
        println("expected: $expected")
        assert(actual == expected)
    }
}
