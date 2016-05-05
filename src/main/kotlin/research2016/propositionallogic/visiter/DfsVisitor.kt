package research2016.propositionallogic.visiter

import java.util.LinkedList

/**
 * visits nodes in depth first search order, and accesses their children using a
 * [NodeAccessStrategy].
 *
 * algorithms that need to iterate through nodes in dfs order should extend this
 * class, and override [visit].
 */
abstract class DfsVisitor<Node>(val nodeAccessStrategy:NodeAccessStrategy<Node>)
{
    /**
     * collection of nodes that have already been visited by this visitor.
     *
     * it is a [List] rather than a [Set] because nodes may be structurally
     * equal, but still referentially different. we are checking for referential
     * difference in this case.
     */
    private val visitedNodes = LinkedList<Node>()

    /**
     * begins the depth first search.
     */
    fun beginTraversal(source:Node)
    {
        assert(visitedNodes.isEmpty(),{"visitor has already traversed a graph. this visitor may not be reused to traverse another graph"})
        traverse(source,null)
    }

    /**
     * algorithm for depth first search traversal of a graph.
     */
    private fun traverse(node:Node,parent:Node?)
    {
        visitedNodes.add(node)
        val children = nodeAccessStrategy.getChildren(node)
        children.forEach()
        {
            child ->
            if (visitedNodes.all {visited -> child !== visited})
            {
                traverse(child,node)
            }
        }
        visit(node,parent,children)
    }

    /**
     * called when visiting each node during the depth first search traversal.
     * [node] is the node that is being visited by the depth first search
     * algorithm. [parent] is the parent of [node] in the depth first search
     * traversal. [children] are all the adjacent nodes of [node]. [parent] will
     * be null for the last node because that is the root node which has no
     * parent.
     */
    protected abstract fun visit(node:Node,parent:Node?,children:List<Node>)

    /**
     * defines functions that this visitor needs to be able to perform on nodes.
     * provides node-specific function implementations.
     */
    interface NodeAccessStrategy<Node>
    {
        /**
         * returns all nodes adjacent to [node].
         */
        fun getChildren(node:Node):List<Node>
    }
}
