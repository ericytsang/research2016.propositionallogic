package com.github.ericytsang.research2016.propositionallogic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * [beliefState] is the belief state that is being revised.
 *
 * the [ByDistanceComparator] is a [Comparator] that can compare [State]
 * with one another to specify an ordering.
 *
 * this implementation assumes that each [State] is a specific distance away
 * from the [beliefState], and implements the [compare] function with this
 * assumption.
 */
public abstract class ByDistanceComparator implements Comparator<State>
{
    protected final Set<Proposition> beliefState;

    /**
     * all models of the receiver.
     */
    protected final Set<State> beliefStateModels;

    public ByDistanceComparator(Set<Proposition> beliefState)
    {
        this.beliefState = beliefState;
        Proposition prop = And.make(new ArrayList<Proposition>(beliefState));
        if (prop == null)
        {
            prop = Proposition.CONTRADICTION;
        }
        beliefStateModels = prop.getModels();
    }

    public Set<Proposition> getBeliefState()
    {
        return beliefState;
    }

    /**
     * used to cache previous calculations produced by the [computeDistanceTo]
     * function.
     */
    private Map<State,Integer> cachedCalculations = new LinkedHashMap<State,Integer>();

    @Override
    public int compare(State state1,State state2)
    {
        int situation1Distance = getDistanceTo(state1);
        int situation2Distance = getDistanceTo(state2);
        return situation1Distance-situation2Distance;
    }

    private int getDistanceTo(State state)
    {
        if (!cachedCalculations.containsKey(state))
        {
            cachedCalculations.put(state,computeDistanceTo(state));
        }
        return cachedCalculations.get(state);
    }

    /**
     * returns the distance from the [state] to the [beliefState].
     */
    protected abstract int computeDistanceTo(State state);
}
