package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.ContainerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by surpl on 7/31/2016.
 */
public class Not extends UnaryOperator
{
    public static final String friendly = "¬";

    public static final Map<List<Boolean>,Boolean> truthTable;

    static
    {
        truthTable = new HashMap<List<Boolean>,Boolean>(2);
        truthTable.put(ContainerUtils.listOf(true),false);
        truthTable.put(ContainerUtils.listOf(false),true);
    }

    public Not(Proposition operand)
    {
        super(operand);
    }

    @Override
    public Map<List<Boolean>,Boolean> getTruthTable()
    {
        return truthTable;
    }

    @Override
    public String getFriendly()
    {
        return friendly;
    }
}
