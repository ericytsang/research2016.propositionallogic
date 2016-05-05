package research2016.propositionallogic.core

import research2016.propositionallogic.core.Proposition.AtomicProposition

/**
 * Created by surpl on 5/4/2016.
 */
class BasicProposition(_friendly:String):AtomicProposition(_friendly)
{
    init
    {
        assert(_friendly.length == 1,{"only strings of length 1 are allowed to be used as the friendly string for atomic propositions"})
        assert(_friendly[0].isLetter(),{"atomic proposition must be a letter"})
    }
    override fun truthValue(situation:Situation):Boolean = situation.getValue(this)
}

class Tautology:AtomicProposition("1")
{
    override fun truthValue(situation:Situation):Boolean = true
}

class Contradiction:AtomicProposition("0")
{
    override fun truthValue(situation:Situation):Boolean = false
}
