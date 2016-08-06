package com.github.ericytsang.research2016.propositionallogic;

import java.util.Collections;
import java.util.List;

/**
 * Created by surpl on 7/30/2016.
 */
public abstract class Operand extends Proposition
{
    private final String friendly;

    protected Operand(String friendly)
    {
        this.friendly = friendly;
    }

    abstract boolean truthValue(State state);

    public String getFriendly()
    {
        return friendly;
    }

    @Override
    public List<Proposition> getChildren()
    {
        return Collections.emptyList();
    }

    @Override
    public String toString()
    {
        return friendly;
    }
}
