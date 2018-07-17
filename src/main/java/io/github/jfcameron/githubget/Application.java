package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.taf.Parameter;
import io.github.jfcameron.githubget.taf.Command;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * entry for this project.
 */
public class Application
{
    static String description = "";

    static APIToken token = null;

    static Properties config = Resources.loadConfigFile();

    public static void main(String[] args) throws Exception
    {
        BuildInfo.prettyPrint((Field[] fields) ->
        {
            for (final Field field : fields)
                try
                {
                    description += field.getName() + ": " + field.get(null) + "\n";
                }
                catch (IllegalArgumentException | IllegalAccessException ex)
                {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
        });

        new Command("GitHubGet (Root)", "utility for viewing github data.\n" + description,
                Arrays.asList(
                        new AccountCommand(),
                        new RepositoryCommand(),
                        new OrganizationCommand()))
        {
            @Override
            protected void usermain(Parameter.List aParameters)
            {
                //String oauthToken = aParameters.getPositional(0);

                String oauthToken = config.getProperty("GithubToken");

                if (oauthToken == null)
                    System.out.println("" 
                            + "Config does not contain githubtoken. "
                            + "This is not STRICTLY required to Github will subject you to a api call usage limit of 60/hr enforced on the basis of your external IP");

                if (null != oauthToken && oauthToken.length() == 40)
                    token = new APIToken(oauthToken);
            }
        }.run(args);
    }
}
