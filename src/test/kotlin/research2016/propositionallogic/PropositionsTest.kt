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
    fun modelsTest()
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

    @Test
    fun modelsOfBigTree()
    {
        val proposition = Xor(Xor(Xor(Oif(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Iff(Nand(Xor(Not(Oif(BasicProposition.make("p"),And(BasicProposition.make("q"),BasicProposition.make("r")))),BasicProposition.make("s")),BasicProposition.make("t")),BasicProposition.make("u")),BasicProposition.make("v")),BasicProposition.make("w")),BasicProposition.make("x")),BasicProposition.make("y")),BasicProposition.make("z")),BasicProposition.make("a")),BasicProposition.make("b")),BasicProposition.make("c")),BasicProposition.make("d")),BasicProposition.make("e")),BasicProposition.make("f"))
        proposition.models
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
}
