package com.github.ericytsang.research2016.propositionallogic

import org.junit.Test
import java.util.AbstractSet
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.TreeMap

/**
 * Created by surpl on 5/4/2016.
 */
class SituationTest
{
    @Test
    fun hashCodeTest()
    {
        val map1 = LinkedHashMap(mapOf("p" to true,"q" to false,"r" to true))
        val map2 = HashMap(mapOf("p" to true,"q" to true,"r" to true))
        val situation1 = State.fromStringMap(map1)
        val situation2 = State.fromStringMap(map2)
        println("situation1.hashCode(): ${situation1.hashCode()}")
        println("situation2.hashCode(): ${situation2.hashCode()}")
        assert(situation1.hashCode() == map1.hashCode(),{"equivalent hash codes should have matched. situation1.hashCode(): ${situation1.hashCode()}, map1.hashCode(): ${map1.hashCode()}"})
        assert(situation2.hashCode() == map2.hashCode(),{"equivalent hash codes should have matched. situation2.hashCode(): ${situation2.hashCode()}, map2.hashCode(): ${map2.hashCode()}"})
    }

    @Test
    fun toStringTest()
    {
        val map1 = TreeMap(mapOf("p" to true,"q" to false,"r" to true))
        val map2 = LinkedHashMap(mapOf("p" to true,"q" to true,"r" to true))
        val situation1 = State.fromStringMap(map1)
        val situation2 = State.fromStringMap(map2)
        println("situation1.toString(): ${situation1.toString()}")
        println("situation2.toString(): ${situation2.toString()}")
        assert(situation1.toString() == "{p, r}",{"equivalent strings should have matched. situation1.toString(): ${situation1.toString()}, map1.toString(): ${map1.toString()}"})
        assert(situation2.toString() == "{p, q, r}",{"equivalent strings should have matched. situation2.toString(): ${situation2.toString()}, map2.toString(): ${map2.toString()}"})
    }

    @Test
    fun equivalenceTest()
    {
        val map1 = HashMap(mapOf("p" to true,"q" to false,"r" to true))
        val map2 = mapOf("p" to true,"q" to false,"r" to true)
        val situation1 = State.fromStringMap(map1)
        val situation2 = State.fromStringMap(map2)

        assert(situation1 == situation2,{"equivalent situations returned false when compared with equals(). situation1: $situation1, situation2: $situation2"})
    }

    @Test
    fun inequivalenceTest()
    {
        val map1 = mapOf("p" to true,"q" to false,"r" to true)
        val map2 = TreeMap(mapOf("p" to true,"q" to true,"r" to true))
        val situation1 = State.fromStringMap(map1)
        val situation2 = State.fromStringMap(map2)

        assert(situation1 != situation2,{"equivalent situations returned false when compared with equals(). situation1: $situation1, situation2: $situation2"})
    }

    @Test
    fun nextReturnsAllUniqueSituationsTest()
    {
        val basicPropositions = setOf(Variable.make("p"),Variable.make("q"),Variable.make("r"))
        val allSituations = State.permutationsOf(basicPropositions)
        assert(allSituations.size == 8,{"allSituations size is not 8. it should be 8 if there are 3 basic propositions... allSituations: $allSituations"})
    }
}
