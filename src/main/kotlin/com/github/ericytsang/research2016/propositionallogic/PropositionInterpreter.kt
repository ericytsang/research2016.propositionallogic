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
        override fun parse(word:String):FormulaTreeFactory.Symbol
        {
            val preprocessedWord = word.toLowerCase().trim()
            return when
            {
                Pattern.matches("(iff){1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERATOR,2,1)
                Pattern.matches("(then){1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERATOR,2,2)
                Pattern.matches("(or){1}",preprocessedWord) ||
                    Pattern.matches("(xor){1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERATOR,2,3)
                Pattern.matches("(and){1}",preprocessedWord) ||
                    Pattern.matches("(nand){1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERATOR,2,4)
                Pattern.matches("(-){1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERATOR,1,5)
                Pattern.matches("(1){1}",preprocessedWord) ||
                    Pattern.matches("(0){1}",preprocessedWord) ||
                    Pattern.matches("[a-zA-Z]+",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPERAND,0,0)
                Pattern.matches("[(]{1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.OPENING_PARENTHESIS,0,0)
                Pattern.matches("[)]{1}",preprocessedWord) -> FormulaTreeFactory.Symbol(FormulaTreeFactory.Symbol.Type.CLOSING_PARENTHESIS,0,0)
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }
    },

    object:FormulaTreeFactory.OperandFactory<Proposition>
    {
        override fun parse(word:String):Proposition
        {
            val preprocessedWord = word.toLowerCase().trim()
            return when
            {
                Pattern.matches("(1){1}",preprocessedWord) -> Proposition.TAUTOLOGY
                Pattern.matches("(0){1}",preprocessedWord) -> Proposition.CONTRADICTION
                Pattern.matches("[a-zA-Z]+",preprocessedWord) -> Variable.fromString(preprocessedWord)
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }

        override fun parse(word:String,operands:List<Proposition>):Proposition
        {
            val preprocessedWord = word.toLowerCase().trim()
            return when
            {
                Pattern.matches("(iff){1}",preprocessedWord) -> Iff.make(operands)!!
                Pattern.matches("(then){1}",preprocessedWord) -> Oif(operands.first(),operands.last())
                Pattern.matches("(or){1}",preprocessedWord) -> Or.make(operands)!!
                Pattern.matches("(xor){1}",preprocessedWord) -> Xor(operands.first(),operands.last())
                Pattern.matches("(and){1}",preprocessedWord) -> And.make(operands)!!
                Pattern.matches("(nand){1}",preprocessedWord) -> Nand(operands.first(),operands.last())
                Pattern.matches("(-){1}",preprocessedWord) -> Not(operands.single())
                else -> throw IllegalArgumentException("unrecognized token: $word")
            }
        }
    })

fun Proposition.toParsableString():String
{
    return when (this)
    {
        is Operand -> friendly
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
