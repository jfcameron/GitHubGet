package io.github.jfcameron.githubget.taf;

import java.util.ArrayList;
import java.util.List;

/**
 * An embedded program
 */
public abstract class Program
{
    //private Map<String, Program> m_Children;

    /**
     * Entry point for this program
     */
    public abstract void run(List<Parameter> aParameters);

    /**
     * overload that takes raw java args list: bootstraps the arg typing
     */
    public void run(String[] aRawArgs)
    {
        final List<Parameter> parameters = new ArrayList<>();

        boolean treatFlagListsAsPositionalParameters = false;

        for (final String rarg : aRawArgs)
        {
            if (rarg.length() == 1 && rarg.startsWith("-"))
                throw new RuntimeException("Invalid Option List: zero length");

            if ("--".equals(rarg))
                treatFlagListsAsPositionalParameters = true;
            else
                if (rarg.length() > 2 && rarg.startsWith("--"))
                {
                    String[] namevalsplit = rarg.substring(2, rarg.length()).split("=", 2);

                    switch (namevalsplit.length)
                    {
                        case 1:
                            parameters.add(new Parameter.LongOption(namevalsplit[0]));
                            break;

                        case 2:
                            parameters.add(new Parameter.LongOptionWithValue(namevalsplit[0], namevalsplit[1]));
                            break;

                        default:
                            throw new RuntimeException("Error while parsing a LongOption: " + rarg);
                    }
                }
                else
                    if (rarg.length() > 1 && rarg.startsWith("-"))
                        if (treatFlagListsAsPositionalParameters == false)
                            for (final char option : rarg.substring(1, rarg.length()).toCharArray())
                                parameters.add(new Parameter.Option(option));
                        else
                            parameters.add(new Parameter.Positional(rarg));
                    else
                        parameters.add(new Parameter.Positional(rarg));
        }

        run(parameters);
    }

    /*protected Program(Map<String, Program> aChildren)
    {
        m_Children = aChildren;
    }*/
}
