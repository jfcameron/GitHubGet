package io.github.jfcameron.githubget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Config
{
    private Properties config = null;
    private File f;

    public Config()
    {
        try
        {
            File path = getAppDataDirectory();

            f = new File(path.getPath() + "/config.cfg");
            f.createNewFile();

            config = new Properties();

            config.load(new FileInputStream(f));
        }
        catch (IOException ex)
        {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String get(final String aPropertyName)
    {
        return config.getProperty(aPropertyName);
    }
    
    public void setProperty(final String aPropertyName, final String aPropertyValue)
    {
        config.setProperty(aPropertyName, aPropertyValue);
        
        saveConfigFile();
    }
    
    public boolean containsKey(final String aKeyName)
    {
        return config.containsKey(aKeyName);
    }
    
    public String getProperty(final String aPropertyName)
    {
        return config.getProperty(aPropertyName);
    }

    private File createOrRetrieve(final String target) throws IOException
    {
        final Path path = Paths.get(target);

        if (Files.notExists(path))
            return Files.createFile(Files.createDirectories(path)).toFile();

        return path.toFile();
    }

    private File getAppDataDirectory() throws IOException //required? probably not
    {
        return createOrRetrieve(System.getProperty("user.home") + "/.config/jfc/" + BuildInfo.NAME);
    }

    private void saveConfigFile()
    {
        try
        {
            config.store(new FileOutputStream(f), null);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*/////blar
    public static String PromptForPassword()
    {
        return System.console() != null
                ? Arrays.toString(System.console().readPassword())
                : null;
    }*/
}
