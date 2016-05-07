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
        val generatedString = Oif(Or(Not(BasicProposition("p")),BasicProposition("q")),BasicProposition("r")).toString()
        val expectedString = "(((¬p)∨q)→r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest2()
    {
        val generatedString = Iff(And(BasicProposition("p"),BasicProposition("q")),BasicProposition("r")).toString()
        val expectedString = "((p∧q)↔r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun toStringTest3()
    {
        val generatedString = Xor(Nand(BasicProposition("p"),BasicProposition("q")),BasicProposition("r")).toString()
        val expectedString = "((p|q)⊕r)"
        assert(expectedString == generatedString,{"expectedString: \"$expectedString\"; generatedString: \"$generatedString\""})
    }

    @Test
    fun basicPropositionsFromPropositionTest1()
    {
        val proposition = Oif(Or(Not(BasicProposition("p")),BasicProposition("q")),BasicProposition("r"))
        val actualResult = proposition.basicPropositions.map {it.friendly}.toSet()
        val expectedResult = setOf(BasicProposition("p"),BasicProposition("q"),BasicProposition("r")).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"$actualResult != $expectedResult"})
    }

    @Test
    fun basicPropositionsFromPropositionTest2()
    {
        val proposition = Oif(And(Oif(BasicProposition("x"),And(BasicProposition("y"),BasicProposition("z"))),Not(BasicProposition("y"))),Not(BasicProposition("x")))
        val actualResult = proposition.basicPropositions.map {it.friendly}.toSet()
        val expectedResult = setOf(BasicProposition("x"),BasicProposition("y"),BasicProposition("z")).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"basicPropositionsFromPropositionTest2 failed: $actualResult != $expectedResult"})
    }

    @Test
    fun evaluateTest()
    {
        val proposition = Oif(And(Oif(BasicProposition("p"),And(BasicProposition("q"),BasicProposition("r"))),Not(BasicProposition("q"))),Not(BasicProposition("p")))
        val allSituations = setOf(
            Situation(mapOf("p" to false,"q" to false,"r" to false)),
            Situation(mapOf("p" to false,"q" to false,"r" to true )),
            Situation(mapOf("p" to false,"q" to true ,"r" to false)),
            Situation(mapOf("p" to false,"q" to true ,"r" to true )),
            Situation(mapOf("p" to true ,"q" to false,"r" to false)),
            Situation(mapOf("p" to true ,"q" to false,"r" to true )),
            Situation(mapOf("p" to true ,"q" to true ,"r" to false)),
            Situation(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        allSituations.forEach()
        {
            assert(proposition.evaluate(it),{"evaluateTest failed. every situation should have evaluated to true, but situation $it evaluated to false"})
        }
    }

    @Test
    fun modelsTest()
    {
        val proposition = Oif(BasicProposition("p"),And(BasicProposition("q"),BasicProposition("r")))
        val models = setOf(
            Situation(mapOf("p" to false,"q" to false,"r" to false)),
            Situation(mapOf("p" to false,"q" to false,"r" to true )),
            Situation(mapOf("p" to false,"q" to true ,"r" to false)),
            Situation(mapOf("p" to false,"q" to true ,"r" to true )),
            Situation(mapOf("p" to true ,"q" to true ,"r" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models,{"${proposition.models.trueSituations} != $models"})
    }

    @Test
    fun modelsOfTreeWithTautologyTest()
    {
        val proposition = Oif(BasicProposition("p"),Or(BasicProposition("q"),Tautology()))
        val models = setOf(
            Situation(mapOf("p" to false,"q" to false)),
            Situation(mapOf("p" to false,"q" to true )),
            Situation(mapOf("p" to true ,"q" to false)),
            Situation(mapOf("p" to true ,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models)
    }

    @Test
    fun modelsOfTreeWithContradictionTest()
    {
        val proposition = Oif(BasicProposition("p"),And(BasicProposition("q"),Contradiction()))
        val models = setOf(
            Situation(mapOf("p" to false,"q" to false)),
            Situation(mapOf("p" to false,"q" to true ))
        )
        println(proposition)
        println(proposition.models)
        assert(proposition.models.trueSituations == models)
    }

    @Test
    fun modelsOfBigTree()
    {
        val proposition = Xor(Xor(Xor(Oif(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Nand(Xor(Not(Oif(BasicProposition("p"),And(BasicProposition("q"),BasicProposition("r")))),BasicProposition("s")),BasicProposition("t")),BasicProposition("u")),BasicProposition("v")),BasicProposition("w")),BasicProposition("x")),BasicProposition("y")),BasicProposition("z")),BasicProposition("a")),BasicProposition("b")),BasicProposition("c")),BasicProposition("d")),BasicProposition("e")),BasicProposition("f"))
        proposition.models
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest1()
    {
        val proposition = Not(BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest2()
    {
        val proposition = And(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest3()
    {
        val proposition = Nand(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest4()
    {
        val proposition = Or(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest5()
    {
        val proposition = Xor(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest6()
    {
        val proposition = Iff(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }

    @Test
    fun modelsCrossCheckWithEvaluateTest7()
    {
        val proposition = Oif(BasicProposition("q"),BasicProposition("q"))
        val models = proposition.models
        models.trueSituations.forEach {assert(proposition.evaluate(it))}
        models.falseSituations.forEach {assert(!proposition.evaluate(it))}
    }
}
