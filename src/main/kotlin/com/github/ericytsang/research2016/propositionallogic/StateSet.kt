package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.permutedIterator
import java.util.LinkedHashSet
import kotlin.collections.AbstractIterator

/**
 * Created by surpl on 5/7/2016.
 */
class StateSetPermutingIterator(val stateSetList:List<Set<State>>):AbstractIterator<State>()
{
    private val permutedSituationIterator = stateSetList.permutedIterator()
    override fun computeNext()
    {
        while (true)
        {
            if (Thread.interrupted()) throw InterruptedException("interrupted!")

            // if there is a permutation remaining to combine and check, do so
            if (permutedSituationIterator.hasNext())
            {
                // try to combine situations
                val situationsToCombine = permutedSituationIterator.next()
                val combinedSituation = situationsToCombine.fold(State(emptyMap())) {prev,next -> State(prev+next)}

                // return combined situation if it is consistent with the
                // situations it is made up of, e.g., combined situation "ab" is
                // consistent with situations "a" and "b", but is not consistent
                // with "ab" and "b not"
                val isCombinedSituationConsistent = combinedSituation.entries.all()
                    {
                        combinedSituationEntry ->
                        situationsToCombine.all()
                        {
                            situation ->
                            situation[combinedSituationEntry.key] ?: combinedSituationEntry.value == combinedSituationEntry.value
                        }
                    }
                if (isCombinedSituationConsistent)
                {
                    setNext(combinedSituation)
                    return
                }
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

class StateSetCombiningIterator(val stateSetList:List<Set<State>>):AbstractIterator<State>()
{
    private val iterators = stateSetList.map {it.iterator()}.toMutableList()
    private val returnedElements = LinkedHashSet<State>()
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
