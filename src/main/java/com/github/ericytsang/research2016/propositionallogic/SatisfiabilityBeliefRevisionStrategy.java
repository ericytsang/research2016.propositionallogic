package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by surpl on 8/2/2016.
 */
public class SatisfiabilityBeliefRevisionStrategy implements BeliefRevisionStrategy
{
    @Override
    public Set<Proposition> revise(Set<Proposition> beliefState,final Proposition sentence)
    {
        return CollectionWrapper.wrap(beliefState)
            .let(new Function<CollectionWrapper<Set<Proposition>,Proposition>,CollectionWrapper<Set<Proposition>,Proposition>>()
            {
                @Override
                public CollectionWrapper<Set<Proposition>,Proposition> invoke(CollectionWrapper<Set<Proposition>,Proposition> params)
                {
                    Set<Proposition> set = new HashSet<Proposition>(params);
                    set.add(sentence);
                    return CollectionWrapper.wrap(set);
                }
            })
            .filter(new Function<Proposition,Boolean>()
            {
                @Override
                public Boolean invoke(Proposition params)
                {
                    return params.and(sentence).isSatisfiable();
                }
            })
            .toSet().wrapee;
    }
}
