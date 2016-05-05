package research2016.propositionallogic.visiter

import java.util.LinkedHashSet

/**
 * Created by surpl on 5/4/2016.
 */
abstract class DfsVisitor<Node>(val nodeAccessStrategy:NodeAccessStrategy<Node>)
{
    /**
     * set of nodes that have already been visited by this visitor.
     */
    private val visitedNodes:MutableSet<Node> = LinkedHashSet()

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
        nodeAccessStrategy.getChildren(node).forEach()
        {
            child ->
            if (visitedNodes.all {child !== it})
            {
                traverse(child,node)
            }
        }
        visit(node,parent)
    }

    protected abstract fun visit(node:Node,parent:Node?)

    interface NodeAccessStrategy<Node>
    {
        fun getChildren(node:Node):List<Node>
    }
}
