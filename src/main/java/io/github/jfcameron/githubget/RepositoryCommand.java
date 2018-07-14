package io.github.jfcameron.githubget;

import static io.github.jfcameron.githubget.Application.token;
import io.github.jfcameron.githubget.taf.Command;
import io.github.jfcameron.githubget.taf.Parameter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josephcameron
 */
public class RepositoryCommand extends Command
{
    public RepositoryCommand()
    {
        super("repo", ""
                + "displays information about a git repo hosted on github.\n"
                + "repo [author] [repo]\n"
                + "NO OPTION: print the title and descrption\n"
                + "--issues: print title and body of all open issues\n"
                + "--topics: print all repo topics\n"
                + "--languages: print language and byte count within repo\n"
                + "--readme: print contents of the repo's readme");
    }

    private enum Mode
    {
        BasicInfo,
        Topics,
        Issues,
        Languages,
        Readme
    }

    @Override
    protected void usermain(Parameter.List aParameters)
    {
        Mode mode = Mode.BasicInfo;

        String accountName = aParameters.getPositional(0);

        String repoName = aParameters.getPositional(1);

        if (aParameters.containsLongOption("issues"))
            mode = Mode.Issues;

        if (aParameters.containsLongOption("topics"))
            mode = Mode.Topics;

        if (aParameters.containsLongOption("languages"))
            mode = Mode.Languages;

        if (aParameters.containsLongOption("readme"))
            mode = Mode.Readme;

        if (accountName != null && repoName != null)
            try
            {
                final Repository repository = new Repository(
                        accountName,
                        repoName,
                        token);

                switch (mode)
                {
                    case Topics:
                    {
                        repository.getTopics().forEach((topic) ->
                        {
                            System.out.println(topic);
                        });
                    }
                    break;

                    case Issues:
                    {
                        repository.getIssues().forEach((issue) ->
                        {
                            System.out.println(""
                                    + "Title: " + issue.getTitle() + "\n"
                                    + issue.getBody()
                                    + "\n======================");
                        });
                    }
                    break;

                    case Languages:
                    {
                        repository.getLanguageBytes().forEach((key, value) ->
                        {
                            System.out.println(key + ": " + value + " bytes");
                        });
                    }
                    break;

                    case Readme:
                    {
                        System.out.println(repository.getReadme());
                    }
                    break;

                    default:
                    case BasicInfo:
                    {
                        System.out.println(repository.getName());
                        System.out.println(repository.getDescription());
                        System.out.println(repository.getCloneURL());
                    }
                    break;
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(RepositoryCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}
