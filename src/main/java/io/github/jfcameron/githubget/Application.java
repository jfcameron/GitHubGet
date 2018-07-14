package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.taf.Parameter;
import io.github.jfcameron.githubget.taf.Command;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
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
                try
                {
                    String pathToExec = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
                    
                    pathToExec = pathToExec.split("/*.jar", 2)[0];
                    
                    System.out.println(pathToExec);
                    
                    File f = new File(pathToExec + "config.cfg");
                    
                    if (!f.isFile())
                        f.createNewFile();

                    Properties mySettings = new Properties();
                    
                    mySettings.load(new FileInputStream(pathToExec + "config.cfg"));
                }
                catch (FileNotFoundException ex)
                {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (URISyntaxException ex)
                {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }

                String oauthToken = aParameters.getPositional(0);

                if (null != oauthToken && oauthToken.length() == 40)
                    token = new APIToken(oauthToken);
            }
        }.run(args);
    }
}
