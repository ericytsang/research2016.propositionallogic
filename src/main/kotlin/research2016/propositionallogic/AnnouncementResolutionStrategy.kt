package research2016.propositionallogic

/**
 * Created by surpl on 6/20/2016.
 */
interface AnnouncementResolutionStrategy
{
    fun resolve(problemInstances:List<ProblemInstance>):Proposition?
    data class ProblemInstance(val initialBeliefState:Set<Proposition>,val targetBeliefState:Proposition,val beliefRevisionStrategy:BeliefRevisionStrategy)
    {
        fun reviseWith(sentence:Proposition):Set<Proposition>
        {
            return beliefRevisionStrategy.revise(initialBeliefState,sentence)
        }
    }
}

class SimpleAnnouncementResolutionStrategy:AnnouncementResolutionStrategy
{
    override fun resolve(problemInstances:List<AnnouncementResolutionStrategy.ProblemInstance>):Proposition?
    {
        // the base announcement is a disjunction of all target belief states
        val baseAnnouncement = problemInstances.map {it.targetBeliefState}.let {Or.make(it)}

        // generate generalized announcements
        val announcements = problemInstances
            // get all underlying input variables
            .flatMap {it.initialBeliefState+it.targetBeliefState}.flatMap {it.variables}
            // only keep the variables that are not mentioned in the base announcement
            .filter {it !in baseAnnouncement.variables}.toSet()
            // generate all the possible states that involve the variables
            .let {State.generateFrom(it)}
            // make a sentence from the state and concatenate it with the base announcement
            .map {baseAnnouncement and Proposition.makeFrom(it)}

        // find the announcement that works and return it; null if none work
        return announcements.find()
        {
            announcement ->
            problemInstances
                // perform the belief revision (K * announcement) for each problem instance
                .associate {problem -> problem.targetBeliefState to problem.reviseWith(announcement)}
                // make sure all target belief states are satisfied by their corresponding revision result
                .let {targetsToResults -> targetsToResults.all {it.key isSatisfiedBy And.make(it.value)}}
        }
    }
}
