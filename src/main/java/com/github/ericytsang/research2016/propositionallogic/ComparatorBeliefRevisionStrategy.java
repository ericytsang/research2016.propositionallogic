package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.ContainerUtils;
import com.github.ericytsang.lib.collections.Function;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ComparatorBeliefRevisionStrategy implements BeliefRevisionStrategy
{
    private final Function<Set<Proposition>,Comparator<State>> stateComparatorFactory;

    /**
     * class that uses an instance of the [Comparator] to order instances of the
     * [State] in order to do the belief revision.
     *
     * @param stateComparatorFactory used to create the [Comparator]. it will be
     * given the initial belief state as an argument, and must return the
     * appropriate [Comparator] which will be used to sort the [State]s.
     */
    public ComparatorBeliefRevisionStrategy(Function<Set<Proposition>,Comparator<State>> stateComparatorFactory)
    {
        this.stateComparatorFactory = stateComparatorFactory;
    }

    @Override
    public Set<Proposition> revise(Set<Proposition> beliefState,final Proposition sentence)
    {
        Comparator<State> stateComparator = stateComparatorFactory.invoke(beliefState);

        // create a tautology that uses all the variables in all the formulas
        // e.g. (a or -a) and (b or -b) and (c or -c) and...
        Proposition allVariablesTautology = CollectionWrapper.wrap(beliefState)
            // get all variables involved and make each one into a tautology
            .flatMap(new Function<Proposition,Collection<Variable>>()
            {
                @Override
                public Collection<Variable> invoke(Proposition params)
                {
                    return params.getVariables();
                }
            })
            .apply(new Function<CollectionWrapper<List<Variable>,Variable>,Void>()
            {
                @Override
                public Void invoke(CollectionWrapper<List<Variable>,Variable> params)
                {
                    params.addAll(sentence.getVariables());
                    return null;
                }
            })
            .toSet()
            .map(new Function<Variable,Proposition>()
            {
                @Override
                public Proposition invoke(Variable params)
                {
                    return params.or(params.not());
                }
            })
            // and them together
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    return And.make(params.wrapee);
                }
            });

        // all models of the sentence..and'd together with
        // basicPropositionTautologies to make sure the resulting models
        // contains a mapping for all variables
        Set<State> sentenceModels = sentence.and(allVariablesTautology).getModels();

        // find the first models in the ordering
        CollectionWrapper<Set<State>,State> nearestModels = CollectionWrapper.wrap(sentenceModels)
            .minWith(stateComparator).toSet();

        if (nearestModels.isEmpty())
        {
            return ContainerUtils.setOf(Proposition.CONTRADICTION);
        }
        else
        {
            return nearestModels
                .map(new Function<State,Proposition>()
                {
                    @Override
                    public Proposition invoke(State params)
                    {
                        return Proposition.makeDnf(params);
                    }
                })
                .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Set<Proposition>>()
                {
                    @Override
                    public Set<Proposition> invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                    {
                        return ContainerUtils.setOf(Or.make(params.wrapee));
                    }
                });
        }
    }
}
