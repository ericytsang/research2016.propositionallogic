package research2016.propositionallogic

import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.collections.AbstractIterator

/**
 * Created by surpl on 5/7/2016.
 */

class PermutedSituationSet private constructor(val situationSetList:List<Set<Situation>>):Set<Situation>
{
    companion object
    {
        fun make(situationSetList:List<Set<Situation>>):Set<Situation>
        {
            if (situationSetList.any {it.isEmpty()})
            {
                return emptySet()
            }
            else
            {
                return PermutedSituationSet(situationSetList)
            }
        }
    }

    private var _realSet:Set<Situation>? = null
    private val realSet:Set<Situation> get() =  _realSet ?: run {_realSet = toCollection(LinkedHashSet()); _realSet!!}
    override val size:Int get() = realSet.size
    override fun contains(element:Situation):Boolean = realSet.contains(element)
    override fun containsAll(elements:Collection<Situation>):Boolean = realSet.containsAll(elements)
    override fun isEmpty():Boolean = _realSet?.let {it.isEmpty()} ?: !iterator().hasNext()
    override fun toString():String = realSet.toString()
    override fun hashCode():Int = realSet.hashCode()
    override fun equals(other:Any?):Boolean = realSet.equals(other)
    override fun iterator():Iterator<Situation> = object:AbstractIterator<Situation>()
    {
        val returnedElements = mutableSetOf<Situation>()
        val situationSetIterators = situationSetList.map {it.iterator()}.toTypedArray()
        val situationsToCombine = Array(situationSetIterators.size,{situationSetIterators[it].next()})
        var isFirstIteration = true
        override fun computeNext()
        {
            while (true)
            {
                // compute the next situations to combine
                if (isFirstIteration)
                {
                    isFirstIteration = false
                }
                else
                {
                    for (i in 0..situationSetIterators.size)
                    {
                        // exit if there are no more situations to combine
                        if (i !in situationSetIterators.indices)
                        {
                            done()
                            if (_realSet == null) _realSet = returnedElements
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
                    returnedElements.add(combinedSituation)
                    return
                }
            }
        }
    }
}

class CombinedSituationSet private constructor(val situationSetList:List<Set<Situation>>):Set<Situation>
{
    companion object
    {
        fun make(situationSetList:List<Set<Situation>>):Set<Situation>
        {
            @Suppress("NAME_SHADOWING")
            val situationSetList = situationSetList.filter {it.isNotEmpty()}
            if (situationSetList.isEmpty())
            {
                return emptySet()
            }
            else
            {
                return CombinedSituationSet(situationSetList)
            }
        }
    }

    private var _realSet:Set<Situation>? = null
    private val realSet:Set<Situation> get() =  _realSet ?: run {_realSet = toCollection(LinkedHashSet()); _realSet!!}
    override val size:Int get() = realSet.size
    override fun contains(element:Situation):Boolean = situationSetList.any {it.contains(element)}
    override fun containsAll(elements:Collection<Situation>):Boolean = elements.all {contains(it)}
    override fun isEmpty():Boolean = !iterator().hasNext()
    override fun iterator():Iterator<Situation> = object:AbstractIterator<Situation>()
    {
        private val iterators = situationSetList.map {it.iterator()}
        private val returnedElements = LinkedHashSet<Situation>()
        override fun computeNext()
        {
            while (true)
            {
                val iterators = iterators.filter {it.hasNext()}
                if (iterators.isEmpty())
                {
                    done()
                    if (_realSet == null) _realSet = returnedElements
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
    override fun toString():String = realSet.toString()
    override fun hashCode():Int = realSet.hashCode()
    override fun equals(other:Any?):Boolean = realSet.equals(other)
}
