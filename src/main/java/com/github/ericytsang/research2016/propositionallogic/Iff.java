package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.Function;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by surpl on 7/31/2016.
 */
public class Iff extends AssociativeOperator
{
    public static final String friendly = "↔";

    public static Proposition make(Proposition...operands)
    {
        List<Proposition> list = new LinkedList<Proposition>();
        Collections.addAll(list,operands);
        return make(list);
    }

    public static Proposition make(List<Proposition> operands)
    {
        Function<Proposition,Boolean> isSameOperator = new Function<Proposition,Boolean>()
        {
            @Override
            public Boolean invoke(Proposition params)
            {
                return params instanceof Iff;
            }
        };

        Function<List<Proposition>,Proposition> operatorFactory = new Function<List<Proposition>,Proposition>()
        {
            @Override
            public Proposition invoke(List<Proposition> params)
            {
                return new Iff(params);
            }
        };

        return AssociativeOperator.make(operands,isSameOperator,operatorFactory);
    }

    private Iff(List<Proposition> operands)
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
            if (operand != operands.get(0))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Double operateDoubles(List<Double> operands)
    {
        Double allFalse = 1.0;
        for(Double operand : operands)
        {
            allFalse *= 1.0-operand;
        }
        Double allTrue = 1.0;
        for(Double operand : operands)
        {
            allTrue *= operand;
        }
        return 1.0-((1.0-allTrue)*(1.0-allFalse));
    }
}
