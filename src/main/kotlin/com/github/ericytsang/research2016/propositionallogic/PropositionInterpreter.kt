package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.formulainterpreter.FormulaTreeFactory
import java.util.regex.Pattern

/**
 * Created by surpl on 5/19/2016.
 */
fun Proposition.Companion.makeFrom(string:String):Proposition
{
    return propositionFactory.parse(prepareForPropositionFactory(string))
}

private fun prepareForPropositionFactory(string:String):List<String> = string.replace("("," ( ").replace(")"," ) ").replace("-"," - ").trim().split(Regex("[ ]+"))

private val propositionFactory = FormulaTreeFactory(

    object:FormulaTreeFactory.TokenInterpreter
    {
        override fun parseToken(word:String):FormulaTreeFactory.Symbol
        {
            val preprocessedWord = word.trim()
            return when
            {
                Pattern.matches("(iff)",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,1)
                Pattern.matches("(then)",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,2)
                Pattern.matches("(or)",preprocessedWord) ||
                    Pattern.matches("(xor)",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,3)
                Pattern.matches("(and)",preprocessedWord) ||
                    Pattern.matches("(nand)",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,4)
                Pattern.matches("(-)",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,1,5)
                Pattern.matches("(1)",preprocessedWord) ||
                    Pattern.matches("(0)",preprocessedWord) ||
                    Pattern.matches("[a-zA-Z][a-zA-Z0-9]*",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERAND,0,0)
                Pattern.matches("[(]",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPENING_PARENTHESIS,0,0)
                Pattern.matches("[)]",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.CLOSING_PARENTHESIS,0,0)
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }
    },

    object:FormulaTreeFactory.OperandFactory<Proposition>
    {
        override fun makeOperand(word:String):Proposition
        {
            val preprocessedWord = word.trim()
            return when
            {
                Pattern.matches("(1)",preprocessedWord) -> tautology
                Pattern.matches("(0)",preprocessedWord) -> contradiction
                Pattern.matches("[a-zA-Z][a-zA-Z0-9]*",preprocessedWord) -> Variable.fromString(preprocessedWord)
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }

        override fun makeOperator(word:String,operands:List<Proposition>):Proposition
        {
            val preprocessedWord = word.trim()
            return when
            {
                Pattern.matches("(iff)",preprocessedWord) -> Iff.make(operands)!!
                Pattern.matches("(then)",preprocessedWord) -> Oif(operands.first(),operands.last())
                Pattern.matches("(or)",preprocessedWord) -> Or.make(operands)!!
                Pattern.matches("(xor)",preprocessedWord) -> Xor(operands.first(),operands.last())
                Pattern.matches("(and)",preprocessedWord) -> And.make(operands)!!
                Pattern.matches("(nand)",preprocessedWord) -> Nand(operands.first(),operands.last())
                Pattern.matches("(-)",preprocessedWord) -> Not(operands.single())
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }
    })

fun Proposition.toParsableString():String
{
    return when (this)
    {
        is Proposition.Operand -> friendly
        is Iff -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " iff ")
        is Or -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " or ")
        is And -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " and ")
        is Oif -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " then ")
        is Xor -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " xor ")
        is Nand -> children.map {if (it.children.size > 1) "(${it.toParsableString()})" else it.toParsableString()}.joinToString(separator = " nand ")
        is Not -> if (operand.children.size > 1) "-(${operand.toParsableString()})" else "-${operand.toParsableString()}"
        else -> throw NotImplementedError()
    }
}
