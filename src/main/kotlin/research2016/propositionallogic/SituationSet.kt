package research2016.propositionallogic

import java.util.ArrayList
import java.util.NoSuchElementException

/**
 * Created by surpl on 5/7/2016.
 */
interface SituationSet:Set<Situation>

class AtomicSituationSet private constructor(val situationSet:Set<Situation>):SituationSet
{
    companion object
    {
        val emptySituationSet = AtomicSituationSet(emptySet())

        fun make(situationSet:Set<Situation>):SituationSet
        {
            if (situationSet.isNotEmpty())
            {
                return AtomicSituationSet(situationSet)
            }
            else
            {
                return emptySituationSet
            }
        }
    }
    override val size:Int get() = situationSet.size
    override fun contains(element:Situation):Boolean = situationSet.contains(element)
    override fun containsAll(elements:Collection<Situation>):Boolean = situationSet.containsAll(elements)
    override fun isEmpty():Boolean = situationSet.isEmpty()
    override fun iterator():Iterator<Situation> = situationSet.iterator()
}

class PermutedSituationSet private constructor(val situationSetList:List<SituationSet>):SituationSet
{
    companion object
    {
        fun make(situationSetList:List<SituationSet>):SituationSet
        {
            if (situationSetList.any {it.isEmpty()})
            {
                return AtomicSituationSet.emptySituationSet
            }
            else
            {
                return PermutedSituationSet(situationSetList)
            }
        }
    }

    init
    {
        assert(situationSetList.all {it.isNotEmpty()})
    }

    override val size:Int = (this as Iterable<Situation>).count()
    override fun contains(element:Situation):Boolean = (this as Iterable<Situation>).contains(element)
    override fun containsAll(elements:Collection<Situation>):Boolean = elements.all {contains(it)}
    override fun isEmpty():Boolean = false
    override fun iterator():Iterator<Situation> = MyIterator()

    inner class MyIterator:Iterator<Situation>
    {
        val situationSetIterators = ArrayList(situationSetList.map {it.iterator()})

        val situationsToCombine = ArrayList(situationSetIterators.map {it.next()})

        var lastNextThrewException:Boolean = false

        var isNextItemInitialized:Boolean = false

        var nextItem:Situation? = null

        override fun hasNext():Boolean
        {
            if (!isNextItemInitialized)
            {
                nextItem = try { next() } catch (ex:NoSuchElementException) { null }
            }
            return nextItem != null
        }

        override fun next():Situation
        {
            if (lastNextThrewException)
            {
                throw NoSuchElementException()
            }

            else if (isNextItemInitialized)
            {
                isNextItemInitialized = false
                return nextItem!!
            }
            else
            {
                whileLoop@while (true)
                {
                    // try to combine situations
                    val combinedSituation = situationsToCombine.fold(Situation(emptyMap())) {prev,next -> Situation(prev+next)}

                    // return combined situation if valid
                    val isCombinedSituationConsistent =
                        combinedSituation.entries.all()
                        {
                            situationsToCombine.all() {situation -> situation[it.key] ?: it.value == it.value}
                        }
                    if (isCombinedSituationConsistent)
                    {
                        return combinedSituation
                    }

                    // compute the next situations to combine
                    for (i in 0..situationSetIterators.size)
                    {
                        if (i !in situationSetIterators.indices)
                        {
                            lastNextThrewException = true
                            throw NoSuchElementException()
                        }

                        // if there is a next, get it for combining later
                        if (situationSetIterators[i].hasNext())
                        {
                            situationsToCombine[i] = situationSetIterators[i].next()
                            continue@whileLoop
                        }

                        // else reset the iterator for i
                        else
                        {
                            situationSetIterators[i] = situationSetList[i].iterator()
                            situationsToCombine[i] = situationSetIterators[i].next()
                        }
                    }
                }
            }
        }
    }
}
