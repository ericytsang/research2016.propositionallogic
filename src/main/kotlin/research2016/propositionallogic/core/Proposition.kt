package research2016.propositionallogic.core

import lib.delegates.LazyWithReceiver
import research2016.propositionallogic.visiter.DfsVisitor
import java.util.LinkedHashMap
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

    abstract class AtomicProposition(val friendly:String):Proposition()
    {
        abstract fun truthValue(situation:Situation):Boolean
        override fun toString():String = friendly.toString()
        override val children:List<Proposition> = emptyList()
    }

    companion object
    {
        val nodeAccessStrategy = object:DfsVisitor.NodeAccessStrategy<Proposition>
        {
            override fun getChildren(node:Proposition):List<Proposition> = node.children
        }
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
    thisRef ->
    val atomicPropositionSearch = object:DfsVisitor<Proposition>(Proposition.nodeAccessStrategy)
    {
        val candidates = LinkedHashSet<BasicProposition>()
        override fun visit(node:Proposition,parent:Proposition?,children:List<Proposition>)
        {
            if (node is BasicProposition)
            {
                candidates.add(node)
            }
        }
    }
    atomicPropositionSearch.beginTraversal(thisRef)
    return@LazyWithReceiver atomicPropositionSearch.candidates
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
fun Proposition.evaluate(situation:Situation):Boolean
{
    val evaluator = object:DfsVisitor<Proposition>(Proposition.nodeAccessStrategy)
    {
        /**
         * a list of the [Proposition]'s children's evaluated truth values.
         * these lists are populated as the visitor visits the node's children,
         * and evaluates their truth values.
         */
        val Proposition.childrenTruthValues:MutableList<Boolean> get() = nodeToChildrenTruthValues.getOrPut(this,{mutableListOf<Boolean>()})
        val nodeToChildrenTruthValues = LinkedHashMap<Proposition,MutableList<Boolean>>()

        /**
         * contains the evaluated truth value of this [Proposition].
         */
        var propositionTruthValue:Boolean? = null

        /**
         * called on each node in the [Proposition] formula tree in dfs order.
         * evaluates the value of each node, and stores the truth value of this
         * [Proposition] in [propositionTruthValue].
         */
        override fun visit(node:Proposition,parent:Proposition?,children:List<Proposition>)
        {
            val nodeTruthValue = when (node)
            {
                is AtomicProposition -> { node.truthValue(situation) }
                is UnaryOperator -> { node.operate(node.childrenTruthValues.single()) }
                is BinaryOperator -> { node.operate(node.childrenTruthValues.first(),node.childrenTruthValues.last()) }
            }
            if (parent != null)
            {
                parent.childrenTruthValues.add(nodeTruthValue)
            }
            else
            {
                propositionTruthValue = nodeTruthValue
            }
        }
    }
    evaluator.beginTraversal(this)
    return evaluator.propositionTruthValue!!
}
