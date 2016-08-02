package com.github.ericytsang.research2016.propositionallogic;

/**
 * Created by surpl on 7/31/2016.
 */
public class Variable extends Operand
{
    public static Variable fromString(String friendly)
    {
        return new Variable(friendly);
    }

    private Variable(String friendly)
    {
        super(friendly);
    }

    @Override
    public boolean truthValue(State state)
    {
        Boolean result = state.get(this);
        if (result == null)
        {
            throw new IllegalArgumentException("no value specified for given proposition ("+toString()+")");
        }
        else
        {
            return result;
        }
    }
}
