package research2016.propositionallogic

import java.util.LinkedHashMap
import java.util.LinkedHashSet

/**
 * Created by surpl on 5/4/2016.
 */
class Situation(propositionValues:Map<BasicProposition,Boolean>):Map<BasicProposition,Boolean>
{
    companion object;
    private val map = LinkedHashMap(propositionValues)
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

fun Situation.Companion.permute(situationSets:List<Set<Situation>>):Set<Situation>
{
    // verify that every basic propositions specified in every situation in each
    // set is present in every other situation in the same set.
    situationSets.forEach()
    {
        situationSet -> situationSet.forEach()
        {
            situation ->
            if (situation.keys != situationSet.first().keys)
                throw IllegalArgumentException("every basic propositions specified in every situation in each set should be present in every other situation in the same set....but it is not. situationSets: $situationSets")
        }
    }

    // permute every situation with every other situation, and return a set of
    // all permutations...
    return situationSets
        .sortedBy {situationSet -> situationSet.size}
        .fold(setOf(Situation(emptyMap())))
        {
            combinedSituationSet,situationSet ->
            combinedSituationSet
                .flatMap()
                {
                    situation1 ->
                    situationSet.mapNotNull()
                    {
                        situation2 ->
                        val combinedSituation = Situation(situation1+situation2)
                        val isCombinedSituationConsistent =
                            combinedSituation.entries.all()
                            {
                                situation1[it.key] ?: it.value == it.value && situation2[it.key] ?: it.value == it.value
                            }
                        if (isCombinedSituationConsistent)
                        {
                            combinedSituation
                        }
                        else
                        {
                            null
                        }
                    }
                }
                .toSet()
        }
}
