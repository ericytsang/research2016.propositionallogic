package com.github.ericytsang.research2016.propositionallogic

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
        val problems = listOf(bot1,bot2)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement)).models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement)))
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
            setOf(breach then checkGate,breach.not then patrol,checkGate xor patrol,breach.not),
            checkGate,
            SatisfiabilityBeliefRevisionStrategy())
        val problems = listOf(bot1,bot2)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement)).models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement)))
        }
    }

    @Test
    fun guardBots3()
    {
        val patrol = Variable.make("patrol")
        val breach = Variable.make("breach")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(patrol and breach),
            patrol,
            SatisfiabilityBeliefRevisionStrategy())
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(patrol.not and breach),
            patrol and breach,
            SatisfiabilityBeliefRevisionStrategy())
        val problems = listOf(bot1,bot2)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement)).models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement)))
        }
    }

    @Test
    fun hammingDistanceImpossible()
    {
        val a = Variable.make("a")
        val b = Variable.make("b")
        val c = Variable.make("c")
        val d = Variable.make("d")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b and c.not and d.not),
            a and b.not and c and d,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a and b and c.not and d.not),
            a.not and b.not and c and d,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot3 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b and c.not and d),
            a and b.not and c and d.not,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot4 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a and b and c.not and d),
            a.not and b.not and c and d.not,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val problems = listOf(bot1,bot2,bot3,bot4)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)
        assert(announcement == null)
    }

    @Test
    fun hammingDistance()
    {
        val a = Variable.make("a")
        val b = Variable.make("b")
        val c = Variable.make("c")
        val d = Variable.make("d")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b and c.not and d.not),
            a.not and b.not and c and d.not,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a and b and c.not and d.not),
            a and b.not and c and d.not,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot3 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b and c.not and d),
            a.not and b.not and c and d,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot4 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a and b and c.not and d),
            a and b.not and c and d,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val problems = listOf(bot1,bot2,bot3,bot4)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement)).models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement)))
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
        val problems = listOf(guard1,guard2,vip)
        val announcement = SimpleAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement)).models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement)))
        }
    }
}
