package research2016.propositionallogic.interpreter

import research2016.propositionallogic.core.And
import research2016.propositionallogic.core.BasicProposition
import research2016.propositionallogic.core.Contradiction
import research2016.propositionallogic.core.Iff
import research2016.propositionallogic.core.Nand
import research2016.propositionallogic.core.Not
import research2016.propositionallogic.core.Oif
import research2016.propositionallogic.core.Or
import research2016.propositionallogic.core.Proposition
import research2016.propositionallogic.core.Tautology
import research2016.propositionallogic.core.Xor
import java.util.ArrayList
import java.util.EmptyStackException
import java.util.Stack
import java.util.regex.Pattern

/**
 * Created by surpl on 5/5/2016.
 */
fun Proposition.Companion.from(string:String):Proposition
{
    val tokens = toReversePolishNotaion(string)

    // instantiate the appropriate object to represent each identified symbol
    val operandStack = Stack<Proposition>()
    tokens.forEach()
    {
        token ->
        val symbol = Symbol.parse(token)
        try
        {
            when (symbol)
            {
                Symbol.NOT -> operandStack.push(Not(operandStack.pop()))
                Symbol.AND ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(And(leftOperand,rightOperand))
                }
                Symbol.NAND ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(Nand(leftOperand,rightOperand))
                }
                Symbol.OR ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(Or(leftOperand,rightOperand))
                }
                Symbol.XOR ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(Xor(leftOperand,rightOperand))
                }
                Symbol.OIF ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(Oif(leftOperand,rightOperand))
                }
                Symbol.IFF ->
                {
                    val rightOperand = operandStack.pop()
                    val leftOperand = operandStack.pop()
                    operandStack.push(Iff(leftOperand,rightOperand))
                }
                Symbol.BASIC_PROPOSITION -> operandStack.push(BasicProposition(token))
                Symbol.TAUTOLOGY -> operandStack.push(Tautology())
                Symbol.CONTRADICTION -> operandStack.push(Contradiction())
                Symbol.OPEN_PARENTHESIS -> throw IllegalArgumentException("unexpected token...string has been put into reverse polish notation, and should not have any parentheses")
                Symbol.CLOSE_PARENTHESIS -> throw IllegalArgumentException("unexpected token...string has been put into reverse polish notation, and should not have any parentheses")
            }
        }
        catch (ex:EmptyStackException)
        {
            throw IllegalArgumentException("missing operand for operator: $token")
        }
    }
    return operandStack.pop()
}

/**
 * shunting algorithm
 */
private fun toReversePolishNotaion(string:String):List<String>
{
    val inputTokens = string
        .toLowerCase()
        .trim()
        .split(" ")
    val outputQueue = ArrayList<String>(inputTokens.size)
    val operatorStack = Stack<String>()

    inputTokens.forEach()
    {
        word ->
        val symbol = Symbol.parse(word)
        when (symbol)
        {
            Symbol.NOT,Symbol.AND,Symbol.NAND,Symbol.OR,Symbol.XOR,Symbol.OIF,Symbol.IFF ->
            {
                while (operatorStack.isNotEmpty() && symbol.precedence <= Symbol.parse(operatorStack.peek()).precedence)
                {
                    outputQueue.add(operatorStack.pop())
                }
                operatorStack.push(word)
            }
            Symbol.BASIC_PROPOSITION -> outputQueue.add(word)
            Symbol.TAUTOLOGY -> outputQueue.add(word)
            Symbol.CONTRADICTION -> outputQueue.add(word)
            Symbol.OPEN_PARENTHESIS -> operatorStack.add(word)
            Symbol.CLOSE_PARENTHESIS ->
            {
                while(true)
                {
                    try
                    {
                        val popped = operatorStack.pop()
                        if (Symbol.parse(popped) == Symbol.OPEN_PARENTHESIS)
                        {
                            break
                        }
                        else
                        {
                            outputQueue.add(popped)
                        }
                    }
                    catch (ex:EmptyStackException)
                    {
                        throw IllegalArgumentException("there is an uneven amount of parenthesis...")
                    }
                }
            }
        }
    }

    while (operatorStack.isNotEmpty())
    {
        outputQueue.add(operatorStack.pop())
    }

    return outputQueue
}

private enum class Symbol(private val _precedence:Int?,val pattern:String)
{
    NOT(5,"(not){1}"),
    AND(4,"(and){1}"),
    NAND(4,"(nand){1}"),
    OR(3,"(or){1}"),
    XOR(3,"(xor){1}"),
    OIF(2,"(oif){1}"),
    IFF(1,"(iff){1}"),
    BASIC_PROPOSITION(null,"[a-z]{1}"),
    TAUTOLOGY(null,"(1){1}"),
    CONTRADICTION(null,"(0){1}"),
    OPEN_PARENTHESIS(0,"[(]{1}"),
    CLOSE_PARENTHESIS(0,"[)]{1}");

    val precedence:Int get() = _precedence ?: throw IllegalAccessException("$this is not an operator, and therefore does not have a precedence...")

    companion object
    {
        fun parse(string:String):Symbol
        {
            val preProcessedString = string.toLowerCase().trim()
            assert(!preProcessedString.contains(' '))

            return when
            {
                Pattern.matches(NOT.pattern,preProcessedString) -> NOT
                Pattern.matches(AND.pattern,preProcessedString) -> AND
                Pattern.matches(NAND.pattern,preProcessedString) -> NAND
                Pattern.matches(OR.pattern,preProcessedString) -> OR
                Pattern.matches(XOR.pattern,preProcessedString) -> XOR
                Pattern.matches(OIF.pattern,preProcessedString) -> OIF
                Pattern.matches(IFF.pattern,preProcessedString) -> IFF
                Pattern.matches(BASIC_PROPOSITION.pattern,preProcessedString) -> BASIC_PROPOSITION
                Pattern.matches(TAUTOLOGY.pattern,preProcessedString) -> TAUTOLOGY
                Pattern.matches(CONTRADICTION.pattern,preProcessedString) -> CONTRADICTION
                Pattern.matches(OPEN_PARENTHESIS.pattern,preProcessedString) -> OPEN_PARENTHESIS
                Pattern.matches(CLOSE_PARENTHESIS.pattern,preProcessedString) -> CLOSE_PARENTHESIS
                else -> throw IllegalArgumentException("could not find match for: $string")
            }
        }
    }
}
