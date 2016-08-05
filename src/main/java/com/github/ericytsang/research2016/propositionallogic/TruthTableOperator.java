package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.util.List;
import java.util.Map;

public abstract class TruthTableOperator extends Operator
{
    public TruthTableOperator(List<Proposition> operands)
    {
        super(operands);
    }

    public abstract Map<List<Boolean>,Boolean> getTruthTable();

    @Override
    public Boolean operateBooleans(List<Boolean> operands)
    {
        if (getTruthTable().containsKey(operands))
        {
            return getTruthTable().get(operands);
        }
        else
        {
            throw new IllegalArgumentException("truth table entry for operands not found. truth table may be missing an entry, or the number of provided operands is too much or too little for this operator. truth table: "+getTruthTable()+", operands: "+operands);
        }
    }

    @Override
    public Double operateDoubles(final List<Double> operands)
    {
        return CollectionWrapper.wrap(getTruthTable().entrySet())
            .filter(new Function<Map.Entry<List<Boolean>,Boolean>,Boolean>()
            {
                @Override
                public Boolean invoke(Map.Entry<List<Boolean>,Boolean> params)
                {
                    return params.getValue();
                }
            })
            .map(new Function<Map.Entry<List<Boolean>,Boolean>,List<Boolean>>()
            {
                @Override
                public List<Boolean> invoke(Map.Entry<List<Boolean>,Boolean> params)
                {
                    return params.getKey();
                }
            })
            .map(new Function<List<Boolean>,Double>()
            {
                @Override
                public Double invoke(List<Boolean> params)
                {
                    Double combinedDoubles = 1.0;
                    for (int i = 0; i < params.size(); i++)
                    {
                        if (params.get(i))
                        {
                            combinedDoubles *= operands.get(i);
                        }
                        else
                        {
                            combinedDoubles *= 1.0-operands.get(i);
                        }
                    }
                    return combinedDoubles;
                }
            })
            .let(new Function<CollectionWrapper<List<Double>,Double>,Double>()
            {
                @Override
                public Double invoke(CollectionWrapper<List<Double>,Double> params)
                {
                    Double sum = 0.0;
                    for (int i = 0; i < params.size(); i++)
                    {
                        sum += params.wrapee.get(i);
                    }
                    return sum;
                }
            });
    }
}
