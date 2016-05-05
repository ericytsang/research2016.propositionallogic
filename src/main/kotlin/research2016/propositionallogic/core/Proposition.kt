package research2016.propositionallogic.core

import research2016.propositionallogic.visiter.DfsVisitor
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import research2016.propositionallogic.core.Proposition.AtomicProposition
import research2016.propositionallogic.core.Proposition.UnaryOperator
import research2016.propositionallogic.core.Proposition.BinaryOperator

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

    abstract class UnaryOperator(val operand:Proposition,val friendly:String,val truthTable:Map<Boolean,Boolean>):Proposition()
    {
        fun operate(operand:Boolean):Boolean = truthTable[operand]!!
        override fun toString():String = "($friendly$operand)"
        override val children:List<Proposition> = listOf(operand)
    }

    abstract class BinaryOperator(val leftOperand:Proposition,val rightOperand:Proposition,val friendly:String,val truthTable:Map<Pair<Boolean,Boolean>,Boolean>):Proposition()
    {
        fun operate(leftOperand:Boolean,rightOperand:Boolean):Boolean = truthTable[leftOperand to rightOperand]!!
        override fun toString():String = "($leftOperand$friendly$rightOperand)"
        override val children:List<Proposition> = listOf(leftOperand,rightOperand)
    }

    companion object
    {
        val nodeAccessStrategy = object:DfsVisitor.NodeAccessStrategy<Proposition>
        {
            override fun getChildren(node:Proposition):List<Proposition> = node.children
        }
    }
}

/**
 * returns all the basic propositions in this [Proposition].
 */
val Proposition.basicPropositions:Set<BasicProposition> get()
{
    val atomicPropositionSearch = object:DfsVisitor<Proposition>(Proposition.nodeAccessStrategy)
    {
        val candidates = LinkedHashSet<BasicProposition>()
        override fun visit(node:Proposition,parent:Proposition?)
        {
            if (node is BasicProposition)
            {
                candidates.add(node)
            }
        }
    }
    atomicPropositionSearch.beginTraversal(this)
    return atomicPropositionSearch.candidates
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
        override fun visit(node:Proposition,parent:Proposition?)
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
