package research2016.propositionallogic

import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator
import java.util.WeakHashMap

/**
 * an [AtomicProposition] which is mapped to a truth value by a [State].
 */
class Variable private constructor(_friendly:String):AtomicProposition(_friendly)
{
    companion object
    {
        private val allInstances = WeakHashMap<String,Variable>()
        fun make(friendly:String) = allInstances.getOrPut(friendly,{Variable(friendly)})
    }
    init
    {
        if(_friendly.length < 1)
        {
            throw IllegalArgumentException("only strings of length 1 or longer are allowed to be used as the friendly string for atomic propositions")
        }
        if(_friendly.any {!it.isLetter()})
        {
            throw IllegalArgumentException("atomic proposition must be composed only of letters")
        }
    }
    override fun truthValue(state:State):Boolean = state[this] ?: throw IllegalArgumentException("no value specified for given proposition ($friendly)")
}

/**
 * an [AtomicProposition] that evaluates to true in any [State].
 */
val tautology = object:AtomicProposition("1")
{
    override fun truthValue(state:State):Boolean = true
}

/**
 * an [AtomicProposition] that evaluates to false in every [State].
 */
val contradiction = object:AtomicProposition("0")
{
    override fun truthValue(state:State):Boolean = false
}

abstract class TruthTableOperator(operands:List<Proposition>):Operator(operands)
{
    init
    {
        if(truthTable.keys.any {it.size != operands.size})
        {
            throw IllegalArgumentException("length of list for truth table keys should match length of list of operands. truth table: $truthTable, operands: $operands")
        }
    }

    abstract val truthTable:Map<List<Boolean>,Boolean>

    override fun operate(operands:List<Boolean>):Boolean = truthTable[operands]
        ?: throw IllegalArgumentException("truth table entry for operands not found. truth table may be missing an entry, or the number of provided operands is too much or too little for this operator. truth table: $truthTable, operands: $operands")

    override fun operate(operands:List<Double>):Double
    {
        return truthTable
            // rows to evaluate to true in the truth table
            .entries.filter {it.value}.map {it.key}
            // mapping each boolean to the truthiness of the operand
            .map {it.mapIndexed {i,b -> if (b) operands[i] else 1-operands[i] }}
            // multiplying all the doubles together for each list into one double
            .map {it.fold(1.0) {i,n -> i*n}}
            // sum...
            .sum()
    }
}

abstract class UnaryOperator(val operand:Proposition):TruthTableOperator(listOf(operand))
{
    abstract val friendly:String
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

abstract class BinaryOperator(val leftOperand:Proposition,val rightOperand:Proposition):TruthTableOperator(listOf(leftOperand,rightOperand))
{
    abstract val friendly:String
    override fun toString():String
    {
        return children.map {if (it.children.size > 1) "($it)" else "$it"}.joinToString(friendly)
    }
}

abstract class AssociativeOperator(operands:List<Proposition>):Operator(operands)
{
    companion object
    {
        fun make(operands:Iterable<Proposition>,isSameOperator:(Proposition)->Boolean,operatorFactory:(List<Proposition>)->Proposition):Proposition
        {
            val newOperands = operands.flatMap()
            {
                if (isSameOperator(it))
                {
                    it.children
                }
                else
                {
                    listOf(it)
                }
            }
            return when (newOperands.size)
            {
                0 -> throw IllegalArgumentException("not enough operands")
                1 -> newOperands.single()
                else -> operatorFactory(newOperands)
            }
        }
    }
    abstract val friendly:String
    override fun toString():String
    {
        return children.map {if (it.children.size > 1) "($it)" else "$it"}.joinToString(friendly)
    }
}

val Proposition.not:Not get() = Not(this)

class Not(operand:Proposition):UnaryOperator(operand)
{
    override val friendly:String get() = "¬"
    override val truthTable:Map<List<Boolean>,Boolean> get() = Companion.truthTable
    companion object
    {
        val truthTable = mapOf(
            listOf(true ) to false,
            listOf(false) to true
        )
    }
}

infix fun Proposition.and(other:Proposition) = And.make(listOf(this,other))

class And private constructor(operands:List<Proposition>):AssociativeOperator(operands)
{
    companion object
    {
        fun make(operands:Iterable<Proposition>):Proposition
        {
            val isSameFunction = {proposition:Proposition -> proposition is And}
            val operatorFactory = {operands:List<Proposition> -> And(operands)}
            return AssociativeOperator.make(operands,isSameFunction,operatorFactory)
        }
    }

    override val friendly:String get() = "∧"

    override fun operate(operands:List<Boolean>):Boolean
    {
        return operands.all {it}
    }

    override fun operate(operands:List<Double>):Double
    {
        return operands.fold(1.0) {i,n -> i*n}
    }
}

infix fun Proposition.or(other:Proposition) = Or.make(listOf(this,other))

class Or private constructor(operands:List<Proposition>):AssociativeOperator(operands)
{
    companion object
    {
        fun make(operands:Iterable<Proposition>):Proposition
        {
            val isSameFunction = {proposition:Proposition -> proposition is Or}
            val operatorFactory = {operands:List<Proposition> -> Or(operands)}
            return AssociativeOperator.make(operands,isSameFunction,operatorFactory)
        }
    }

    override val friendly:String get() = "∨"

    override fun operate(operands:List<Boolean>):Boolean
    {
        return operands.any {it}
    }

    override fun operate(operands:List<Double>):Double
    {
        return operands.fold(1.0) {i,n -> i*(1-n)}.let {1-it}
    }
}

infix fun Proposition.oif(other:Proposition) = Oif(this,other)

class Oif(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand)
{
    override val friendly:String get() = "→"
    override val truthTable:Map<List<Boolean>,Boolean> get() = Companion.truthTable
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

infix fun Proposition.iff(other:Proposition) = Iff.make(listOf(this,other))

class Iff private constructor(operands:List<Proposition>):AssociativeOperator(operands)
{
    companion object
    {
        fun make(operands:List<Proposition>):Proposition
        {
            val isSameFunction = {proposition:Proposition -> proposition is Iff}
            val operatorFactory = {operands:List<Proposition> -> Iff(operands)}
            return AssociativeOperator.make(operands,isSameFunction,operatorFactory)
        }
    }

    override val friendly:String get() = "↔"

    override fun operate(operands:List<Boolean>):Boolean
    {
        return operands.all {it == operands.first()}
    }

    override fun operate(operands:List<Double>):Double
    {
        val allTrue = operands.fold(1.0) {i,n -> i*n}
        val allFalse = operands.fold(1.0) {i,n -> i*(1-n)}
        return 1-((1-allTrue)*(1-allFalse))
    }
}

infix fun Proposition.xor(other:Proposition) = Xor(this,other)

class Xor(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand)
{
    override val friendly:String get() = "⊕"
    override val truthTable:Map<List<Boolean>,Boolean> get() = Companion.truthTable
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

class Nand(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand)
{
    override val friendly:String get() = "|"
    override val truthTable:Map<List<Boolean>,Boolean> get() = Companion.truthTable
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
