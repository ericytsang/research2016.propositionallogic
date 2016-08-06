package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.first
import com.github.ericytsang.lib.collections.joinToString
import com.github.ericytsang.lib.collections.last
import com.github.ericytsang.lib.collections.let
import com.github.ericytsang.lib.collections.map
import com.github.ericytsang.lib.collections.mutableListOf
import com.github.ericytsang.lib.formulainterpreter.FormulaTreeFactory

fun Proposition.Companion.parse(string:String):Proposition
{
    var spacedString = ""
    for (i in 0..string.length-1)
    {
        if (string[i] == '(' || string[i] == ')' || string[i] == '-')
        {
            spacedString += " "+string[i]+" "
        }
        else
        {
            spacedString += string[i]
        }
    }
    val tokens = mutableListOf<String>()
    var word = ""
    for (i in 0..spacedString.length-1)
    {
        if (spacedString[i] == ' ')
        {
            if (word.length != 0)
            {
                tokens.add(word)
                word = ""
            }
        }
        else
        {
            word += spacedString[i]
        }
        if (i == spacedString.length-1 && word.length != 0)
        {
            tokens.add(word)
        }
    }
    return propositionFactory.parse(tokens)
}

fun Proposition.toParsableString():String
{
    return when (this)
    {
        is Proposition.Operator ->
        {
            children
                .map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}
                .let()
                {
                    when (this)
                    {
                        is Not -> it.joinToString(prefix = "-")
                        is And -> it.joinToString(separator = " and ")
                        is Or -> it.joinToString(separator = " or ")
                        is Oif -> it.joinToString(separator = " then ")
                        is Iff -> it.joinToString(separator = " iff ")
                        is Xor -> it.joinToString(separator = " xor ")
                        is Nand -> it.joinToString(separator = " nand ")
                        is Proposition.Operand,
                        is Proposition.Operator -> throw IllegalArgumentException("unknown type: "+this)
                    }
                }
        }
        is Proposition.Operand -> toString()
    }
}

private val tokenInterpreter = object:FormulaTreeFactory.TokenInterpreter
{
    override fun parseToken(word:String):FormulaTreeFactory.Symbol
    {
        return when (java.lang.String(word).toLowerCase())
        {
            "-" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,1,5)
            "and" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,4)
            "or" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,3)
            "then" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,2)
            "iff" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,1)
            "xor" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,3)
            "nand" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,4)
            "(" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPENING_PARENTHESIS,0,0)
            ")" -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.CLOSING_PARENTHESIS,0,0)
            else -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERAND,0,0)
        }
    }
}

private val operandFactory = object:FormulaTreeFactory.OperandFactory<Proposition>
{
    override fun makeOperand(word:String):Proposition
    {
        return when (word)
        {
            "1" -> tautology
            "0" -> contradiction
            else -> Variable.fromString(word)
        }
    }

    override fun makeOperator(word:String,operands:List<Proposition>):Proposition
    {
        return when (java.lang.String(word).toLowerCase())
        {
            "-" -> Not(operands[0])
            "and" -> And.make(operands)!!
            "or" -> Or.make(operands)!!
            "then" -> Oif(operands.first(),operands.last())
            "iff" -> Iff.make(operands)!!
            "xor" -> Xor(operands.first(),operands.last())
            "nand" -> Nand(operands.first(),operands.last())
            else -> throw IllegalArgumentException("unrecognized token: \$word")
        }
    }
}

private val propositionFactory = FormulaTreeFactory<Proposition>(tokenInterpreter,operandFactory)
