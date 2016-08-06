package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.AbstractIterator;
import com.github.ericytsang.lib.collections.Bounds;
import com.github.ericytsang.lib.collections.BranchAndBound;
import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.ContainerUtils;
import com.github.ericytsang.lib.collections.Function;
import com.github.ericytsang.lib.collections.IteratorToSetAdapter;
import com.github.ericytsang.lib.collections.NodeMetadata;
import com.github.ericytsang.lib.formulainterpreter.FormulaTreeFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by surpl on 7/26/2016.
 */
public abstract class Proposition implements Serializable
{
    public static final Companion Companion = new Companion();

    public static class Companion
    {
        private Companion()
        {}
    }

    public static final Proposition TAUTOLOGY = new Operand("1")
    {
        @Override
        boolean truthValue(State state)
        {
            return true;
        }
    };

    public static final Proposition CONTRADICTION = new Operand("0")
    {
        @Override
        boolean truthValue(State state)
        {
            return false;
        }
    };

    public static Proposition parse(String string)
    {
        String[] tokens = string.replace("("," ( ").replace(")"," ) ").replace("-"," - ").trim().split("[ ]+");
        List<String> allTokens = new ArrayList<String>();
        for (String token : tokens)
        {
            allTokens.add(token);
        }
        return propositionFactory.parse(allTokens);
    }

    public static Proposition makeDnf(final State state)
    {
        return CollectionWrapper.wrap(state.keySet())
            .map(new Function<Variable,Proposition>()
            {
                @Override
                public Proposition invoke(Variable params)
                {
                    if (state.get(params))
                    {
                        return params;
                    }
                    else
                    {
                        return params.not();
                    }
                }
            })
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    Proposition result = And.make(params.wrapee);
                    return result != null ? result : Proposition.TAUTOLOGY;
                }
            });
    }

    public static Proposition makeDnf(Iterable<State> states)
    {
        CollectionWrapper<LinkedList<State>,State> collectionWrapper = CollectionWrapper.wrap(new LinkedList<State>());
        for (State state : states)
        {
            collectionWrapper.add(state);
        }
        return collectionWrapper
            .map(new Function<State,Proposition>()
            {
                @Override
                public Proposition invoke(State params)
                {
                    return Proposition.makeDnf(params);
                }
            })
            .let(new Function<CollectionWrapper<List<Proposition>,Proposition>,Proposition>()
            {
                @Override
                public Proposition invoke(CollectionWrapper<List<Proposition>,Proposition> params)
                {
                    Proposition result = Or.make(params.wrapee);
                    return result != null ? result : Proposition.CONTRADICTION;
                }
            });
    }

    public abstract List<Proposition> getChildren();

    public Set<Variable> getVariables()
    {
        if(this instanceof Variable)
        {
            Set<Variable> set = new HashSet<Variable>();
            set.add((Variable) this);
            return set;
        }
        else
        {
            return CollectionWrapper.wrap(getChildren())
                .flatMap(new Function<Proposition,Collection<Variable>>()
                {
                    @Override
                    public Collection<Variable> invoke(Proposition params)
                    {
                        return params.getVariables();
                    }
                })
                .let(new Function<CollectionWrapper<List<Variable>,Variable>,Set<Variable>>()
                {
                    @Override
                    public Set<Variable> invoke(CollectionWrapper<List<Variable>,Variable> params)
                    {
                        return new HashSet<Variable>(params);
                    }
                });
        }
    }

    public synchronized Set<State> getModels()
    {
        final BranchAndBound<State> findModel = new BranchAndBound<State>()
        {
            @Override
            protected Set<State> getChildren(final State state)
            {
                final Variable nextVariable = CollectionWrapper.wrap(getVariables())
                    .filter(new Function<Variable,Boolean>()
                    {
                        @Override
                        public Boolean invoke(Variable params)
                        {
                            return !state.containsKey(params);
                        }
                    })
                    .firstOrNull();
                if (nextVariable != null)
                {
                    State node1 = new Function<Void,State>()
                    {
                        @Override
                        public State invoke(Void params)
                        {
                            Map<Variable,Boolean> map = new HashMap<Variable,Boolean>(state.size()+1);
                            map.putAll(state);
                            map.put(nextVariable,true);
                            return State.fromVariableMap(map);
                        }
                    }.invoke(null);
                    State node2 = new Function<Void,State>()
                    {
                        @Override
                        public State invoke(Void params)
                        {
                            Map<Variable,Boolean> map = new HashMap<Variable,Boolean>(state.size()+1);
                            map.putAll(state);
                            map.put(nextVariable,false);
                            return State.fromVariableMap(map);
                        }
                    }.invoke(null);
                    return CollectionWrapper.wrap(ContainerUtils.listOf(node1,node2))
                        .filter(new Function<State,Boolean>()
                        {
                            @Override
                            public Boolean invoke(State params)
                            {
                                return truthiness(params) != 0.0;
                            }
                        })
                        .toSet().wrapee;
                }
                else
                {
                    return ContainerUtils.emptySet();
                }
            }

            @Override
            protected Bounds getBounds(State state)
            {
                return new Bounds(1.0,Math.floor(truthiness(state)));
            }

            @Override
            protected Boolean isSolution(State state)
            {
                return truthiness(state) == 1.0 && state.keySet().containsAll(getVariables());
            }
        };

        Iterator<State> iterator = new AbstractIterator<State>()
        {
            private Map<State,NodeMetadata> unbranchedNodes = new HashMap<State,NodeMetadata>();

            {
                unbranchedNodes.put(State.fromStringMap(ContainerUtils.<String,Boolean>emptyMap()),BranchAndBound.ROOT_NODE_METADATA);
            }

            @Override
            protected void computeNext()
            {
                State next = findModel.runAlgorithm(unbranchedNodes);
                if (next == null)
                {
                    done();
                }
                else
                {
                    setNext(next);
                }
            }

            @Override
            protected void doRemove(int indexToRemove,State toRemove)
            {
                throw new UnsupportedOperationException("objects are generated. collection is immutable");
            }
        };

        return new IteratorToSetAdapter<State>(iterator);
    }

    /**
     * evaluates if {@code this} is a subset of {@code that}.
     * @param that the {@code Proposition} instance to test against.
     * @return true if {@code this} is a subset of {@code that}.
     */
    public boolean isSubsetOf(Proposition that)
    {
        return this.not().and(this).isContradiction();
    }

    public boolean isSatisfiedBy(Proposition that)
    {
        if (!isSubsetOf(that))
        {
            return false;
        }

        for (State model : getModels())
        {
            if (!Proposition.makeDnf(model).and(that).isSatisfiable())
            {
                return false;
            }
        }
        return true;
    }

    public boolean isSatisfiable()
    {
        return !getModels().isEmpty();
    }

    public boolean isTautology()
    {
        return !not().isSatisfiable();
    }

    public boolean isContradiction()
    {
        return !isSatisfiable();
    }

    public boolean evaluate(final State state)
    {
        if (this instanceof Operand)
        {
            return ((Operand) this).truthValue(state);
        }
        else if (this instanceof Operator)
        {
            return CollectionWrapper.wrap(getChildren())
                .map(new Function<Proposition,Boolean>()
                {
                    @Override
                    public Boolean invoke(Proposition params)
                    {
                        return params.evaluate(state);
                    }
                })
                .let(new Function<CollectionWrapper<List<Boolean>,Boolean>,Boolean>()
                {
                    @Override
                    public Boolean invoke(CollectionWrapper<List<Boolean>,Boolean> params)
                    {
                        return ((Operator) Proposition.this).operateBooleans(params.wrapee);
                    }
                });
        }
        else
        {
            throw new IllegalArgumentException("unhandled type: "+this);
        }
    }

    public double truthiness(final State state)
    {
        if (this instanceof Variable)
        {
            if (state.keySet().contains((Variable) this))
            {
                if (((Variable) this).truthValue(state))
                {
                    return 1.0;
                }
                else
                {
                    return 0.0;
                }
            }
            else
            {
                return 0.5;
            }
        }
        else if (this instanceof Operand)
        {
            if (this == Proposition.TAUTOLOGY)
            {
                return 1.0;
            }
            else if(this == Proposition.CONTRADICTION)
            {
                return 0.0;
            }
            else
            {
                throw new RuntimeException("unhandled type: "+this);
            }
        }
        else if (this instanceof Operator)
        {
            return CollectionWrapper.wrap(getChildren())
                .map(new Function<Proposition,Double>()
                {
                    @Override
                    public Double invoke(Proposition params)
                    {
                        return params.truthiness(state);
                    }
                })
                .let(new Function<CollectionWrapper<List<Double>,Double>,Double>()
                {
                    @Override
                    public Double invoke(CollectionWrapper<List<Double>,Double> params)
                    {
                        return ((Operator) Proposition.this).operateDoubles(params.wrapee);
                    }
                });
        }
        else
        {
            throw new RuntimeException("unhandled type: "+this);
        }
    }

    public Proposition not()
    {
        return new Not(this);
    }

    public Proposition and(Proposition that)
    {
        ArrayList<Proposition> list = new ArrayList<Proposition>(2);
        list.add(this);
        list.add(that);
        return And.make(list);
    }

    public Proposition or(Proposition that)
    {
        ArrayList<Proposition> list = new ArrayList<Proposition>(2);
        list.add(this);
        list.add(that);
        return Or.make(list);
    }

    public String toParsableString()
    {
        if (this instanceof Operand)
        {
            return this.toString();
        }
        else if (this instanceof Or)
        {
            return CollectionWrapper.wrap(getChildren())
                .map(new Function<Proposition,String>()
                {
                    @Override
                    public String invoke(Proposition params)
                    {
                        if (params.getChildren().size() > 1)
                        {
                            return "("+params.toParsableString()+")";
                        }
                        else
                        {
                            return params.toParsableString();
                        }
                    }
                })
                .let(new Function<CollectionWrapper<List<String>,String>,String>()
                {
                    @Override
                    public String invoke(CollectionWrapper<List<String>,String> params)
                    {
                        return params.wrapee.get(0)+" or "+params.wrapee.get(1);
                    }
                });
        }
        else if (this instanceof And)
        {
            return CollectionWrapper.wrap(getChildren())
                .map(new Function<Proposition,String>()
                {
                    @Override
                    public String invoke(Proposition params)
                    {
                        if (params.getChildren().size() > 1)
                        {
                            return "("+params.toParsableString()+")";
                        }
                        else
                        {
                            return params.toParsableString();
                        }
                    }
                })
                .let(new Function<CollectionWrapper<List<String>,String>,String>()
                {
                    @Override
                    public String invoke(CollectionWrapper<List<String>,String> params)
                    {
                        return params.wrapee.get(0)+" and "+params.wrapee.get(1);
                    }
                });
        }
        else if (this instanceof Not)
        {
            return CollectionWrapper.wrap(getChildren())
                .map(new Function<Proposition,String>()
                {
                    @Override
                    public String invoke(Proposition params)
                    {
                        if (params.getChildren().size() > 1)
                        {
                            return "("+params.toParsableString()+")";
                        }
                        else
                        {
                            return params.toParsableString();
                        }
                    }
                })
                .let(new Function<CollectionWrapper<List<String>,String>,String>()
                {
                    @Override
                    public String invoke(CollectionWrapper<List<String>,String> params)
                    {
                        return "-"+params.wrapee.get(0);
                    }
                });
        }
        else
        {
            throw new IllegalArgumentException("unknown type: "+this);
        }
    }

    @Override
    public abstract String toString();

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof Proposition && other.toString().equals(toString());
    }

    private static final FormulaTreeFactory.TokenInterpreter tokenInterpreter =
        new FormulaTreeFactory.TokenInterpreter()
    {
        @Override
        public FormulaTreeFactory.Symbol parse(String word)
        {
            String preprocessedWord = word.toLowerCase();
            if (Pattern.matches("(and)",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,4);
            }
            else if (Pattern.matches("(or)",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,2,3);
            }
            else if (Pattern.matches("(0)",preprocessedWord) ||
                Pattern.matches("(1)",preprocessedWord) ||
                Pattern.matches("[a-zA-Z]+",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERAND,0,0);
            }
            else if (Pattern.matches("(-)",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPERATOR,1,5);
            }
            else if (Pattern.matches("(\\()",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.OPENING_PARENTHESIS,0,0);
            }
            else if (Pattern.matches("(\\))",preprocessedWord))
            {
                return new FormulaTreeFactory.Symbol(FormulaTreeFactory.Type.CLOSING_PARENTHESIS,0,0);
            }
            else
            {
                throw new IllegalArgumentException("unrecognized token: "+word);
            }
        }
    };

    private static final FormulaTreeFactory.OperandFactory<Proposition> operandFactory =
        new FormulaTreeFactory.OperandFactory<Proposition>()
    {
        @Override
        public Proposition parse(String word)
        {
            if (Pattern.matches("(1)",word))
            {
                return Proposition.TAUTOLOGY;
            }
            else if (Pattern.matches("(0)",word))
            {
                return Proposition.CONTRADICTION;
            }
            else if (Pattern.matches("[a-zA-Z]+",word))
            {
                return Variable.fromString(word);
            }
            else
            {
                throw new IllegalArgumentException("unrecognized token: $word");
            }
        }

        @Override
        public Proposition parse(String word,List<Proposition> operands)
        {
            String preprocessedWord = word.toLowerCase();
            if(Pattern.matches("(and)",preprocessedWord))
            {
                return And.make(operands);
            }
            else if(Pattern.matches("(or)",preprocessedWord))
            {
                return Or.make(operands);
            }
            else if(Pattern.matches("(-)",preprocessedWord))
            {
                return new Not(operands.get(0));
            }
            else
            {
                throw new IllegalArgumentException("unrecognized token: $word");
            }
        }
    };

    private static final FormulaTreeFactory<Proposition> propositionFactory =
        new FormulaTreeFactory<Proposition>(tokenInterpreter,operandFactory);
}
