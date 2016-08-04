package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.ContainerUtils;
import com.github.ericytsang.lib.collections.Function;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by surpl on 7/31/2016.
 */
public class And extends AssociativeOperator
{
    public static final String friendly = "∧";

    public static Proposition make(Proposition...operands)
    {
        List<Proposition> list = new LinkedList<Proposition>();
        ContainerUtils.addAll(list,operands);
        return make(list);
    }

    public static Proposition make(List<Proposition> operands)
    {
        Function<Proposition,Boolean> isSameOperator = new Function<Proposition,Boolean>()
        {
            @Override
            public Boolean invoke(Proposition params)
            {
                return params instanceof And;
            }
        };

        Function<List<Proposition>,Proposition> operatorFactory = new Function<List<Proposition>,Proposition>()
        {
            @Override
            public Proposition invoke(List<Proposition> params)
            {
                return new And(params);
            }
        };

        return AssociativeOperator.make(operands,isSameOperator,operatorFactory);
    }

    private And(List<Proposition> operands)
    {
        super(operands);
    }

    @Override
    public String getFriendly()
    {
        return friendly;
    }

    @Override
    public Boolean operateBooleans(List<Boolean> operands)
    {
        for (Boolean operand : operands)
        {
            if (!operand)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Double operateDoubles(List<Double> operands)
    {
        Double result = 1.0;
        for(Double operand : operands)
        {
            result *= operand;
        }
        return result;
    }
}
