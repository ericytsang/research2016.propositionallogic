package research2016.propositionallogic.core

import research2016.propositionallogic.core.Proposition.AtomicProposition
import research2016.propositionallogic.core.Proposition.Operator

/**
 * Created by surpl on 5/4/2016.
 */
class BasicProposition(_friendly:String):AtomicProposition(_friendly)
{
    init
    {
        assert(_friendly.length == 1,{"only strings of length 1 are allowed to be used as the friendly string for atomic propositions"})
        assert(_friendly[0].isLetter(),{"atomic proposition must be a letter"})
    }
    override fun truthValue(situation:Situation):Boolean = situation.getValue(this)
}

class Tautology:AtomicProposition("1")
{
    override fun truthValue(situation:Situation):Boolean = true
}

class Contradiction:AtomicProposition("0")
{
    override fun truthValue(situation:Situation):Boolean = false
}

abstract class UnaryOperator(val operand:Proposition,val friendly:String,truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(operand),truthTable)
{
    override fun toString():String = "($friendly$operand)"
}

abstract class BinaryOperator(val leftOperand:Proposition,val rightOperand:Proposition,val friendly:String,truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(leftOperand,rightOperand),truthTable)
{
    override fun toString():String = "($leftOperand$friendly$rightOperand)"
}

class Not(operand:Proposition):UnaryOperator(operand,"¬",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(true ) to false,
            listOf(false) to true
        )
    }
}

class And(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∧",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to false,
            listOf(false,true ) to false,
            listOf(true ,false) to false,
            listOf(true ,true ) to true
        )
    }
}

class Or(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∨",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to false,
            listOf(false,true ) to true,
            listOf(true ,false) to true,
            listOf(true ,true ) to true
        )
    }
}

class Oif(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"→",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to true,
            listOf(false,true ) to true,
            listOf(true ,false) to false,
            listOf(true ,true ) to true
        )
    }
}

class Iff(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"↔",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to true,
            listOf(false,true ) to false,
            listOf(true ,false) to false,
            listOf(true ,true ) to true
        )
    }
}

class Xor(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"⊕",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to false,
            listOf(false,true ) to true,
            listOf(true ,false) to true,
            listOf(true ,true ) to false
        )
    }
}

class Nand(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"|",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(false,false) to true,
            listOf(false,true ) to true,
            listOf(true ,false) to true,
            listOf(true ,true ) to false
        )
    }
}
