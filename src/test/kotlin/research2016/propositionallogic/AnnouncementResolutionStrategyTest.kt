package research2016.propositionallogic

import org.junit.Test

/**
 * Created by surpl on 6/21/2016.
 */
class AnnouncementResolutionStrategyTest
{
    @Test
    fun guardBots1()
    {
        val patrol = Variable.make("patrol")
        val breach = Variable.make("breach")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(patrol),
            patrol,
            SatisfiabilityBeliefRevisionStrategy())
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(breach xor patrol,breach.not),
            patrol.not,
            SatisfiabilityBeliefRevisionStrategy())
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(listOf(bot1,bot2))
        println(announcement)
        if (announcement != null)
        {
            println(And.make(bot1.reviseWith(announcement)).models)
            println(And.make(bot2.reviseWith(announcement)).models)
        }
    }

    @Test
    fun guardBots2()
    {
        val patrol = Variable.make("patrol")
        val checkGate = Variable.make("checkGate")
        val breach = Variable.make("breach")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(patrol,checkGate xor patrol),
            patrol,
            SatisfiabilityBeliefRevisionStrategy())
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(breach oif checkGate,breach.not oif patrol,checkGate xor patrol,breach.not),
            checkGate,
            SatisfiabilityBeliefRevisionStrategy())
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(listOf(bot1,bot2))
        println(announcement)
        if (announcement != null)
        {
            println(And.make(bot1.reviseWith(announcement)).models)
            println(And.make(bot2.reviseWith(announcement)).models)
        }
    }

    @Test
    fun protectTheVip()
    {
        val threadDetected = Variable.make("threadDetected")
        val fightThreat = Variable.make("fightThreat")
        val runAway = Variable.make("runAway")
        val helpVipEscape = Variable.make("helpVipEscape")
        val scanForThreats = Variable.make("scanForThreats")
        val eatFood = Variable.make("eatFood")
        val guard1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(threadDetected xor scanForThreats,fightThreat iff threadDetected,threadDetected.not,eatFood.not,helpVipEscape.not,runAway.not),
            fightThreat,
            SatisfiabilityBeliefRevisionStrategy())
        val guard2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(threadDetected xor scanForThreats,helpVipEscape iff threadDetected,threadDetected.not,eatFood.not,fightThreat.not,runAway.not),
            helpVipEscape,
            SatisfiabilityBeliefRevisionStrategy())
        val vip = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(threadDetected xor eatFood,runAway iff threadDetected,threadDetected.not,scanForThreats.not,fightThreat.not,helpVipEscape.not),
            runAway,
            SatisfiabilityBeliefRevisionStrategy())
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(listOf(guard1,guard2,vip))
        println(announcement)
        if (announcement != null)
        {
            println(And.make(guard1.reviseWith(announcement)).models)
            println(And.make(guard2.reviseWith(announcement)).models)
            println(And.make(vip.reviseWith(announcement)).models)
        }
    }
}
