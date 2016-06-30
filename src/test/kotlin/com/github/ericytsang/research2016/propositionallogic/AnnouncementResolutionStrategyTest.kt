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
        val announcements = findAllAnnouncements(problems)
        println(announcements)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcements.first()))?.models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcements.first()))!!)
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
        val announcements = findAllAnnouncements(problems)
        println(announcements)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcements.first()))?.models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcements.first()))!!)
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
        val announcements = findAllAnnouncements(problems)
        println(announcements)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcements.first()))?.models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcements.first()))!!)
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
        assert(OrderedAnnouncementResolutionStrategy().resolve(problems) == null)
    }

    /**
     * o..o
     * .xx.
     * .xx.
     * o..o
     */
    @Test
    fun hammingDistance1()
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
        val announcement = OrderedAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement))!!.models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement))!!)
        }
    }

    /**
     * oxx.
     * .ox.
     */
    @Test
    fun hammingDistance2()
    {
        val a = Variable.make("a")
        val b = Variable.make("b")
        val c = Variable.make("c")
        val bot1 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b.not and c.not),
            b and c.not,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val bot2 = AnnouncementResolutionStrategy.ProblemInstance(
            setOf(a.not and b and c),
            a and b and c,
            ComparatorBeliefRevisionStrategy({HammingDistanceComparator(it)}))
        val problems = listOf(bot1,bot2)
        val announcement = OrderedAnnouncementResolutionStrategy().resolve(problems)!!
        println(announcement)
        problems.forEach()
        {
            println(And.make(it.reviseBy(announcement))!!.models)
            assert(it.targetBeliefState isSatisfiedBy And.make(it.reviseBy(announcement))!!)
        }
    }
}
