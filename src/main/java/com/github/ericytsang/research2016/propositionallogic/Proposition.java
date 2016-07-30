package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by surpl on 7/26/2016.
 */
public abstract class Proposition implements Serializable
{
    public static Proposition makeDnf(final State state)
    {
        return new CollectionWrapper<Set<Variable>,Variable>(state.keySet())
            .map(new Function<Variable,Proposition>()
            {
                @Override
                public Proposition invoke(Variable params)
                {
                    if (state.get(params))
                    {
                        return params;
                    }
                    else
                    {
                        return params.not();
                    }
                }
            })
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    Proposition result = And.Companion.make(params);
                    return result != null ? result : Proposition.TAUTOLOGY;
                }
            });
    }

    public static Proposition makeDnf(Iterable<State> states)
    {
        CollectionWrapper<List<State>,State> collectionWrapper = new CollectionWrapper<List<State>,State>(new LinkedList<State>());
        for (State state : states)
        {
            collectionWrapper.add(state);
        }
        collectionWrapper
            .map(new Function<State,Proposition>()
            {
                @Override
                public Proposition invoke(State params)
                {
                    return Proposition.makeDnf(params);
                }
            })
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    Proposition result = Or.Companion.make(params);
                    return result != null ? result : Proposition.CONTRADICTION;
                }
            });
    }
}
