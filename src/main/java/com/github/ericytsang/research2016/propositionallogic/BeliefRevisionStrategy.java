package com.github.ericytsang.research2016.propositionallogic;

import java.util.Set;

/**
 * Created by surpl on 8/2/2016.
 */
public interface BeliefRevisionStrategy
{
    Set<Proposition> revise(Set<Proposition> beliefState,Proposition sentence);
}
