package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AssociativeOperator extends Operator
{
    public static Proposition make(Collection<Proposition> operands,final Function<Proposition,Boolean> isSameOperator,final Function<List<Proposition>,Proposition> operatorFactory)
    {
        return CollectionWrapper.wrap(operands)
            .flatMap(new Function<Proposition,Collection<Proposition>>()
            {
                @Override
                public Collection<Proposition> invoke(Proposition params)
                {
                    if (isSameOperator.invoke(params))
                    {
                        return params.getChildren();
                    }
                    else
                    {
                        return Collections.singletonList(params);
                    }
                }
            })
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    switch(params.wrapee.size())
                    {
                        case 0:
                            return null;
                        case 1:
                            return params.wrapee.get(0);
                        default:
                            return operatorFactory.invoke(params.wrapee);
                    }
                }
            });
    }

    public AssociativeOperator(List<Proposition> operands)
    {
        super(operands);
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
                    StringBuilder stb = new StringBuilder();
                    for (int i = 0; i < params.wrapee.size(); i++)
                    {
                        if (i != 0)
                        {
                            stb.append(getFriendly());
                        }
                        stb.append(params.wrapee.get(i));
                    }
                    return stb.toString();
                }
            });
    }
}
