package research2016.propositionallogic

import org.junit.Test
import research2016.propositionallogic.core.And
import research2016.propositionallogic.core.BasicProposition
import research2016.propositionallogic.core.Oif
import research2016.propositionallogic.core.Proposition
import research2016.propositionallogic.interpreter.from

/**
 * Created by surpl on 5/5/2016.
 */
class InterpreterTest
{
    @Test
    fun toReversePolishNotationTest1()
    {
        assert(Proposition.from("p oif q and r") == Oif(BasicProposition("p"),And(BasicProposition("q"),BasicProposition("r"))))
    }

    @Test
    fun toReversePolishNotationTest2()
    {
        assert(Proposition.from("q and r oif p") == Oif(And(BasicProposition("q"),BasicProposition("r")),BasicProposition("p")))
    }
}
