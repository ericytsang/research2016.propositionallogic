package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.util.ArrayList;
import java.util.List;

public abstract class BinaryOperator extends TruthTableOperator
{
    private final Proposition leftOperand;

    private final Proposition rightOperand;

    public BinaryOperator(final Proposition leftOperand,final Proposition rightOperand)
    {
        super(new Function<Void,List<Proposition>>()
        {
            @Override
            public List<Proposition> invoke(Void params)
            {
                ArrayList<Proposition> list = new ArrayList<Proposition>(2);
                list.add(leftOperand);
                list.add(rightOperand);
                return list;
            }
        }.invoke(null));

        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public Proposition getLeftOperand()
    {
        return leftOperand;
    }

    public Proposition getRightOperand()
    {
        return rightOperand;
    }

    public abstract String getFriendly();

    @Override
    public String toString()
    {
        return CollectionWrapper.wrap(getChildren())
            .map(new Function<Proposition,String>()
            {
                @Override
                public String invoke(Proposition params)
                {
                    if (params.getChildren().size() > 1)
                    {
                        return "("+params+")";
                    }
                    else
                    {
                        return params.toString();
                    }
                }
            })
            .let(new Function<CollectionWrapper<List<String>,String>,String>()
            {
                @Override
                public String invoke(CollectionWrapper<List<String>,String> params)
                {
                    return params.wrapee.get(0)+getFriendly()+params.wrapee.get(1);
                }
            });
    }
}
