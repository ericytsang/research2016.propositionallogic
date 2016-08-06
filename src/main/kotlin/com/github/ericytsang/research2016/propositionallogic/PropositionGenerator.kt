package com.github.ericytsang.research2016.propositionallogic

import com.github.ericytsang.lib.collections.getRandom

/**
 * Created by surpl on 5/19/2016.
 */
private enum class Operator(val numOperands:Int,val generator:(List<Proposition>)->Proposition)
{
    NOT(1,{Not(it.single())}),
    AND(2,{And.make(it)!!}),
    OR(2,{Or.make(it)!!});
    //IFF(2,{Iff(it.first(),it.last())}),
    //THEN(2,{Oif(it.first(),it.last())}),
    //XOR(2,{Xor(it.first(),it.last())}),
    //NAND(2,{Nand(it.first(),it.last())});
}

fun Proposition.Companion.makeRandom(variables:List<Variable>):Proposition
{
    val scrambledBasicPropositions = variables.sortedBy {Math.random()}
    loop@ while (true)
    {
        if (scrambledBasicPropositions.size == 1)
        {
            return scrambledBasicPropositions.single()
        }
        else
        {
            if(scrambledBasicPropositions.isEmpty())
            {
                throw IllegalArgumentException("cannot pass in empty list of basic propositions")
            }
            val randomOperator = Operator.values().getRandom()
            if (scrambledBasicPropositions.size < randomOperator.numOperands)
            {
                continue@loop
            }
            val iterator = scrambledBasicPropositions.iterator()
            val lists = Array(randomOperator.numOperands,{mutableListOf(iterator.next())})
            while (iterator.hasNext())
            {
                lists[(Math.random()*lists.size).toInt()].add(iterator.next())
            }
            return randomOperator.generator(lists.map {makeRandom(it)})
        }
    }
}
