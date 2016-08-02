package com.github.ericytsang.research2016.propositionallogic;

import java.util.*;

/**
 * Created by surpl on 7/30/2016.
 */
public abstract class Operator extends Proposition
{
    private final List<Proposition> children;

    public Operator(List<Proposition> children)
    {
        this.children = children;
    }

    @Override
    public List<Proposition> getChildren()
    {
        return children;
    }

    public abstract Boolean operateBooleans(List<Boolean> operands);
    public abstract Double operateDoubles(List<Double> operands);
}
