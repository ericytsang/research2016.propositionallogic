package research2016.propositionallogic

import lib.collections.getRandom
import lib.collections.IteratorToSetAdapter
import lib.delegates.LazyWithReceiver
import java.util.LinkedHashSet
import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator
import java.io.Serializable

/**
 * Created by surpl on 5/4/2016.
 */
sealed class Proposition:Serializable
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

    /**
     * leaf node.
     */
    abstract class AtomicProposition(val friendly:String):Proposition()
    {
        /**
         * returns the truth value of this [AtomicProposition] for the
         * [situation].
         */
        abstract fun truthValue(situation:Situation):Boolean

        /**
         * returns all [Situation]s that
         */
        abstract val allSituations:Set<Situation>

        override fun toString():String = friendly.toString()
        override val children:List<Proposition> = emptyList()
    }

    /**
     * has child nodes.
     */
    abstract class Operator(val operands:List<Proposition>):Proposition()
    {
        /**
         * returns the truth value of the operation for the given [operands].
         */
        abstract fun operate(operands:List<Boolean>):Boolean

        /**
         * returns the truthiness of the operation for the given [operands].
         */
        abstract fun operate(operands:List<Double>):Double

        override val children:List<Proposition> = operands
    }
}

/**
 * creates a [Proposition] in disjunctive normal form from the provided
 * [Situation].
 */
fun Proposition.Companion.makeFrom(situation:Situation):Proposition
{
    val propositions = situation.keys.map()
    {
        basicProposition ->
        if (situation[basicProposition]!!)
        {
            basicProposition
        }
        else
        {
            Not(basicProposition)
        }
    }
    return propositions.fold<Proposition,Proposition?>(null)
    {
        initial,next ->
        initial?.let {initial and next} ?: next
    } ?: Tautology
}

/**
 * generates a [List] of [BasicProposition]s of length [numPropositions]. each
 * generated [BasicProposition]'s [String] is randomly chosen from
 * [basicPropositionStrings].
 */
fun BasicProposition.Companion.makeRandom(basicPropositionStrings:List<String>,numPropositions:Int):List<BasicProposition>
{
    return (1..numPropositions).map {BasicProposition.make(basicPropositionStrings.getRandom())}
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
val Proposition.models:Set<Situation> by LazyWithReceiver<Proposition,Set<Situation>>()
{
    with(it)
    {
        // get all the basic propositions in descending order of influence
        val basicPropositions = basicPropositionToInfluence.entries
            .sortedBy {it.value}.map {it.key}

        val branch = fun(situation:Situation):Map<Situation,Double>
        {
            val nextVariable = basicPropositions.minus(situation.keys).firstOrNull()
            if (nextVariable != null)
            {
                val node1 = Situation(situation+mapOf(nextVariable to true))
                val node2 = Situation(situation+mapOf(nextVariable to false))
                return listOf(node1 to truthiness(node1),node2 to truthiness(node2))
                    .filter {it.second != 0.0}
                    .map {it.first to it.first.keys.size.toDouble()}
                    .toMap()
            }
            else
            {
                return emptyMap()
            }
        }

        // returns true if the situation is a solution; false otherwise
        val checkSolution = fun(situation:Situation):Boolean
        {
            return truthiness(situation) == 1.0 && situation.keys.containsAll(basicPropositions)
        }

        val iterator = object:AbstractIterator<Situation>()
        {
            val unbranchedSituations = mutableMapOf(Situation(emptyMap()) to 0.0)
            override fun computeNext()
            {
                val next = branchAndBound(unbranchedSituations,branch,checkSolution)
                if (next == null)
                {
                    done()
                }
                else
                {
                    setNext(next)
                }
            }
        }

        return@with IteratorToSetAdapter(iterator)
    }
}

/**
 * returns a map of [BasicProposition] to [Double]s. the value mapped to each
 * [BasicProposition] should be between 1.0 and 0.0. the value represents an
 * estimate of how much the truth value of that [BasicProposition] is able to
 * affect the truth value of the whole [Proposition].
 */
val Proposition.basicPropositionToInfluence by LazyWithReceiver<Proposition,Map<BasicProposition,Double>> {it.basicPropositionToInfluence()}
private fun Proposition.basicPropositionToInfluence(influence:Double = 1.0):Map<BasicProposition,Double> = when(this)
{
    is BasicProposition -> mapOf(this to influence)
    is Operator ->
    {
        // distribute the influence among the children of the operator
        val dividedInfluence = influence/(children.size)
        val influences = children.map {it.basicPropositionToInfluence(dividedInfluence)}

        // sum up all the influence for each basic proposition together
        influences
            // put all the entries into a list
            .flatMap {it.entries}
            // group entries by their keys (Basic Proposition)
            .groupBy {it.key}
            // sum the influence for each entry together
            .mapValues {it.value.sumByDouble {it.value}}
    }
    else -> emptyMap()
}

/**
 * returns true if there is at least one model for this [Proposition]; false
 * otherwise.
 */
val Proposition.isSatisfiable:Boolean get()
{
    return models.isNotEmpty()
}

/**
 * returns true if this [Proposition] would [evaluate] to true in any situation;
 * false otherwise.
 */
val Proposition.isTautology:Boolean get()
{
    return !not.isSatisfiable
}

/**
 * returns true if this [Proposition] would [evaluate] to false in any
 * situation; false otherwise.
 */
val Proposition.isContradiction:Boolean get()
{
    return !isSatisfiable
}

/**
 * returns the truth value of this [Proposition] for the given [situation].
 */
fun Proposition.evaluate(situation:Situation):Boolean = when (this)
{
    is AtomicProposition -> truthValue(situation)
    is Operator -> operate(operands.map {it.evaluate(situation)})
}

/**
 * returns the truthiness of this [Proposition] for the given [situation].
 * if the proposition is more likely to be true for the given [situation], it
 * will return a value closer to 1.0. if the value is more likely to be false,
 * it will return a value closer to 0.0. is the returned value is 1 or 0,
 * [evaluate] should return true or false respectively if it is passed the same
 * [situation], assuming that there are no missing value mappings.
 */
fun Proposition.truthiness(situation:Situation):Double = when (this)
{
    is BasicProposition ->
    {
        if (this in situation.keys)
        {
            if (truthValue(situation)) 1.0 else 0.0
        }
        else
        {
            0.5
        }
    }
    is AtomicProposition ->
    {
        when
        {
            this == Tautology -> 1.0
            this == Contradiction -> 0.0
            else -> throw IllegalArgumentException("unknown atomic proposition! $this")
        }
    }
    is Operator -> operate(operands.map {it.truthiness(situation)})
}
