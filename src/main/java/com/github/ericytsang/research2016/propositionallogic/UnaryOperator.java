package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.ContainerUtils;

public abstract class UnaryOperator extends TruthTableOperator
{
    private final Proposition operand;

    public UnaryOperator(Proposition operand)
    {
        super(ContainerUtils.listOf(operand));
        this.operand = operand;
    }

    public Proposition getOperand()
    {
        return operand;
    }

    public abstract String getFriendly();

    @Override
    public String toString()
    {
        if (getOperand().getChildren().size() > 1)
        {
            return getFriendly()+"("+getOperand()+")";
        }
        else
        {
            return getFriendly()+getOperand();
        }
    }
}
