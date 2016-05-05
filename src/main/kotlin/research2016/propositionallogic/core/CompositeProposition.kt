package research2016.propositionallogic.core

import research2016.propositionallogic.core.Proposition.UnaryOperator
import research2016.propositionallogic.core.Proposition.BinaryOperator

/**
 * Created by surpl on 5/4/2016.
 */

class Not(operand:Proposition):UnaryOperator(operand,"¬",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            true to false,
            false to true
        )
    }
}

class And(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∧",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to false,
            false to true  to false,
            true  to false to false,
            true  to true  to true
        )
    }
}

class Or(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"∨",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to false,
            false to true  to true,
            true  to false to true,
            true  to true  to true
        )
    }
}

class Oif(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"→",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to true,
            false to true  to true,
            true  to false to false,
            true  to true  to true
        )
    }
}

class Iff(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"↔",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to true,
            false to true  to false,
            true  to false to false,
            true  to true  to true
        )
    }
}

class Xor(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"⊕",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to false,
            false to true  to true,
            true  to false to true,
            true  to true  to false
        )
    }
}

class Nand(leftOperand:Proposition,rightOperand:Proposition):BinaryOperator(leftOperand,rightOperand,"|",Companion.truthTable)
{
    companion object
    {
        val truthTable = mapOf(
            false to false to true,
            false to true  to true,
            true  to false to true,
            true  to true  to false
        )
    }
}
