package research2016.propositionallogic

import lib.collections.permutedIterator
import lib.collections.toIterable
import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator
import java.util.Arrays
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

abstract class UnaryOperator(val operand:Proposition,val friendly:String,override val truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(operand))
{
    init
    {
        assert(truthTable.keys.all {it.size == operands.size})
        {
            throw IllegalArgumentException("length of list for truth table keys should match length of list of operands. truth table: $truthTable, operands: $operands")
        }
    }

    override fun toString():String
    {
        if (operand.children.size > 1)
        {
            return "$friendly($operand)"
        }
        else
        {
            return "$friendly$operand"
        }
    }
}

abstract class BinaryOperator(val leftOperand:Proposition,val rightOperand:Proposition,val friendly:String,override val truthTable:Map<List<Boolean>,Boolean>):Operator(listOf(leftOperand,rightOperand))
{
    init
    {
        assert(truthTable.keys.all {it.size == operands.size})
        {
            throw IllegalArgumentException("length of list for truth table keys should match length of list of operands. truth table: $truthTable, operands: $operands")
        }
    }

    override fun toString():String
    {
        return operands.map {if (it.children.size > 1) "($it)" else "$it"}.joinToString(friendly)
    }
}

abstract class AssociativeOperator(operands:List<Proposition>,val friendly:String):Operator(operands)
{
    abstract override fun operate(operands:List<Boolean>):Boolean

    override val truthTable:Map<List<Boolean>,Boolean> by lazy()
    {
        val truthValues = listOf(true,false)
        val mapKeys = Array(operands.size,{truthValues}).toList().permutedIterator().toIterable()
        mapKeys.associate {it to operate(it)}
    }

    override fun toString():String
    {
        return operands.map {if (it.children.size > 1) "($it)" else "$it"}.joinToString(friendly)
    }
}

val Proposition.not:Not get() = Not(this)

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

infix fun Proposition.and(other:Proposition) = And.make(listOf(this,other))

class And private constructor(operands:List<Proposition>):AssociativeOperator(operands,"∧")
{
    companion object
    {
        fun make(operands:List<Proposition>):And
        {
            val newOperands = operands.flatMap()
            {
                if (it is And)
                {
                    it.operands
                }
                else
                {
                    listOf(it)
                }
            }
            return And(newOperands)
        }
    }

    override fun operate(operands:List<Boolean>):Boolean
    {
        return operands.all {it}
    }
}

infix fun Proposition.or(other:Proposition) = Or.make(listOf(this,other))

class Or private constructor(operands:List<Proposition>):AssociativeOperator(operands,"∨")
{
    companion object
    {
        fun make(operands:List<Proposition>):Or
        {
            val newOperands = operands.flatMap()
            {
                if (it is Or)
                {
                    it.operands
                }
                else
                {
                    listOf(it)
                }
            }
            return Or(newOperands)
        }
    }

    override fun operate(operands:List<Boolean>):Boolean
    {
        return operands.any {it}
    }
}

infix fun Proposition.oif(other:Proposition) = Oif(this,other)

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

infix fun Proposition.iff(other:Proposition) = Iff(this,other)

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

infix fun Proposition.xor(other:Proposition) = Xor(this,other)

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

infix fun Proposition.nand(other:Proposition) = Nand(this,other)

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
