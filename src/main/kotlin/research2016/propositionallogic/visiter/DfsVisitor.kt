package research2016.propositionallogic.visiter

import java.util.LinkedList

/**
 * Created by surpl on 5/4/2016.
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

    protected abstract fun visit(node:Node,parent:Node?,children:List<Node>)

    interface NodeAccessStrategy<Node>
    {
        fun getChildren(node:Node):List<Node>
    }
}
