package research2016.propositionallogic

import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator
import java.util.WeakHashMap

/**
 * Created by surpl on 5/4/2016.
 */
class BasicProposition private constructor(_friendly:String):AtomicProposition(_friendly)
{
    companion object
    {
        val allInstances = WeakHashMap<String,BasicProposition>()
        fun make(friendly:String) = allInstances.getOrPut(friendly,{BasicProposition(friendly)})
    }
    init
    {
        assert(_friendly.length >= 1,{"only strings of length 1 or longer are allowed to be used as the friendly string for atomic propositions"})
        assert(_friendly.all {it.isLetter()},{"atomic proposition must be composed only of letters"})
    }
    override fun truthValue(situation:Situation):Boolean = situation[this] ?: throw IllegalArgumentException("no value specified for given proposition ($friendly)")
    override val allSituations:Set<Situation> = setOf(Situation(mapOf(this to true)),Situation(mapOf(this to false)))
}

val Tautology = object:AtomicProposition("1")
{
    override fun truthValue(situation:Situation):Boolean = true
    override val allSituations:Set<Situation> = setOf(Situation(emptyMap()))
}

val Contradiction = object:AtomicProposition("0")
{
    override fun truthValue(situation:Situation):Boolean = false
    override val allSituations:Set<Situation> = setOf(Situation(emptyMap()))
}

abstract class UnaryOperator(val operand:Proposition,val friendly:String,truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(operand),truthTable)
{
    override fun toString():String = "($friendly$operand)"
}

abstract class BinaryOperator(val leftOperand:Proposition,val rightOperand:Proposition,val friendly:String,truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(leftOperand,rightOperand),truthTable)
{
    override fun toString():String = "($leftOperand$friendly$rightOperand)"
}

class Not(operand:Proposition):UnaryOperator(operand,"¬",truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            listOf(true ) to false,
            listOf(false) to true
        )
    }
}

class And(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∧",truthTable)
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

class Or(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∨",truthTable)
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

class Oif(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"→",truthTable)
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

class Iff(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"↔",truthTable)
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

class Xor(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"⊕",truthTable)
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

class Nand(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"|",truthTable)
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
