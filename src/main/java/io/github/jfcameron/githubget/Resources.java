package io.github.jfcameron.githubget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Resources
{
    private Resources()
    {
    }

    private static File createOrRetrieve(final String target) throws IOException
    {
        final Path path = Paths.get(target);

        if (Files.notExists(path))
            return Files.createFile(Files.createDirectories(path)).toFile();

        return path.toFile();
    }

    private static File getAppDataDirectory() throws IOException //required? probably not
    {
        return createOrRetrieve(System.getProperty("user.home") + "/.config/jfc/" + BuildInfo.NAME);
    }
    
    public static Properties loadConfigFile()
    {
        Properties config = null;

        try
        {
            File path = getAppDataDirectory();

            File f = new File(path.getPath() + "/config.cfg");
            f.createNewFile();

            config = new Properties();

            config.load(new FileInputStream(f));
        }
        catch (IOException ex)
        {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        }

        return config;
    }

    public static String PromptForPassword()
    {
        return System.console() != null
                ? Arrays.toString(System.console().readPassword())
                : null;
    }
}
