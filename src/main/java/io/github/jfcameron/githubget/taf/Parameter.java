package io.github.jfcameron.githubget.taf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * positional argument: {value}, option {name}, longoption {name, value} How to
 * define option with value? if param type is list && contains option && next
 * param is positional then name is option, value is positional
 */
public abstract class Parameter
{
    public static class List
    {
        private final java.util.List<Parameter> m_Parameters = new ArrayList<>();
        
        protected List()
        {
            
        }
    }
    
    public static class Positional extends Parameter
    {
        private final String m_Value;

        public String getValue()
        {
            return m_Value;
        }

        protected Positional(final String aValue)
        {
            m_Value = aValue;
        }

        @Override
        public String toString()
        {
            return m_Value;
        }
    }

    public static class LongOption extends Parameter
    {
        private final String m_Name;

        public String getName()
        {
            return m_Name;
        }

        protected LongOption(final String aName)
        {
            m_Name = aName;
        }

        @Override
        public String toString()
        {
            return "--" + m_Name;
        }
    }

    public static class LongOptionWithValue extends Parameter
    {
        private final String m_Name;

        private final String m_Value;

        public String getName()
        {
            return m_Name;
        }

        public String getValue()
        {
            return m_Value;
        }

        protected LongOptionWithValue(final String aName, final String aValue)
        {
            m_Name = aName;

            m_Value = aValue;
        }

        @Override
        public String toString()
        {
            return "--" + m_Name + "=" + m_Value;
        }
    }

    public static class OptionList extends Parameter
    {
        private final java.util.List<Character> m_Options;

        public Character get(int aIndex)
        {
            return m_Options.get(aIndex);
        }

        public int size()
        {
            return m_Options.size();
        }

        public void forEach(Consumer<? super Character> action)
        {
            m_Options.forEach(action);
        }

        public boolean contains(final Character aChar)
        {
            return m_Options.contains(aChar);
        }

        protected OptionList(java.util.List<Character> aOptions)
        {
            m_Options = aOptions;
        }

        @Override
        public String toString()
        {
            String buffer = "-";

            for (final Character option : m_Options)
                buffer += option;

            return "--" + buffer;
        }
    }
}
