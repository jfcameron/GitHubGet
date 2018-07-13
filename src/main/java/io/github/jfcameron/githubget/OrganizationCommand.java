package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.taf.Command;
import io.github.jfcameron.githubget.taf.Parameter;

/**
 *
 * @author josephcameron
 */
public class OrganizationCommand extends Command
{
    public OrganizationCommand()
    {
        super("org", ""
                + "display info about a github organization.");
    }

    @Override
    protected void usermain(Parameter.List aParameters)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
