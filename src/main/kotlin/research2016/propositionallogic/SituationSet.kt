package research2016.propositionallogic

import java.util.LinkedHashSet
import kotlin.collections.AbstractIterator

/**
 * Created by surpl on 5/7/2016.
 */
class SituationSetPermutingIterator(val situationSetList:List<Set<Situation>>):AbstractIterator<Situation>()
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

class SituationSetCombiningIterator(val situationSetList:List<Set<Situation>>):AbstractIterator<Situation>()
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
