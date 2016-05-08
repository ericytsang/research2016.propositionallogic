package research2016.propositionallogic

import lib.delegates.LazyWithReceiver
import java.util.LinkedHashSet
import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator

/**
 * Created by surpl on 5/4/2016.
 */
sealed class Proposition
{
    companion object;

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
        override fun hashCode():Int = friendly.hashCode()
        override fun equals(other:Any?):Boolean = other is AtomicProposition && other.friendly == friendly
        override val children:List<Proposition> = emptyList()
        abstract val allSituations:Set<Situation>
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
 */
val Proposition.models:Models by LazyWithReceiver<Proposition,Models>()
{
    with (it)
    {
        when (this)
        {
            is AtomicProposition ->
            {
                val (trueSituations,falseSituations) = allSituations.partition {truthValue(it)}
                Models(trueSituations.toSet(),falseSituations.toSet())
            }
            is Operator ->
            {
                // two sets. each set is a set of lists of booleans that will
                // yield true or false respectively when evaluated by this
                // operator
                // e.g. one of the sets may look like this: {[1,0,0],[1,1,1]}
                val (inputsForTrue,inputsForFalse) = truthTable.keys
                    .partition {truthTable[it]!!}
                    .let {it.first.toSet() to it.second.toSet()}

                // throw interrupted exception if interrupted...
                if (Thread.interrupted()) throw InterruptedException("thread was interrupted")

                // list of models for each operand in the order that they appear
                // in the operand list. each model contains situations
                // partitioned into ones that satisfy the operand, and ones that
                // do not.
                // i.e.: [{q,!q} to {},{p} to {!p}]
                val operandModels = children
                    .map {it.models}

                // throw interrupted exception if interrupted...
                if (Thread.interrupted()) throw InterruptedException("thread was interrupted")

                // set of situations that would make this proposition true
                val trueSituations =
                    // {[0,0],[0,1],[1,1]}
                    inputsForTrue
                        // [[{p},{}],[{p},{!q,q}],[{!p},{!q,q}]]
                        .map {booleanList -> booleanList.mapIndexed {i,b -> if (b) operandModels[i].trueSituations else operandModels[i].falseSituations}}
                        // [{},{p!q,pq},{!p!q,!pq}]
                        .map {situationSetList -> Situation.permute(situationSetList)}
                        // {p!q,pq,!p!q,!pq}
                        .let {CombinedSituationSet.make(it)}

                // set of situations that would make this proposition false
                val falseSituations = inputsForFalse
                    .map {booleanList -> booleanList.mapIndexed {i,b -> if (b) operandModels[i].trueSituations else operandModels[i].falseSituations}}
                    .map {situationSetList -> Situation.permute(situationSetList)}
                    .let {CombinedSituationSet.make(it)}

                Models(trueSituations,falseSituations)
            }
        }
    }
}

data class Models(val trueSituations:Set<Situation>,val falseSituations:Set<Situation>)

/**
 * returns the truth value of this [Proposition] for the given [Situation].
 */
fun Proposition.evaluate(situation:Situation):Boolean = when (this)
{
    is AtomicProposition -> truthValue(situation)
    is Operator -> operate(operands.map {it.evaluate(situation)})
}
