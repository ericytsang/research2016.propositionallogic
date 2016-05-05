package research2016.propositionallogic.core

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
