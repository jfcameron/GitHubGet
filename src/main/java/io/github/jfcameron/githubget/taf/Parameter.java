package io.github.jfcameron.githubget.taf;

/**
 * positional argument: {value}, option {name}, longoption {name, value}
 */
public abstract class Parameter
{
    public static class Positional extends Parameter
    {
        public Positional(final String aName)
        {
            super(aName);
        }
    }

    public static class Option extends Parameter
    {
        protected Option(final char aName)
        {
            super(Character.toString(aName));
        }
    }

    public static class LongOption extends Parameter
    {
        protected LongOption(final String aName)
        {
            super(aName);
        }
    }

    public static class LongOptionWithValue extends Parameter
    {
        private final String m_Value;

        public String getValue()
        {
            return m_Value;
        }

        protected LongOptionWithValue(final String aName, final String aValue)
        {
            super(aName);

            m_Value = aValue;
        }
    }

    private final String m_Name;

    public String getName()
    {
        return m_Name;
    }

    protected Parameter(final String aName)
    {
        m_Name = aName;
    }
}
