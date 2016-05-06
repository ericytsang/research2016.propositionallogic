package research2016.propositionallogic.core

import lib.delegates.LazyWithReceiver
import java.util.LinkedHashSet
import research2016.propositionallogic.core.Proposition.AtomicProposition
import research2016.propositionallogic.core.Proposition.Operator

/**
 * Created by surpl on 5/4/2016.
 */
sealed class Proposition
{
    /**
     * returns the expression as a human-readable string, e.g., (0∨1)→1
     */
    abstract override fun toString():String

    override fun hashCode():Int = toString().hashCode()

    override fun equals(other:Any?):Boolean = other is Proposition && other.toString() == toString()

    /**
     * list of the children associated with this node.
     */
    abstract val children:List<Proposition>

    abstract class AtomicProposition(val friendly:String,val allSituations:Set<Situation>):Proposition()
    {
        abstract fun truthValue(situation:Situation):Boolean
        override fun toString():String = friendly.toString()
        override val children:List<Proposition> = emptyList()
    }

    abstract class Operator(val operands:List<Proposition>,val truthTable:Map<List<Boolean>,Boolean>):Proposition()
    {
        init
        {
            assert(truthTable.keys.all {it.size == operands.size},{"length of list for truth table keys should match length of list of operands. truth table: $truthTable, operands: $operands"})
        }
        fun operate(operands:List<Boolean>):Boolean = truthTable[operands]!!
        override val children:List<Proposition> = operands
    }
}

/**
 * returns all the basic propositions in this [Proposition].
 */
val Proposition.basicPropositions:Set<BasicProposition> by LazyWithReceiver<Proposition,Set<BasicProposition>>()
{
    with (it)
    {
        val candidates = LinkedHashSet<BasicProposition>()
        if (this is BasicProposition) candidates.add(this)
        candidates.addAll(children.flatMap {it.basicPropositions})
        return@LazyWithReceiver candidates
    }
}

/**
 * returns all the models of this [Proposition], i.e., all the [Situation] that
 * satisfy this [Proposition].
 *
 * todo: make more efficient
 */
val Proposition.models:Set<Situation> get()
{
    val allSituations = Situation.generateFrom(basicPropositions)
    return allSituations.filter {evaluate(it)}.toSet()
}

/**
 * returns the truth value of this [Proposition] for the given [Situation].
 */
fun Proposition.evaluate(situation:Situation):Boolean = when (this)
{
    is AtomicProposition -> truthValue(situation)
    is Operator -> operate(operands.map {it.evaluate(situation)})
}
