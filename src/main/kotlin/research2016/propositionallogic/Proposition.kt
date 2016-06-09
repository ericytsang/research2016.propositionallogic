package research2016.propositionallogic

import lib.collections.Bounds
import lib.collections.getRandom
import lib.collections.IteratorToSetAdapter
import lib.collections.branchAndBound
import lib.collections.rootNodeMetaData
import lib.delegates.LazyWithReceiver
import java.util.LinkedHashSet
import research2016.propositionallogic.Proposition.AtomicProposition
import research2016.propositionallogic.Proposition.Operator
import java.io.Serializable
import java.util.LinkedHashMap

/**
 * this class is the component in the Composite pattern.
 */
sealed class Proposition:Serializable
{
    companion object;

    /**
     * returns the expression as a human-readable string, e.g., (0 or 1) then 1
     */
    abstract override fun toString():String

    /**
     * returns a hashcode that should equal to the hashcode of other
     * [Proposition]s with an identical structure, [Operator]s and
     * [AtomicProposition]s.
     */
    override fun hashCode():Int = toString().hashCode()

    /**
     * returns true when this [Proposition] structurally equals to [other].
     *
     * @param other [Proposition] to compare with for structural equality.
     */
    override fun equals(other:Any?):Boolean = other is Proposition && other.toString() == toString()

    /**
     * [List] of children of this node.
     */
    abstract val children:List<Proposition>

    /**
     * a [Proposition] that cannot be further decomposed into more [Proposition]
     * objects; a leaf node.
     *
     * @param friendly the string returned when [toString] is called.
     */
    abstract class AtomicProposition(val friendly:String):Proposition()
    {
        /**
         * returns the [Boolean] truth value of this [AtomicProposition] for the
         * [situation].
         */
        abstract fun truthValue(situation:Situation):Boolean

        /**
         * returns [friendly].
         */
        override fun toString():String = friendly.toString()

        /**
         * [List] of children of this node. leaf nodes do not have children; an
         * empty [List] is returned in this implementation.
         */
        override val children:List<Proposition> = emptyList()
    }

    /**
     * a logical connective that connects its [children] together forming a
     * compound sentence; an internal node.
     */
    abstract class Operator(override val children:List<Proposition>):Proposition()
    {
        /**
         * returns the [Boolean] truth value of the operation for the given
         * [operands].
         *
         * @param operands truth value of ordered operands to operate on.
         */
        abstract fun operate(operands:List<Boolean>):Boolean

        /**
         * returns the truthiness of the operation for the given [operands]; the
         * probability that this operation would return true.
         *
         * @param operands probability of truth of ordered operands to operate
         * on.
         */
        abstract fun operate(operands:List<Double>):Double
    }
}

/**
 * creates a [Proposition] in conjunctive normal form from the provided
 * [situation], e.g. the situation {a, -b, c} would yield "a and -b and c".
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
 * generates a [List] of [Variable]s of length [numPropositions]. each
 * generated [Variable]'s [String] is randomly chosen from
 * [basicPropositionStrings].
 */
fun Variable.Companion.makeRandom(basicPropositionStrings:List<String>,numPropositions:Int):List<Variable>
{
    return (1..numPropositions).map {Variable.make(basicPropositionStrings.getRandom())}
}

/**
 * returns all the basic propositions in this [Proposition].
 */
val Proposition.variables:Set<Variable> by LazyWithReceiver<Proposition,Set<Variable>>()
{
    with (it)
    {
        val candidates = LinkedHashSet<Variable>()
        if (this is Variable) candidates.add(this)
        candidates.addAll(children.flatMap {it.variables})
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
        val basicPropositions = variablesToInfluence.entries
            .sortedBy {it.value}.map {it.key}

        val branch = fun(situation:Situation):Set<Situation>
        {
            val nextVariable = basicPropositions.minus(situation.keys).firstOrNull()
            if (nextVariable != null)
            {
                val node1 = Situation(situation+mapOf(nextVariable to true))
                val node2 = Situation(situation+mapOf(nextVariable to false))
                return listOf(node1,node2)
                    .filter {truthiness(it) != 0.0}
                    .toSet()
            }
            else
            {
                return emptySet()
            }
        }

        val bounds = fun(situation:Situation):Bounds
        {
            return Bounds(1.0,Math.floor(truthiness(situation)))
        }

        // returns true if the situation is a solution; false otherwise
        val checkSolution = fun(situation:Situation):Boolean
        {
            return truthiness(situation) == 1.0 && situation.keys.containsAll(basicPropositions)
        }

        val iterator = object:AbstractIterator<Situation>()
        {
            val unbranchedSituations = mutableMapOf(Situation(emptyMap()) to rootNodeMetaData)
            override fun computeNext()
            {
                val next = branchAndBound(unbranchedSituations,branch,bounds,checkSolution)
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
 * returns a map of [Variable] to [Double]s. the value mapped to each
 * [Variable] should be between 1.0 and 0.0. the value represents an
 * estimate of how much the truth value of that [Variable] is able to
 * affect the truth value of the whole [Proposition].
 */
private val Proposition.variablesToInfluence by LazyWithReceiver<Proposition,Map<Variable,Double>> {it.variablesToInfluence()}
private fun Proposition.variablesToInfluence(influence:Double = 1.0):Map<Variable,Double> = when(this)
{
    is Variable -> mapOf(this to influence)
    is Operator ->
    {
        // distribute the influence among the children of the operator
        val dividedInfluence = influence/(children.size)
        val influences = children.map {it.variablesToInfluence(dividedInfluence)}

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
    is Operator -> operate(children.map {it.evaluate(situation)})
}

/**
 * returns the [truthiness] of this [Proposition] for the given [situation].
 * if the proposition is more likely to be true for the given [situation], it
 * will return a value closer to 1.0. if the value is more likely to be false,
 * it will return a value closer to 0.0. if the returned value is 1 or 0,
 * [evaluate] should return true or false respectively if it is passed the same
 * [situation], assuming that there are no missing value mappings.
 */
fun Proposition.truthiness(situation:Situation):Double = when (this)
{
    is Variable ->
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
    is Operator -> operate(children.map {it.truthiness(situation)})
}

/**
 * returns a logically equivalent [Proposition] in full disjunctive normal form.
 *
 * (a and b and c) or (a and -b and -c)
 */
fun Proposition.toFullDnf():Proposition
{
    // list of literals (or negations of literals) and'd together
    val literalConjunctions = models
        .mapNotNull()
        {
            situation ->
            situation.keys
                // return the proposition if it is mapped to true, and the
                // negation of it otherwise
                .map {if (situation[it]!!) it else it.not}
                // and everything together
                .let {if (it.isNotEmpty()) And.make(it) else Tautology}
        }
    return if (literalConjunctions.isEmpty())
    {
        Contradiction
    }
    else
    {
        Or.make(literalConjunctions)
    }
}

/**
 * returns a logically equivalent [Proposition] in disjunctive normal form...
 * simplified as much as possible as if one was using Karnaugh maps...
 *
 * (a and b) or -a
 */
fun Proposition.toDnf():Proposition
{
    val hammingDistance = fun(situation1:Situation,situation2:Situation):Int
    {
        return situation1.keys.count {situation1[it] != situation2[it]}
    }

    val basicPropositionsToSituations = LinkedHashMap(models.groupBy {it.keys}.mapValues {it.value.toMutableSet()})
    val unprocessedKeys = basicPropositionsToSituations.keys.toMutableSet()

    while (unprocessedKeys.isNotEmpty())
    {
        val basicPropositions = unprocessedKeys.maxBy {it.size}!!
        val situations = basicPropositionsToSituations[basicPropositions]!!
        var situation1:Situation? = null
        var situation2:Situation? = null
        loop@for (situationA in situations)
        {
            for (situationB in situations)
            {
                if (hammingDistance(situationA,situationB) == 1)
                {
                    situation1 = situationA
                    situation2 = situationB
                    break@loop
                }
            }
        }
        if (situation1 == null && situation2 == null)
        {
            unprocessedKeys.remove(basicPropositions)
        }
        else
        {
            situations.remove(situation1)
            situations.remove(situation2)

            val commonMappings = situation1!!.entries.filter {situation1!![it.key] == situation2!![it.key]}
            val newSituation = Situation(commonMappings.associate {it.key to it.value})
            basicPropositionsToSituations.getOrPut(newSituation.keys,{mutableSetOf()}).add(newSituation)
            unprocessedKeys.add(newSituation.keys)
        }
    }

    // list of literals (or negations of literals) and'd together
    val literalConjunctions = basicPropositionsToSituations
        .flatMap {it.value}
        .mapNotNull()
        {
            situation ->
            situation.keys
                // return the proposition if it is mapped to true, and the
                // negation of it otherwise
                .map {if (situation[it]!!) it else it.not}
                // and everything together
                .let {if (it.isNotEmpty()) And.make(it) else Tautology}
        }
    return if (literalConjunctions.isEmpty())
    {
        Contradiction
    }
    else
    {
        Or.make(literalConjunctions)
    }
}
