package research2016.propositionallogic

import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.collections.AbstractIterator

/**
 * Created by surpl on 5/7/2016.
 */

abstract class AbstractSet<T>():Set<T>
{
    override val size:Int = 0

    private val generatedElementsList = ArrayList<T>()
    private val generatedElementsSet = LinkedHashSet<T>()
    private var allElementsGenerated = false

    abstract protected fun hasNext():Boolean

    abstract protected fun next():T

    override fun contains(element:T):Boolean
    {
        if (!allElementsGenerated) generateAllElements()
        return generatedElementsSet.contains(element)
    }

    override fun containsAll(elements:Collection<T>):Boolean
    {
        if (!allElementsGenerated) generateAllElements()
        return generatedElementsSet.containsAll(elements)
    }

    override fun isEmpty():Boolean = !iterator().hasNext()

    private fun generateAllElements()
    {
        for (t in this) {}
    }

    override fun equals(other:Any?):Boolean
    {
        if (!allElementsGenerated) generateAllElements()
        return generatedElementsSet.equals(other)
    }

    override fun hashCode():Int
    {
        if (!allElementsGenerated) generateAllElements()
        return generatedElementsSet.hashCode()
    }

    override fun toString():String
    {
        if (!allElementsGenerated) generateAllElements()
        return generatedElementsSet.toString()
    }

    override fun iterator():Iterator<T> = object:AbstractIterator<T>()
    {
        var nextElementIndex = 0
        override fun computeNext()
        {
            // generate the next element if it doesn't exist yet
            if (!allElementsGenerated && nextElementIndex !in generatedElementsList.indices)
            {
                synchronized(generatedElementsList)
                {
                    if (this@AbstractSet.hasNext())
                    {
                        val generatedElement = this@AbstractSet.next()
                        generatedElementsList.add(generatedElement)
                        generatedElementsSet.add(generatedElement)
                    }
                    else
                    {
                        allElementsGenerated = true
                    }
                }
            }

            // set the next element
            if (nextElementIndex in generatedElementsList.indices)
            {
                setNext(generatedElementsList[nextElementIndex++])
            }
            else
            {
                done()
            }
        }
    }
}

class PermutedSituationSet private constructor(val situationSetList:List<Set<Situation>>):AbstractSet<Situation>()
{
    companion object
    {
        fun make(situationSetList:List<Set<Situation>>):Set<Situation>
        {
            return PermutedSituationSet(situationSetList)
        }
    }
    override fun next():Situation = iterator.next()
    override fun hasNext():Boolean = iterator.hasNext()
    private val iterator = object:AbstractIterator<Situation>()
    {
        val situationSetIterators = situationSetList.map {it.iterator()}.toTypedArray()
        lateinit var situationsToCombine:Array<Situation>
        var isFirstIteration = true
        override fun computeNext()
        {
            if (Thread.interrupted()) throw InterruptedException("interrupted!")
            while (true)
            {
                // compute the next situations to combine
                if (isFirstIteration)
                {
                    isFirstIteration = false
                    if (situationSetList.any {it.isEmpty()})
                    {
                        done()
                        return
                    }
                    else
                    {
                        situationsToCombine = Array(situationSetIterators.size,{situationSetIterators[it].next()})
                    }
                }
                else
                {
                    for (i in 0..situationSetIterators.size)
                    {
                        // exit if there are no more situations to combine
                        if (i !in situationSetIterators.indices)
                        {
                            done()
                            return
                        }

                        // if there is a next, get it for combining later
                        if (situationSetIterators[i].hasNext())
                        {
                            situationsToCombine[i] = situationSetIterators[i].next()
                            break
                        }

                        // else reset the iterator for i
                        else
                        {
                            situationSetIterators[i] = situationSetList[i].iterator()
                            situationsToCombine[i] = situationSetIterators[i].next()
                        }
                    }
                }

                // try to combine situations
                val combinedSituation = situationsToCombine.fold(Situation(emptyMap())) {prev,next -> Situation(prev+next)}

                // return combined situation if valid
                val isCombinedSituationConsistent =
                    combinedSituation.entries.all()
                    {
                        it ->
                        situationsToCombine.all() {situation -> situation[it.key] ?: it.value == it.value}
                    }
                if (isCombinedSituationConsistent)
                {
                    setNext(combinedSituation)
                    return
                }
            }
        }
    }
}

class CombinedSituationSet private constructor(val situationSetList:List<Set<Situation>>):AbstractSet<Situation>()
{
    companion object
    {
        fun make(situationSetList:List<Set<Situation>>):Set<Situation>
        {
            return CombinedSituationSet(situationSetList)
        }
    }
    override fun next():Situation = iterator.next()
    override fun hasNext():Boolean = iterator.hasNext()
    private val iterator = object:AbstractIterator<Situation>()
    {
        private val iterators = situationSetList.map {it.iterator()}.toMutableList()
        private val returnedElements = LinkedHashSet<Situation>()
        override fun computeNext()
        {
            if (Thread.interrupted()) throw InterruptedException("interrupted!")
            while (true)
            {
                iterators.removeAll {!it.hasNext()}
                if (iterators.isEmpty())
                {
                    done()
                    return
                }
                else
                {
                    val nextItem = iterators.first().next()
                    if (nextItem !in returnedElements)
                    {
                        returnedElements.add(nextItem)
                        setNext(nextItem)
                        return
                    }
                }
            }
        }
    }
}
