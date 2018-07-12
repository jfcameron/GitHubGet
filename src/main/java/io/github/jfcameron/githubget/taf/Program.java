package io.github.jfcameron.githubget.taf;

import com.google.common.primitives.Chars;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedded program
 */
public abstract class Program
{
    private Map<String, Program> m_Commands = new HashMap<>();

    /**
     * Entry point for user-defined behaviour of this program
     */
    protected abstract void usermain(List<Parameter> aParameters);

    /**
     * Given a set of parameters, run the user-defined behaviour of the program
     * (plus some boilerplate for handling commands)
     */
    public void run(List<Parameter> aParameters)
    {
        for (int i = 0, s = aParameters.size(); i < s; ++i)
            if (aParameters.get(i) instanceof Parameter.Positional)
                if (m_Commands.containsKey(((Parameter.Positional) aParameters.get(i)).getValue()))
                {
                    final String commandName = ((Parameter.Positional) aParameters.get(i)).getValue();

                    List<Parameter> subcommandParams = new ArrayList<>(aParameters).subList(i, aParameters.size());
                    subcommandParams.remove(0);

                    aParameters.subList(i, aParameters.size()).clear();

                    usermain(aParameters);
                    m_Commands.get(commandName).run(subcommandParams);

                    return;
                }

        usermain(aParameters);
    }

    /**
     * this overload of run takes standard java program args, converting them to
     * the arg types defined in this library
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
                        if (!treatFlagListsAsPositionalParameters)
                            parameters.add(new Parameter.OptionList(Chars.asList(rarg.substring(1, rarg.length()).toCharArray())));
                        else
                            parameters.add(new Parameter.Positional(rarg));
                    else
                        parameters.add(new Parameter.Positional(rarg));
        }

        run(parameters);
    }

    public Program(Map<String, Program> aCommands)
    {
        m_Commands = aCommands;
    }

    public Program()
    {
    }
}
