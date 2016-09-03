package com.github.ericytsang.research2016.propositionallogic

import java.util.NoSuchElementException

/**
 * Created by surpl on 9/2/2016.
 */
fun <T> List<T>.getRandom():T
{
    if (isEmpty()) throw NoSuchElementException("list is empty.")
    return this[(Math.random()*size).toInt()]
}

fun <T> Array<T>.getRandom():T
{
    if (isEmpty()) throw NoSuchElementException("list is empty.")
    return this[(Math.random()*size).toInt()]
}
