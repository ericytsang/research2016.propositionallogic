package com.github.ericytsang.research2016.propositionallogic

import java.io.Serializable
import java.util.LinkedHashSet

/**
 * maps [Variable]s to truth values [Boolean]s.
 */
class State private constructor(val propositionValues:Map<Variable,Boolean>):Map<Variable,Boolean>,Serializable
{
    companion object
    {
        fun fromVariableMap(propositionValues:Map<Variable,Boolean>):State
        {
            return State(propositionValues)
        }
        fun fromStringMap(propositionValues:Map<String,Boolean>):State
        {
            return State.fromVariableMap(propositionValues.mapKeys {Variable.fromString(it.key)})
        }

        /**
         * returns all possible permutations of [State]s (truth value assignments)
         * that involve variables from [variables].
         */
        fun permutationsOf(variables:Set<Variable>):Set<State>
        {
            val numSituationsToGenerate = Math.round(Math.pow(2.toDouble(),variables.size.toDouble())).toInt()
            val allSituations = LinkedHashSet<State>()
            val propositionKeys = variables.map {it.friendly}.toList().sorted()
            var seed = 0
            while (seed != numSituationsToGenerate)
            {
                val newSituation = run()
                {
                    val string = Integer.toBinaryString(seed).padStart(variables.size,'0')
                    val map = propositionKeys.mapIndexed { i, s -> s to (string[i] == '1') }.toMap()
                    return@run State.fromStringMap(map)
                }
                allSituations.add(newSituation)
                seed++
            }
            if(allSituations.size != numSituationsToGenerate)
            {
                throw RuntimeException("failed to generate all states! D: situations generated: $allSituations")
            }
            return allSituations
        }
    }
    private val map:Map<Variable,Boolean> get() = propositionValues
    override val entries:Set<Map.Entry<Variable,Boolean>> get() = map.entries
    override val keys:Set<Variable> get() = map.keys
    override val size:Int get() = map.size
    override val values:Collection<Boolean> get() = map.values
    override fun containsKey(key:Variable):Boolean = map.containsKey(key)
    override fun containsValue(value:Boolean):Boolean = map.containsValue(value)
    override fun get(key:Variable):Boolean? = map[key]
    fun getWithDefault(key:Variable):Boolean = map[key] ?: false
    override fun isEmpty():Boolean = map.isEmpty()
    override fun toString():String = entries
        .sortedBy {it.key.toString()}
        .filter {it.value}
        .map {it.key.toString()}
        .joinToString(separator = ", ",prefix = "{",postfix = "}")
    override fun hashCode():Int = map.hashCode()
    override fun equals(other:Any?):Boolean = map.equals(other)
}

