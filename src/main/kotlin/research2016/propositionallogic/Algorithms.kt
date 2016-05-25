package research2016.propositionallogic

fun <Node> branchAndBound(initialNode:Node,branch:(Node)->Map<Node,Double>,checkSolution:(Node)->Boolean):Node?
{
    return branchAndBound(mutableMapOf(initialNode to 0.0),branch,checkSolution)
}

/**
 * the branch and bound algorithm...
 *
 * the [unbranchedNodes] parameter is a map of [Node]s to [Double]s. the
 * [Double] value represents that [Node]'s potential to be the solution. the
 * [Node]s in this collection will be passed to the supplied [branch] function.
 *
 * the [branch] function takes a [Node], and returns a map of [Node]s to
 * [Double]s. the [Double] value represents that [Node]'s potential to be the
 * solution.
 *
 * the [checkSolution] function is passed a [Node], and should return true if
 * the [Node] is the solution, and false otherwise.
 *
 * the function will return the first [Node] that [checkSolution] returns true
 * for. if [checkSolution] returns for all [Node]s, then the function will
 * return null.
 */
fun <Node> branchAndBound(unbranchedNodes:MutableMap<Node,Double>,branch:(Node)->Map<Node,Double>,checkSolution:(Node)->Boolean):Node?
{
    var bestCandidate:Node?

    // until we find the solution...or exhaust all options
    do
    {
        // update the best candidate reference
        bestCandidate = unbranchedNodes.maxBy {it.value}?.key ?: return null

        // branch the best candidate
        val newNodes = branch(bestCandidate)
        unbranchedNodes.remove(bestCandidate)

        // check the potentials of the new nodes
        unbranchedNodes.putAll(newNodes)
    }
    while (bestCandidate != null && !checkSolution(bestCandidate))

    return bestCandidate
}
