package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.taf.Command;
import io.github.jfcameron.githubget.taf.Parameter;

/**
 *
 * @author josephcameron
 */
public class SettingsCommand extends Command
{
    public SettingsCommand()
    {
        super("settings", "interact with config on disk.\n"
                + "--check prints whether or not a token is present\n"
                + "--token=[VALUE] sets token to value");
    }

    @Override
    protected void usermain(Parameter.List aParameters)
    {
        if (aParameters.containsLongOption("check"))
            if (Application.config.containsKey("GithubToken"))
                System.out.println(Application.config.get("GithubToken"));
            else
                System.out.println("No token in config");
        else
        {
            String token = aParameters.getLongOptionWithValue("token");

            if (token != null)
                Application.config.setProperty("GithubToken", token);
        }
    }
}
