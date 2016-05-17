package research2016.propositionallogic

import lib.collections.permutedIterator
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.collections.AbstractIterator

/**
 * Created by surpl on 5/7/2016.
 */
class SituationSetPermutingIterator(val situationSetList:List<Set<Situation>>):AbstractIterator<Situation>()
{
    private val permutedSituationIterator = situationSetList.permutedIterator()
    override fun computeNext()
    {
        loop@ while (true)
        {
            if (Thread.interrupted()) throw InterruptedException("interrupted!")

            // if there is a permutation remaining to combine and check, do so
            if (permutedSituationIterator.hasNext())
            {
                // try to combine situations...while combining, make sure there
                // are no conflicting values e.g., combined situation "ab" is
                // not conflicting with with situations "a" and "b", but is in
                // conflict with "ab" and "b not".
                val situationsToCombine = permutedSituationIterator.next()
                val combinedMap = LinkedHashMap<BasicProposition,Boolean>()
                for (situation in situationsToCombine)
                {
                    for (entry in situation)
                    {
                        val prevMapping = combinedMap.put(entry.key,entry.value)
                        if (prevMapping == null || prevMapping == entry.value)
                        {
                            continue
                        }
                        else
                        {
                            continue@loop
                        }
                    }
                }

                setNext(Situation(combinedMap))
                return
            }

            // finish otherwise
            else
            {
                done()
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
