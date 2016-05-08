package research2016.propositionallogic

import java.util.LinkedHashMap
import java.util.LinkedHashSet

/**
 * Created by surpl on 5/4/2016.
 */
class Situation(val propositionValues:Map<BasicProposition,Boolean>):Map<BasicProposition,Boolean>
{
    companion object;
    private val map:Map<BasicProposition,Boolean> get() = propositionValues
    override val entries:Set<Map.Entry<BasicProposition,Boolean>> get() = map.entries
    override val keys:Set<BasicProposition> get() = map.keys
    override val size:Int get() = map.size
    override val values:Collection<Boolean> get() = map.values
    override fun containsKey(key:BasicProposition):Boolean = map.containsKey(key)
    override fun containsValue(value:Boolean):Boolean = map.containsValue(value)
    override fun get(key:BasicProposition):Boolean? = map[key]
    override fun isEmpty():Boolean = map.isEmpty()
    override fun toString():String = map.toString()
    override fun hashCode():Int = map.hashCode()
    override fun equals(other:Any?):Boolean = map.equals(other)
}

fun Situation.Companion.make(propositionValues:Map<String,Boolean>):Situation
{
    return Situation(propositionValues.mapKeys {BasicProposition.make(it.key)})
}

fun Situation.Companion.generateFrom(basicPropositions:Set<BasicProposition>):Set<Situation>
{
    val numSituationsToGenerate = Math.round(Math.pow(2.toDouble(),basicPropositions.size.toDouble())).toInt()
    val allSituations = LinkedHashSet<Situation>()
    val propositionKeys = basicPropositions.map {it.friendly}.toList().sorted()
    var seed = 0
    while (seed != numSituationsToGenerate)
    {
        val newSituation = run()
        {
            val string = Integer.toBinaryString(seed).padStart(basicPropositions.size,'0')
            val map = propositionKeys.mapIndexed { i, s -> s to (string[i] == '1') }.toMap()
            return@run Situation.make(map)
        }
        allSituations.add(newSituation)
        seed++
    }
    assert(allSituations.size == numSituationsToGenerate,{"failed to generate all situations! D: situations generated: $allSituations"})
    return allSituations
}

fun Situation.Companion.permute(situationSetList:List<Set<Situation>>):Set<Situation>
{
    return PermutedSituationSet.make(situationSetList)
}
