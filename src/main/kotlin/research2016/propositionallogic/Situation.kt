package research2016.propositionallogic

import lib.collections.IteratorToSetAdapter
import java.io.Serializable
import java.util.LinkedHashSet

/**
 * maps [Variable]s to truth values [Boolean]s.
 */
class Situation(val propositionValues:Map<Variable,Boolean>):Map<Variable,Boolean>,Serializable
{
    companion object;
    private val map:Map<Variable,Boolean> get() = propositionValues
    override val entries:Set<Map.Entry<Variable,Boolean>> get() = map.entries
    override val keys:Set<Variable> get() = map.keys
    override val size:Int get() = map.size
    override val values:Collection<Boolean> get() = map.values
    override fun containsKey(key:Variable):Boolean = map.containsKey(key)
    override fun containsValue(value:Boolean):Boolean = map.containsValue(value)
    override fun get(key:Variable):Boolean? = map[key]
    override fun isEmpty():Boolean = map.isEmpty()
    override fun toString():String = map.toString()
    override fun hashCode():Int = map.hashCode()
    override fun equals(other:Any?):Boolean = map.equals(other)
}

fun Situation.Companion.make(propositionValues:Map<String,Boolean>):Situation
{
    return Situation(propositionValues.mapKeys {Variable.make(it.key)})
}

/**
 * returns all possible permutations of [Situation]s (truth value assignments)
 * that involve variables from [variables].
 */
fun Situation.Companion.generateFrom(variables:Set<Variable>):Set<Situation>
{
    val numSituationsToGenerate = Math.round(Math.pow(2.toDouble(),variables.size.toDouble())).toInt()
    val allSituations = LinkedHashSet<Situation>()
    val propositionKeys = variables.map {it.friendly}.toList().sorted()
    var seed = 0
    while (seed != numSituationsToGenerate)
    {
        val newSituation = run()
        {
            val string = Integer.toBinaryString(seed).padStart(variables.size,'0')
            val map = propositionKeys.mapIndexed { i, s -> s to (string[i] == '1') }.toMap()
            return@run Situation.make(map)
        }
        allSituations.add(newSituation)
        seed++
    }
    if(allSituations.size != numSituationsToGenerate)
    {
        throw RuntimeException("failed to generate all situations! D: situations generated: $allSituations")
    }
    return allSituations
}

fun Situation.Companion.permute(situationSetList:List<Set<Situation>>):Set<Situation>
{
    return IteratorToSetAdapter(SituationSetPermutingIterator(situationSetList))
}

/**
 * combines multiple sets of situations into one. like the union set operator,
 * except the computation of unifying the sets is deferred to when the resulting
 * unified set is queried.
 */
fun Situation.Companion.combine(situationSetList:List<Set<Situation>>):Set<Situation>
{
    return IteratorToSetAdapter(SituationSetCombiningIterator(situationSetList))
}
