package com.github.ericytsang.research2016.propositionallogic

import org.junit.Test

class PropositionsTest
{
    val p:Variable = Variable.fromString("p")
    val q:Variable = Variable.fromString("q")
    val r:Variable = Variable.fromString("r")
    val x:Variable = Variable.fromString("x")
    val y:Variable = Variable.fromString("y")
    val z:Variable = Variable.fromString("z")
    val s:Variable = Variable.fromString("s")
    val t:Variable = Variable.fromString("t")
    val u:Variable = Variable.fromString("u")
    val v:Variable = Variable.fromString("v")
    val w:Variable = Variable.fromString("w")
    val a:Variable = Variable.fromString("a")
    val b:Variable = Variable.fromString("b")
    val c:Variable = Variable.fromString("c")
    val d:Variable = Variable.fromString("d")
    val e:Variable = Variable.fromString("e")
    val f:Variable = Variable.fromString("f")
    val g:Variable = Variable.fromString("g")
    val h:Variable = Variable.fromString("h")
    val i:Variable = Variable.fromString("i")
    val j:Variable = Variable.fromString("j")
    val k:Variable = Variable.fromString("k")
    val l:Variable = Variable.fromString("l")
    val m:Variable = Variable.fromString("m")
    val n:Variable = Variable.fromString("n")
    val o:Variable = Variable.fromString("o")

    val Proposition.not:Proposition get() = not()
    infix fun Proposition.or(that:Proposition):Proposition = or(that)
    infix fun Proposition.and(that:Proposition):Proposition = and(that)

    @Test
    fun toStringTest1()
    {
        val generatedString = ((p.not or q) and r).toString()
        val expectedString = "(¬p∨q)∧r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest2()
    {
        val generatedString = ((p and q)or r.not).toString()
        val expectedString = "(p∧q)∨¬r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest4()
    {
        val generatedString = (p and q and r).toString()
        val expectedString = "p∧q∧r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest5()
    {
        val generatedString = (p or q or r).toString()
        val expectedString = "p∨q∨r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest6()
    {
        val generatedString = ((p or q or r)and(p.not or q or r)and(p.not or q.not or r)and(p.not or q.not or r.not)).toString()
        val expectedString = "(p∨q∨r)∧(¬p∨q∨r)∧(¬p∨¬q∨r)∧(¬p∨¬q∨¬r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun basicPropositionsFromPropositionTest1()
    {
        val proposition = ((p.not or q)and r)
        val actualResult = proposition.variables.map {it.friendly}.toSet()
        val expectedResult = setOf(p,q,r).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"$actualResult != $expectedResult"})
    }

    @Test
    fun basicPropositionsFromPropositionTest2()
    {
        val proposition = (((x and(y and z))and y.not)or x.not)
        val actualResult = proposition.variables.map {it.friendly}.toSet()
        val expectedResult = setOf(x,y,z).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"basicPropositionsFromPropositionTest2 failed: $actualResult != $expectedResult"})
    }

    @Test
    fun modelsOfPropositionTest()
    {
        val proposition = (p or(q and r))
        val models = setOf(
            State.fromStringMap(mapOf("p" to false,"q" to true ,"r" to true )),
            State.fromStringMap(mapOf("p" to true ,"q" to true ,"r" to true )),
            State.fromStringMap(mapOf("p" to true ,"q" to true ,"r" to false)),
            State.fromStringMap(mapOf("p" to true ,"q" to false,"r" to false)),
            State.fromStringMap(mapOf("p" to true ,"q" to false,"r" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models,{"${proposition.models} != $models"})
    }

    @Test
    fun modelsOfTreeWithTautologyTest()
    {
        val proposition = And.make(p,(q or Proposition.TAUTOLOGY))
        val models = setOf(
            State.fromStringMap(mapOf("p" to true ,"q" to false)),
            State.fromStringMap(mapOf("p" to true ,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models)
    }

    @Test
    fun modelsOfTreeWithContradictionTest()
    {
        val proposition = Or.make(p,(q and Proposition.CONTRADICTION))
        val models = setOf(
            State.fromStringMap(mapOf("p" to true,"q" to false)),
            State.fromStringMap(mapOf("p" to true,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models)
    }

    val bigProposition1 = And.make(And.make(And.make(listOf(Or.make(listOf(And.make(listOf(Or.make(listOf(And.make(listOf(Or.make(listOf(And.make(listOf(Or.make(listOf(And.make(And.make(Not(Or.make(p,(q and r))),s),t),u))!!,v))!!,w))!!,x))!!,y))!!,z))!!,a))!!,b))!!,c),d)
    val bigProposition2 = Or.make(Or.make(Or.make(Or.make(listOf(Or.make(listOf(Or.make(listOf(Or.make(listOf(Or.make(listOf(Or.make(listOf(Or.make(listOf(Or.make(listOf(And.make(Or.make(Not(Or.make(p,(q and r))),s),t),u))!!,v))!!,w))!!,x))!!,y))!!,z))!!,a))!!,b))!!,c),d),e)

    @Test
    fun benchmark_evaluateProp1()
    {
        evaluateEach(bigProposition1)
    }

    @Test
    fun benchmark_evaluateProp2()
    {
        evaluateEach(bigProposition2)
    }

    @Test
    fun benchmark_modelProp1()
    {
        allModels(bigProposition1)
    }

    @Test
    fun benchmark_modelProp2()
    {
        allModels(bigProposition2)
    }

    @Test
    fun benchmark_printModelsProp1()
    {
        println(bigProposition1.models)
    }

    @Test
    fun benchmark_printModelsProp2()
    {
        println(bigProposition2.models)
    }

    fun evaluateEach(proposition:Proposition)
    {
        State.permutationsOf(proposition.variables).partition {proposition.evaluate(it)}
    }

    fun allModels(proposition:Proposition)
    {
        proposition.models
    }

    @Test
    fun modelsOfContradiction()
    {
        val contradiction = (Proposition.TAUTOLOGY and a and b and b.not)
        println(contradiction)
        assert(contradiction.models.isEmpty())
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest1()
    {
        val proposition = Not(q)
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest2()
    {
        val proposition = q and q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest4()
    {
        val proposition = q or q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest8()
    {
        val proposition = p and q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest10()
    {
        val proposition = p or q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun makePropositionsFromSituationsTest()
    {
        val models = (p.not or(q and r)).models
        assert(models.map {Proposition.makeDnf(it)}.fold<Proposition,Proposition?>(null) {initial,next -> initial?.let {(initial or next)} ?: next}?.models == models)
    }

    @Test
    fun isSatisfiableOfContradictionIsFalse()
    {
        assert(!(a and Not(a)).isSatisfiable)
    }

    @Test
    fun isSatisfiableOfPropositionIsTrue()
    {
        assert(a.isSatisfiable)
    }

    @Test
    fun isSatisfiableOfTautologyIsTrue()
    {
        assert((a or a.not).isSatisfiable)
    }

    @Test
    fun isTautologyOfContradictionIsFalse()
    {
        assert(!(a and Not(a)).isTautology)
    }

    @Test
    fun isTautologyOfPropositionIsFalse()
    {
        assert(!a.isTautology)
    }

    @Test
    fun isTautologyOfTautologyIsTrue()
    {
        assert((a or Not(a)).isTautology)
    }

    @Test
    fun isContradictionOfContradictionIsTrue()
    {
        assert((a and Not(a)).isContradiction)
    }

    @Test
    fun isContradictionOfPropositionIsFalse()
    {
        assert(!a.isContradiction)
    }

    @Test
    fun isContradictionOfTautologyIsFalse()
    {
        assert(!(a or a.not).isContradiction)
    }
}
