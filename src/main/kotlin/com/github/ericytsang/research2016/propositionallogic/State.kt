package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.asIterable
import com.github.ericytsang.lib.collections.associate
import com.github.ericytsang.lib.collections.filter
import com.github.ericytsang.lib.collections.indices
import com.github.ericytsang.lib.collections.joinToString
import com.github.ericytsang.lib.collections.listOf
import com.github.ericytsang.lib.collections.map
import com.github.ericytsang.lib.collections.mutableMapOf
import com.github.ericytsang.lib.collections.mutableSetOf
import com.github.ericytsang.lib.collections.permutations
import com.github.ericytsang.lib.collections.set
import com.github.ericytsang.lib.collections.sortedBy
import com.github.ericytsang.lib.collections.to
import java.io.Serializable

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
            return State.fromVariableMap(propositionValues.entries.associate {Variable.fromString(it.key) to it.value})
        }

        /**
         * returns all possible permutations of [State]s (truth value assignments)
         * that involve variables from [variables].
         */
        fun permutationsOf(variables:Set<Variable>):Set<State>
        {
            val numStatesToGenerate = Math.round(Math.pow(2.0,variables.size.toDouble())).toInt()
            val allSituations = mutableSetOf<State>()
            val propositionKeys = variables.map {it.toString()}.sortedBy {it}
            val toPermute = variables.map {listOf(true,false)}

            for (booleans in toPermute.permutations.asIterable())
            {
                val stringMap = mutableMapOf<String,Boolean>()
                for (i in propositionKeys.indices)
                {
                    stringMap[propositionKeys[i]] = booleans[i]
                }
                val newState = State.fromStringMap(stringMap)
                allSituations.add(newState)
            }

            if(allSituations.size != numStatesToGenerate)
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

