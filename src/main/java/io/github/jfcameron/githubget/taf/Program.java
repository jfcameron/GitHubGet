package io.github.jfcameron.githubget.taf;

import com.google.common.primitives.Chars;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedded program
 */
public abstract class Program
{
    private Map<String, Program> m_Commands = new HashMap<>();

    private final String m_Name;

    private final String m_Description;

    /**
     * Entry point for user-defined behaviour of this program
     */
    protected abstract void usermain(Parameter.List aParameters);

    private void main(Parameter.List aParameters)
    {
        if (aParameters.containsOption('h') || aParameters.containsLongOption("help"))
        {
            System.out.println("Current command: " + m_Name);
            System.out.println("Description: " + m_Description);

            if (m_Commands.size() > 0)
            {
                System.out.println("sub commands: ");

                m_Commands.forEach((k, v) ->
                {
                    System.out.println(k);
                });
            }
            else
                System.out.println(m_Name + " has no sub commands");
        }

        usermain(aParameters);
    }

    /**
     * Given a set of parameters, run the user-defined behaviour of the program
     * (plus some boilerplate for handling commands)
     */
    public void run(Parameter.List aParameters)
    {
        for (int i = 0, s = aParameters.size(); i < s; ++i)
            if (aParameters.get(i) instanceof Parameter.Positional)
                if (m_Commands.containsKey(((Parameter.Positional) aParameters.get(i)).getValue()))
                {
                    final String commandName = ((Parameter.Positional) aParameters.get(i)).getValue();

                    Parameter.List subcommandParams = new Parameter.List(aParameters).subList(i, aParameters.size());
                    subcommandParams.remove(0);

                    aParameters.subList(i, aParameters.size()).clear();

                    main(aParameters);
                    m_Commands.get(commandName).run(subcommandParams);

                    return;
                }

        main(aParameters);
    }

    /**
     * this overload of run takes standard java program args, converting them to
     * the arg types defined in this library
     */
    public void run(String[] aRawArgs)
    {
        //final List<Parameter> parameters = new ArrayList<>();
        final Parameter.List parameters = new Parameter.List();

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

    public Program(final String aName, final String aDescription, List<Program> aCommands)
    {
        m_Name = aName;
        m_Description = aDescription;

        if (null != aCommands)
            aCommands.forEach((program) ->
            {
                m_Commands.put(program.m_Name, program);
            });
    }

    public Program(final String aName, final String aDescription)
    {
        this(aName, aDescription, null);
    }
}
