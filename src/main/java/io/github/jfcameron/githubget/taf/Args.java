package io.github.jfcameron.githubget.taf;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all params for a program. Constructed fomr sthings
 */
public class Args
{
    /**
     * Produces args from raw args
     */
    static public List<Args> parseRawArgs(final String[] args)
    {
        return new ArrayList<>();
    }
    
    private List<PositionalArgument> m_PositionalArguments;
    private List<NamedArgument> m_NamedArguments;
    private List<Flag> m_Flags;
    
    protected Args()
    {
        
    }
}
