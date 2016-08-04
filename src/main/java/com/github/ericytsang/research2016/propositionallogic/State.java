package com.github.ericytsang.research2016.propositionallogic;

import com.github.ericytsang.lib.collections.CollectionWrapper;
import com.github.ericytsang.lib.collections.Function;

import java.io.Serializable;
import java.util.*;

public class State implements Map<Variable,Boolean>,Serializable
{
    private final Map<Variable,Boolean> truthValues;

    // constructor

    private State(Map<Variable,Boolean> truthValues)
    {
        this.truthValues = truthValues;
    }

    // factory methods

    public static State fromVariableMap(Map<Variable,Boolean> truthValues)
    {
        return new State(truthValues);
    }

    public static State fromStringMap(Map<String,Boolean> truthValues)
    {
        Map<Variable,Boolean> variableMap = new HashMap<Variable,Boolean>(truthValues.size());
        for(Entry<String,Boolean> entry : truthValues.entrySet())
        {
            Variable key = Variable.fromString(entry.getKey());
            variableMap.put(key,entry.getValue());
        }
        return new State(variableMap);
    }

    public static Set<State> permutationsOf(Set<Variable> variables)
    {
        int numStatesToGenerate = (int) Math.round(Math.pow(2.0,variables.size()));
        LinkedHashSet<State> allSituations = new LinkedHashSet<State>();
        ArrayList<String> propositionKeys = new ArrayList<String>(variables.size());
        for (Variable variable : variables)
        {
            propositionKeys.add(variable.toString());
        }
        Collections.sort(propositionKeys);
        for (int seed = 0;seed != numStatesToGenerate;seed++)
        {
            String string = String.format("%"+variables.size()+"s",Integer.toBinaryString(seed)).replace(' ','0');
            HashMap<String,Boolean> stringMap = new HashMap<String,Boolean>(propositionKeys.size());
            for (int i = 0;i < propositionKeys.size();i++)
            {
                String s = propositionKeys.get(i);
                stringMap.put(s,string.charAt(i) == '1');
            }
            State newState = State.fromStringMap(stringMap);
            allSituations.add(newState);
        }

        if(allSituations.size() != numStatesToGenerate)
        {
            throw new RuntimeException("failed to generate all states! D: situations generated: $allSituations");
        }

        return allSituations;
    }

    public String toString()
    {
        return CollectionWrapper.wrap(entrySet())
            .map(new Function<Entry<Variable,Boolean>,Entry<Variable,Boolean>>()
            {
                @Override
                public Entry<Variable,Boolean> invoke(Entry<Variable,Boolean> params)
                {
                    return params;
                }
            })
            .apply(new Function<CollectionWrapper<List<Entry<Variable,Boolean>>,Entry<Variable,Boolean>>,Void>()
            {
                @Override
                public Void invoke(CollectionWrapper<List<Entry<Variable,Boolean>>,Entry<Variable,Boolean>> params)
                {
                    Collections.sort(params.wrapee,new Comparator<Entry<Variable,Boolean>>()
                    {
                        @Override
                        public int compare(Entry<Variable,Boolean> o1,Entry<Variable,Boolean> o2)
                        {
                            return o1.getKey().toString().compareTo(o2.getKey().toString());
                        }
                    });
                    return null;
                }
            })
            .filter(new Function<Entry<Variable,Boolean>,Boolean>()
            {
                @Override
                public Boolean invoke(Entry<Variable,Boolean> params)
                {
                    return params.getValue();
                }
            })
            .map(new Function<Entry<Variable,Boolean>,String>()
            {
                @Override
                public String invoke(Entry<Variable,Boolean> params)
                {
                    return params.getKey().toString();
                }
            })
            .let(new Function<CollectionWrapper<List<String>,String>,String>()
            {
                @Override
                public String invoke(CollectionWrapper<List<String>,String> params)
                {
                    StringBuilder stb = new StringBuilder();
                    for(int i = 0;i < params.size();i++)
                    {
                        stb.append(i==0?"{"+params.wrapee.get(i):", "+params.wrapee.get(i));
                    }
                    stb.append('}');
                    return stb.toString();
                }
            });
    }

    // Map implementation

    @Override
    public int size()
    {
        return truthValues.size();
    }

    @Override
    public boolean isEmpty()
    {
        return truthValues.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return truthValues.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return truthValues.containsValue(value);
    }

    @Override
    public Boolean get(Object key)
    {
        return truthValues.get(key);
    }

    @Override
    public Boolean put(Variable key,Boolean value)
    {
        return truthValues.put(key,value);
    }

    @Override
    public Boolean remove(Object key)
    {
        return truthValues.remove(key);
    }

    @Override
    public void putAll(Map<? extends Variable,? extends Boolean> m)
    {
        truthValues.putAll(m);
    }

    @Override
    public void clear()
    {
        truthValues.clear();
    }

    @Override
    public Set<Variable> keySet()
    {
        return truthValues.keySet();
    }

    @Override
    public Collection<Boolean> values()
    {
        return truthValues.values();
    }

    @Override
    public Set<Entry<Variable,Boolean>> entrySet()
    {
        return truthValues.entrySet();
    }

    @Override
    public boolean equals(Object o)
    {
        return truthValues.equals(o);
    }

    @Override
    public int hashCode()
    {
        return truthValues.hashCode();
    }
}
