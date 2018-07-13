package io.github.jfcameron.githubget.taf;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * OptionListWithValue
 */
public abstract class Parameter
{
    public static class List//<T extends Parameter>
    {
        private final java.util.List<Parameter> m_Parameters;

        /**
         * default ctor
         */
        protected List()
        {
            m_Parameters = new LinkedList<>();
        }

        /**
         * Deep copy of another paramlist
         *
         * @param aOther
         */
        protected List(final Parameter.List aOther)
        {
            m_Parameters = new LinkedList<>(aOther.m_Parameters);
        }

        /**
         * View constructor. Does NOT make a deep copy of params
         *
         * @param aParameters
         */
        protected List(final java.util.List aParameters)
        {
            m_Parameters = aParameters;
        }

        /**
         * @return total number of params
         */
        public int size()
        {
            return m_Parameters.size();
        }

        /**
         * @param aIndex of param to get
         * @return the param at aIndex
         */
        protected Parameter get(final int aIndex)
        {
            return m_Parameters.get(aIndex);
        }

        /**
         * @param aParam the param to add
         * @return returns true
         */
        protected boolean add(final Parameter aParam)
        {
            return m_Parameters.add(aParam);
        }

        /**
         * @param aIndex index of the param to remove
         * @return the param that was removed
         */
        protected Parameter remove(final int aIndex)
        {
            return m_Parameters.remove(aIndex);
        }

        /**
         * creates a shallow copy of a subsection of the list
         *
         * @param aFirst start of subsection
         * @param aLast end of subsection
         * @return
         */
        protected Parameter.List subList(final int aFirst, final int aLast)
        {
            return new List(m_Parameters.subList(aFirst, aLast));
        }

        /**
         * removes all parameters from the list
         */
        protected void clear()
        {
            m_Parameters.clear();
        }

        public void forEach(Consumer<? super Parameter> action)
        {
            m_Parameters.forEach(action);
        }

        public boolean containsLongOption(final String aName)
        {
            final java.util.List<LongOption> longOptions = new java.util.ArrayList<>();

            m_Parameters.forEach((param) ->
            {
                if (param instanceof LongOption)
                    longOptions.add((LongOption) param);
            });

            return longOptions.stream().anyMatch((longOption) -> (longOption.getName().equals(aName)));
        }

        public boolean containsOption(final Character aName)
        {
            final java.util.List<Character> options = new java.util.ArrayList<>();

            m_Parameters.forEach((param) ->
            {
                if (param instanceof OptionList)
                    ((OptionList) param).forEach((option) ->
                    {
                        options.add(option);
                    });
            });

            return options.stream().anyMatch((option) -> (option.equals(aName)));
        }

        public boolean containsPositional(final String aValue)
        {
            final java.util.List<Positional> positionals = new java.util.ArrayList<>();

            m_Parameters.forEach((param) ->
            {
                if (param instanceof Positional)
                    positionals.add((Positional) param);
            });

            return positionals.contains(new Positional(aValue));
        }

        public String getPositional(final int aPosition)
        {
            final java.util.List<Positional> positionals = new java.util.ArrayList<>();

            m_Parameters.forEach((Parameter param) ->
            {
                if (param instanceof Positional)
                    positionals.add((Positional) param);
            });

            return aPosition >= positionals.size() ? null
                    : positionals.get(aPosition) == null ? null
                    : positionals.get(aPosition).getValue();
        }

        //====================================================================================================
        //???
        private java.util.List<LongOptionWithValue> getLongOptionsWithValues()
        {
            final java.util.List<LongOptionWithValue> longOptionsWithValue = new java.util.ArrayList<>();

            m_Parameters.forEach((param) ->
            {
                if (param instanceof LongOptionWithValue)
                    longOptionsWithValue.add((LongOptionWithValue) param);
            });

            return longOptionsWithValue;
        }

        private java.util.List<Positional> getOptionListsWithValues()
        {
            throw new RuntimeException("getOptionListsWithValues currently not supported");
            /*final java.util.List<Positional> positionals = new java.util.ArrayList<>();
            
            m_Parameters.forEach((param) ->
            {
                if (param instanceof Positional)
                    positionals.add((Positional) param);
            });

            return positionals;*/
        }
        //====================================================================================================
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

    public static class OptionListWithValue extends OptionList
    {
        private final String m_Value;

        public String getValue()
        {
            return m_Value;
        }

        protected OptionListWithValue(java.util.List<Character> aOptions, final String aValue)
        {
            super(aOptions);

            m_Value = aValue;
        }
    }
}
