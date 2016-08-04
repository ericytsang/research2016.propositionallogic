package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.ContainerUtils;

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
        return ContainerUtils.emptyList();
    }

    @Override
    public String toString()
    {
        return friendly;
    }
}
