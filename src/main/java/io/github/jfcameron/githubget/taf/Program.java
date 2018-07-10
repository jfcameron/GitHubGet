package io.github.jfcameron.githubget.taf;

import java.util.List;

/**
 * An embedded program
 */
public abstract class Program
{
    public abstract void run(List<Flag> aFlags, List<PositionalArgument> aPositionalArguments, List<NamedArgument> aNamedArguments);
}
