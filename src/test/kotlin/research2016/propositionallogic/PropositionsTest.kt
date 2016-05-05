package research2016.propositionallogic

import org.junit.Test
import research2016.propositionallogic.core.And
import research2016.propositionallogic.core.BasicProposition
import research2016.propositionallogic.core.Iff
import research2016.propositionallogic.core.Nand
import research2016.propositionallogic.core.Not
import research2016.propositionallogic.core.Oif
import research2016.propositionallogic.core.Or
import research2016.propositionallogic.core.Situation
import research2016.propositionallogic.core.Xor
import research2016.propositionallogic.core.basicPropositions
import research2016.propositionallogic.core.evaluate
import research2016.propositionallogic.core.generateFrom

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
        val asd = proposition.basicPropositions
        val asdadsasd = proposition.basicPropositions
        proposition.basicPropositions
        proposition.basicPropositions
        proposition.basicPropositions
        proposition.basicPropositions
        val actualResult = proposition.basicPropositions.map {it.friendly}.toSet()
        val expectedResult = setOf(BasicProposition("x"),BasicProposition("y"),BasicProposition("z")).map {it.friendly}.toSet()
        assert(actualResult == expectedResult,{"$actualResult != $expectedResult"})
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
}
