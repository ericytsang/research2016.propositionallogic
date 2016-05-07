package research2016.propositionallogic

import java.util.LinkedHashSet

/**
 * Created by surpl on 5/4/2016.
 */
class Situation(val propositionValues:Map<String,Boolean>)
{
    companion object;

    /**
     * returns the value of the proposition for this [Situation].
     */
    fun getValue(proposition:BasicProposition):Boolean
    {
        return propositionValues[proposition.friendly] ?: throw IllegalArgumentException("no value specified for given proposition (${proposition.friendly})")
    }

    override fun toString():String
    {
        return propositionValues.toString()
    }

    override fun hashCode():Int
    {
        return propositionValues.hashCode()
    }

    override fun equals(other:Any?):Boolean
    {
        return other is Situation && other.propositionValues == propositionValues
    }
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
            return@run Situation(map)
        }
        allSituations.add(newSituation)
        seed++
    }
    assert(allSituations.size == numSituationsToGenerate,{"failed to generate all situations! D: situations generated: ${allSituations}"})
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
            if (situation.propositionValues.keys != situationSet.first().propositionValues.keys)
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
                        val combinedSituation = Situation(situation1.propositionValues+situation2.propositionValues)
                        val isCombinedSituationConsistent =
                            combinedSituation.propositionValues.entries.all()
                            {
                                situation1.propositionValues[it.key] ?: it.value == it.value && situation2.propositionValues[it.key] ?: it.value == it.value
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
