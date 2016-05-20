package research2016.propositionallogic

import org.junit.Test

/**
 * Created by surpl on 5/4/2016.
 */
class PropositionsTest
{
    @Test
    fun toStringTest1()
    {
        val generatedString = Oif(Or(Not(BasicProposition.make("p")),BasicProposition.make("q")),BasicProposition.make("r")).toString()
        val expectedString = "(((¬p)∨q)→r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest2()
    {
        val generatedString = Iff(And(BasicProposition.make("p"),BasicProposition.make("q")),BasicProposition.make("r")).toString()
        val expectedString = "((p∧q)↔r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest3()
    {
        val generatedString = Xor(Nand(BasicProposition.make("p"),BasicProposition.make("q")),BasicProposition.make("r")).toString()
        val expectedString = "((p|q)⊕r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun basicPropositionsFromPropositionTest1()
    {
        val proposition = Oif(Or(Not(BasicProposition.make("p")),BasicProposition.make("q")),BasicProposition.make("r"))
        val actualResult = proposition.basicPropositions.map {it.friendly}.toSet()
        val expectedResult = setOf(BasicProposition.make("p"),BasicProposition.make("q"),BasicProposition.make("r")).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"$actualResult != $expectedResult"})
    }

    @Test
    fun basicPropositionsFromPropositionTest2()
    {
        val proposition = Oif(And(Oif(BasicProposition.make("x"),And(BasicProposition.make("y"),BasicProposition.make("z"))),Not(BasicProposition.make("y"))),Not(BasicProposition.make("x")))
        val actualResult = proposition.basicPropositions.map {it.friendly}.toSet()
        val expectedResult = setOf(BasicProposition.make("x"),BasicProposition.make("y"),BasicProposition.make("z")).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"basicPropositionsFromPropositionTest2 failed: $actualResult != $expectedResult"})
    }

    @Test
    fun evaluateTest()
    {
        val proposition = Oif(And(Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r"))),Not(BasicProposition.make("q"))),Not(BasicProposition.make("p")))
        val allSituations = setOf(
            Situation.make(mapOf("p" to false,"q" to false,"r" to false)),
            Situation.make(mapOf("p" to false,"q" to false,"r" to true )),
            Situation.make(mapOf("p" to false,"q" to true ,"r" to false)),
            Situation.make(mapOf("p" to false,"q" to true ,"r" to true )),
            Situation.make(mapOf("p" to true ,"q" to false,"r" to false)),
            Situation.make(mapOf("p" to true ,"q" to false,"r" to true )),
            Situation.make(mapOf("p" to true ,"q" to true ,"r" to false)),
            Situation.make(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        allSituations.forEach()
        {
            assert(proposition.evaluate(it),{"evaluateTest failed. every situation should have evaluated to true, but situation $it evaluated to false"})
        }
    }

    @Test
    fun modelsOfPropositionTest()
    {
        val proposition = Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r")))
        val models = setOf(
            Situation.make(mapOf("p" to false,"q" to false,"r" to false)),
            Situation.make(mapOf("p" to false,"q" to false,"r" to true )),
            Situation.make(mapOf("p" to false,"q" to true ,"r" to false)),
            Situation.make(mapOf("p" to false,"q" to true ,"r" to true )),
            Situation.make(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models,{"${proposition.models.trueSituations} != $models"})
    }

    @Test
    fun modelsOfTreeWithTautologyTest()
    {
        val proposition = Oif(BasicProposition.make("p"),Or(BasicProposition.make("q"),Tautology))
        val models = setOf(
            Situation.make(mapOf("p" to false,"q" to false)),
            Situation.make(mapOf("p" to false,"q" to true )),
            Situation.make(mapOf("p" to true ,"q" to false)),
            Situation.make(mapOf("p" to true ,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models)
    }

    @Test
    fun modelsOfTreeWithContradictionTest()
    {
        val proposition = Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),Contradiction))
        val models = setOf(
            Situation.make(mapOf("p" to false,"q" to false)),
            Situation.make(mapOf("p" to false,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models)
    }

    val bigProposition1 = Xor(Oif(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Nand(Xor(Not(Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r")))),BasicProposition.make("s")),BasicProposition.make("t")),BasicProposition.make("u")),BasicProposition.make("v")),BasicProposition.make("w")),BasicProposition.make("x")),BasicProposition.make("y")),BasicProposition.make("z")),BasicProposition.make("a")),BasicProposition.make("b")),BasicProposition.make("c")),BasicProposition.make("d"))
    val bigProposition2 = Xor(Xor(Oif(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Nand(Xor(Not(Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r")))),BasicProposition.make("s")),BasicProposition.make("t")),BasicProposition.make("u")),BasicProposition.make("v")),BasicProposition.make("w")),BasicProposition.make("x")),BasicProposition.make("y")),BasicProposition.make("z")),BasicProposition.make("a")),BasicProposition.make("b")),BasicProposition.make("c")),BasicProposition.make("d")),BasicProposition.make("e"))

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
        Situation.generateFrom(proposition.basicPropositions).partition {proposition.evaluate(it)}
    }

    fun allModels(proposition:Proposition)
    {
        proposition.models
    }

    @Test
    fun modelsOfContradiction()
    {
        val contradiction = And(Tautology,And(And(BasicProposition.make("a"),BasicProposition.make("b")),Not(BasicProposition.make("b"))))
        println(contradiction)
        assert(contradiction.models.trueSituations.isEmpty())
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest1()
    {
        val proposition = Not(BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest2()
    {
        val proposition = And(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest3()
    {
        val proposition = Nand(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest4()
    {
        val proposition = Or(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest5()
    {
        val proposition = Xor(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest6()
    {
        val proposition = Iff(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest7()
    {
        val proposition = Oif(BasicProposition.make("q"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest8()
    {
        val proposition = And(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest9()
    {
        val proposition = Nand(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest10()
    {
        val proposition = Or(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest11()
    {
        val proposition = Xor(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest12()
    {
        val proposition = Iff(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest13()
    {
        val proposition = Oif(BasicProposition.make("p"),BasicProposition.make("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun isSatisfiableOfContradictionIsFalse()
    {
        assert(!And(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isSatisfiable)
    }

    @Test
    fun isSatisfiableOfPropositionIsTrue()
    {
        assert(BasicProposition.make("a").isSatisfiable)
    }

    @Test
    fun isSatisfiableOfTautologyIsTrue()
    {
        assert(Or(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isSatisfiable)
    }

    @Test
    fun makePropositionsFromSituationsTest()
    {
        val models = Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r"))).models
        assert(models.trueSituations.map {Proposition.makeFrom(it)}.fold<Proposition,Proposition?>(null) {initial,next -> initial?.let {Or(initial,next)} ?: next}?.models == models)
    }

    @Test
    fun isTautologyOfContradictionIsFalse()
    {
        assert(!And(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isTautology)
    }

    @Test
    fun isTautologyOfPropositionIsFalse()
    {
        assert(!BasicProposition.make("a").isTautology)
    }

    @Test
    fun isTautologyOfTautologyIsTrue()
    {
        assert(Or(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isTautology)
    }

    @Test
    fun isContradictionOfContradictionIsTrue()
    {
        assert(And(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isContradiction)
    }

    @Test
    fun isContradictionOfPropositionIsFalse()
    {
        assert(!BasicProposition.make("a").isContradiction)
    }

    @Test
    fun isContradictionOfTautologyIsFalse()
    {
        assert(!Or(BasicProposition.make("a"),Not(BasicProposition.make("a"))).isContradiction)
    }

    @Test
    fun makeRandom0()
    {
        val basicPropositions = emptyList<BasicProposition>()
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
        val basicPropositions = listOf(BasicProposition.make("a"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }

    @Test
    fun makeRandom2()
    {
        val basicPropositions = listOf(BasicProposition.make("a"),BasicProposition.make("b"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }

    @Test
    fun makeRandom3()
    {
        val basicPropositions = listOf(BasicProposition.make("a"),BasicProposition.make("b"),BasicProposition.make("c"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }

    @Test
    fun makeRandom4()
    {
        val basicPropositions = listOf(BasicProposition.make("a"),BasicProposition.make("b"),BasicProposition.make("c"),BasicProposition.make("d"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }

    @Test
    fun makeRandom5()
    {
        val basicPropositions = listOf(BasicProposition.make("a"),BasicProposition.make("b"),BasicProposition.make("c"),BasicProposition.make("d"),BasicProposition.make("e"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }

    @Test
    fun makeRandom6()
    {
        val basicPropositions = listOf(BasicProposition.make("a"),BasicProposition.make("b"),BasicProposition.make("c"),BasicProposition.make("d"),BasicProposition.make("e"),BasicProposition.make("f"))
        val proposition = Proposition.makeRandom(basicPropositions)
        println(proposition)
        assert(proposition.basicPropositions.size == basicPropositions.size)
    }
}
