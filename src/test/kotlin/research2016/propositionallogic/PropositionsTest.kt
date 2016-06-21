package research2016.propositionallogic

import org.junit.Test

/**
 * Created by surpl on 5/4/2016.
 */
class PropositionsTest
{
    val p = Variable.make("p")
    val q = Variable.make("q")
    val r = Variable.make("r")
    val x = Variable.make("x")
    val y = Variable.make("y")
    val z = Variable.make("z")
    val s = Variable.make("s")
    val t = Variable.make("t")
    val u = Variable.make("u")
    val v = Variable.make("v")
    val w = Variable.make("w")
    val a = Variable.make("a")
    val b = Variable.make("b")
    val c = Variable.make("c")
    val d = Variable.make("d")
    val e = Variable.make("e")
    val f = Variable.make("f")
    val g = Variable.make("g")
    val h = Variable.make("h")
    val i = Variable.make("i")
    val j = Variable.make("j")
    val k = Variable.make("k")
    val l = Variable.make("l")
    val m = Variable.make("m")
    val n = Variable.make("n")
    val o = Variable.make("o")

    @Test
    fun toStringTest1()
    {
        val generatedString = ((p.not or q) oif r).toString()
        val expectedString = "(¬p∨q)→r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest2()
    {
        val generatedString = ((p and q)iff r).toString()
        val expectedString = "(p∧q)↔r"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest3()
    {
        val generatedString = ((p nand q)xor r).toString()
        val expectedString = "(p|q)⊕r"
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
        val proposition = ((p.not or q)oif r)
        val actualResult = proposition.variables.map {it.friendly}.toSet()
        val expectedResult = setOf(p,q,r).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"$actualResult != $expectedResult"})
    }

    @Test
    fun basicPropositionsFromPropositionTest2()
    {
        val proposition = (((x oif(y and z))and y.not)oif x.not)
        val actualResult = proposition.variables.map {it.friendly}.toSet()
        val expectedResult = setOf(x,y,z).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"basicPropositionsFromPropositionTest2 failed: $actualResult != $expectedResult"})
    }

    @Test
    fun evaluateTest()
    {
        val proposition = (((p oif(q and r))and q.not)oif p.not)
        val allSituations = setOf(
            State.make(mapOf("p" to false,"q" to false,"r" to false)),
            State.make(mapOf("p" to false,"q" to false,"r" to true )),
            State.make(mapOf("p" to false,"q" to true ,"r" to false)),
            State.make(mapOf("p" to false,"q" to true ,"r" to true )),
            State.make(mapOf("p" to true ,"q" to false,"r" to false)),
            State.make(mapOf("p" to true ,"q" to false,"r" to true )),
            State.make(mapOf("p" to true ,"q" to true ,"r" to false)),
            State.make(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        allSituations.forEach()
        {
            assert(proposition.evaluate(it),{"evaluateTest failed. every situation should have evaluated to true, but situation $it evaluated to false"})
        }
    }

    @Test
    fun modelsOfPropositionTest()
    {
        val proposition = (p oif(q and r))
        val models = setOf(
            State.make(mapOf("p" to false,"q" to false,"r" to false)),
            State.make(mapOf("p" to false,"q" to false,"r" to true )),
            State.make(mapOf("p" to false,"q" to true ,"r" to false)),
            State.make(mapOf("p" to false,"q" to true ,"r" to true )),
            State.make(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models,{"${proposition.models} != $models"})
    }

    @Test
    fun modelsOfTreeWithTautologyTest()
    {
        val proposition = Oif(p,(q or tautology))
        val models = setOf(
            State.make(mapOf("p" to false,"q" to false)),
            State.make(mapOf("p" to false,"q" to true )),
            State.make(mapOf("p" to true ,"q" to false)),
            State.make(mapOf("p" to true ,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models)
    }

    @Test
    fun modelsOfTreeWithContradictionTest()
    {
        val proposition = Oif(p,(q and contradiction))
        val models = setOf(
            State.make(mapOf("p" to false,"q" to false)),
            State.make(mapOf("p" to false,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models == models)
    }

    val bigProposition1 = Xor(Oif(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Nand(Xor(Not(Oif(p,(q and r))),s),t),u)),v)),w)),x)),y)),z)),a)),b)),c),d)
    val bigProposition2 = Xor(Xor(Oif(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Iff.make(listOf(Nand(Xor(Not(Oif(p,(q and r))),s),t),u)),v)),w)),x)),y)),z)),a)),b)),c),d),e)

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
        State.generateFrom(proposition.variables).partition {proposition.evaluate(it)}
    }

    fun allModels(proposition:Proposition)
    {
        proposition.models
    }

    @Test
    fun modelsOfContradiction()
    {
        val contradiction = (tautology and a and b and b.not)
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
    fun modelsCrossCheckWithEvaluateTest3()
    {
        val proposition = Nand(q,q)
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
    fun modelsCrossCheckWithEvaluateTest5()
    {
        val proposition = Xor(q,q)
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest6()
    {
        val proposition = q iff q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest7()
    {
        val proposition = Oif(q,q)
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
    fun modelsCrossCheckWithEvaluateTest9()
    {
        val proposition = Nand(p,q)
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
    fun modelsCrossCheckWithEvaluateTest11()
    {
        val proposition = p xor q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest12()
    {
        val proposition = p iff q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest13()
    {
        val proposition = p oif q
        val models = proposition.models
        val notModels = proposition.not.models
        models.forEach {assert(proposition.evaluate(it))}
        notModels.forEach {assert(!proposition.evaluate(it))}
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
    fun makePropositionsFromSituationsTest()
    {
        val models = Oif(p,(q and r)).models
        assert(models.map {Proposition.makeFrom(it)}.fold<Proposition,Proposition?>(null) {initial,next -> initial?.let {(initial or next)} ?: next}?.models == models)
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

    @Test
    fun makeRandom0()
    {
        val basicPropositions = emptyList<Variable>()
        try
        {
            println(Proposition.makeRandom(basicPropositions))
            assert(false,{"no exception thrown...was expecting an illegal argument exception"})
        }
        catch (ex:IllegalArgumentException)
        {
            // test passed
        }
    }

    @Test
    fun makeRandom1()
    {
        val basicPropositions = listOf(a)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun makeRandom2()
    {
        val basicPropositions = listOf(a,b)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun makeRandom3()
    {
        val basicPropositions = listOf(a,b,c)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun makeRandom4()
    {
        val basicPropositions = listOf(a,b,c,d)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun makeRandom5()
    {
        val basicPropositions = listOf(a,b,c,d,e)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun makeRandom6()
    {
        val basicPropositions = listOf(a,b,c,d,e,f)
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.variables.size == basicPropositions.size)
    }

    @Test
    fun toDnfTest1()
    {
        val proposition = a or b and c oif d
        println(proposition)
        assert(proposition.toDnf().models == proposition.models)
    }

    @Test
    fun toDnfTest2()
    {
        val proposition = a or a.not and b
        println(proposition.toDnf())
        assert(proposition.toDnf().models == b.models)
    }

    @Test
    fun toDnfTest3()
    {
        val proposition = a or a.not
        println(proposition.toDnf())
        assert(proposition.toDnf().models == tautology.models)
    }

    @Test
    fun toFullDnfTest()
    {
        val proposition = a or b and c oif d
        assert(proposition.toFullDnf().models == proposition.models)
    }

    @Test
    fun toParsableString()
    {
        val proposition = a and b or c xor d iff e nand f oif g.not
        assert(Proposition.makeFrom(proposition.toParsableString()) == proposition)
    }
}
