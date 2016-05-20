package research2016.propositionallogic

import lib.collections.getRandom

/**
 * Created by surpl on 5/19/2016.
 */
private enum class Operator(val numOperands:Int,val generator:(List<Proposition>)->Proposition)
{
    NOT(1,{Not(it.single())}),
    AND(2,{And(it.first(),it.last())}),
    OR(2,{Or(it.first(),it.last())});
    //IFF(2,{Iff(it.first(),it.last())}),
    //THEN(2,{Oif(it.first(),it.last())}),
    //XOR(2,{Xor(it.first(),it.last())}),
    //NAND(2,{Nand(it.first(),it.last())});
}

fun Proposition.Companion.makeRandom(basicPropositions:List<BasicProposition>):Proposition
{
    val scrambledBasicPropositions = basicPropositions.sortedBy {Math.random()}
    loop@ while (true)
    {
        if (scrambledBasicPropositions.size == 1)
        {
            return scrambledBasicPropositions.single()
        }
        else
        {
            assert(scrambledBasicPropositions.isNotEmpty(),{throw IllegalArgumentException("cannot pass in empty list of basic propositions")})
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
