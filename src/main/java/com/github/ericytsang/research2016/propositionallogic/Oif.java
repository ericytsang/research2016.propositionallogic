package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.ContainerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by surpl on 7/31/2016.
 */
public class Oif extends BinaryOperator
{
    public static final String friendly = "→";

    public static final Map<List<Boolean>,Boolean> truthTable;

    static
    {
        truthTable = new HashMap<List<Boolean>,Boolean>(4);
        truthTable.put(ContainerUtils.listOf(true,true),true);
        truthTable.put(ContainerUtils.listOf(false,true),true);
        truthTable.put(ContainerUtils.listOf(true,false),false);
        truthTable.put(ContainerUtils.listOf(false,false),true);
    }

    public Oif(Proposition leftOperand,Proposition rightOperand)
    {
        super(leftOperand,rightOperand);
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
