package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.util.List;
import java.util.Set;

/**
 * Created by surpl on 8/2/2016.
 */
public class HammingDistanceComparator extends ByDistanceComparator
{
    public HammingDistanceComparator(Set<Proposition> beliefState)
    {
        super(beliefState);
    }

    @Override
    protected int computeDistanceTo(final State state)
    {
        return CollectionWrapper.wrap(beliefStateModels)
            .map(new Function<State,Integer>()
            {
                @Override
                public Integer invoke(State params)
                {
                    return hammingDistance(params,state);
                }
            })
            .minBy(new Function<Integer,Integer>()
            {
                @Override
                public Integer invoke(Integer params)
                {
                    return params;
                }
            })
            .apply(new Function<CollectionWrapper<List<Integer>,Integer>,Void>()
            {
                @Override
                public Void invoke(CollectionWrapper<List<Integer>,Integer> params)
                {
                    if (params.isEmpty())
                    {
                        params.add(0);
                    }
                    return null;
                }
            })
            .first();
    }

    /**
     * returns the hamming distance between this [state1] and [state2];
     * the number of mappings of variables to truth values that they do not
     * match.
     */
    private int hammingDistance(State state1,final State state2)
    {
        Set<Variable> commonKeys = CollectionWrapper.wrap(state1.keySet())
            .filter(new Function<Variable,Boolean>()
            {
                @Override
                public Boolean invoke(Variable params)
                {
                    return state2.keySet().contains(params);
                }
            }).toSet().wrapee;

        int hammingDistance = 0;
        for (Variable commonKey : commonKeys)
        {
            if (state1.get(commonKey) != state2.get(commonKey))
            {
                hammingDistance += 1;
            }
        }

        return hammingDistance;
    }
}
