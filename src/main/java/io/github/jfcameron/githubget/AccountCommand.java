package io.github.jfcameron.githubget;

import static io.github.jfcameron.githubget.Application.token;
import io.github.jfcameron.githubget.taf.Command;
import io.github.jfcameron.githubget.taf.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josephcameron
 */
public class AccountCommand extends Command
{
    public AccountCommand()
    {
        super("account", ""
                + "get info about a github account specified by the first positional\n"
                + "account [account]\n"
                + "No args: print basic account info\n"
                + "--repos: no basic info, list account repos instead\n"
                + " -f: ignore forks in repo list\n"
                + " -l: list languages with repos\n"
                + " -t: list topics with repos\n"
                + "--followers: list this user's followers\n"
                + "--following: list users this user is following\n"
                + "--languages: aggregation of programming languages used in all repos owned by this account\n"
                + "--topics: aggregation of topics from all repos owned by this account\n");
    }

    private enum Mode
    {
        BasicInfo,
        Repositories,
        Followers,
        Following,
        Languages,
        Topics,
        Issues
    }

    @Override
    protected void usermain(Parameter.List aParameters)
    {
        Mode mode = Mode.BasicInfo;

        if (aParameters.containsLongOption("repos"))
            mode = Mode.Repositories;
        else
            if (aParameters.containsLongOption("followers"))
                mode = Mode.Followers;
            else
                if (aParameters.containsLongOption("following"))
                    mode = Mode.Following;
                else
                    if (aParameters.containsLongOption("languages"))
                        mode = Mode.Languages;
                    else
                        if (aParameters.containsLongOption("topics"))
                            mode = Mode.Topics;
                        else
                            if (aParameters.containsLongOption("issues"))
                                mode = Mode.Issues;

        String accountName = aParameters.getPositional(0);

        if (accountName != null)
            try
            {
                final Account account = new Account(token, accountName, aParameters.containsOption('f'));

                switch (mode)
                {
                    case Repositories:
                    {
                        account.getRepositories().forEach((repo) ->
                        {
                            System.out.println(repo.getName());

                            if (aParameters.containsOption('l'))
                            {
                                System.out.println("Languages:");

                                if (repo.getLanguageBytes().size() > 0)
                                    repo.getLanguageBytes().keySet().forEach((key)
                                            -> System.out.println(key + ": " + repo.getLanguageBytes().get(key)));
                                else
                                    System.out.println("NONE");
                            }

                            if (aParameters.containsOption('t'))
                            {
                                System.out.println("Topics:");

                                if (repo.getTopics().size() > 0)
                                    repo.getTopics().forEach((topic) -> System.out.println(topic));
                                else
                                    System.out.println("NONE");
                            }
                        });
                    }
                    break;

                    case Followers:
                    {
                        account.getFollowers().forEach((follower) ->
                        {
                            System.out.println(follower.getLogin());
                        });
                    }
                    break;

                    case Following:
                    {
                        account.getFollowing().forEach((following) ->
                        {
                            System.out.println(following.getLogin());
                        });
                    }
                    break;

                    case Languages:
                    {
                        System.out.println("Account-wide language usage:");
                        
                        final Map<String, Long> languageBytes = new HashMap<>();
                        {

                            account.getRepositories().stream().map((repo) -> repo.getLanguageBytes()).forEachOrdered((repoLanguageScores)
                                    -> repoLanguageScores.keySet().forEach((key)
                                            -> languageBytes.put(key, languageBytes.containsKey(key)
                                            ? (long) languageBytes.get(key) + (long) repoLanguageScores.get(key)
                                            : (long) repoLanguageScores.get(key))));
                        }

                        final List<Map.Entry<String, Long>> sortedEntires = new ArrayList<>(languageBytes.entrySet());
                        sortedEntires.sort((Map.Entry<String, Long> l, Map.Entry<String, Long> r) ->
                        {
                            return l.getValue() >= r.getValue() ? -1 : +1;
                        });
                        sortedEntires.forEach((entry) ->
                        {
                            System.out.println(entry.getKey() + ": " + entry.getValue());
                        });
                        System.out.println();
                    }
                    break;

                    case Topics:
                    {
                        System.out.println("Account-wide topics");
                        
                        final Map<String, Long> totalTopics = new HashMap<>();
                        {
                            account.getRepositories().forEach((repo) -> repo.getTopics().forEach((topic)
                                    -> totalTopics.put(topic, totalTopics.containsKey(topic)
                                            ? totalTopics.get(topic) + 1
                                            : 1)));
                        }

                        final List<Map.Entry<String, Long>> sortedTopics = new ArrayList<>(totalTopics.entrySet());
                        sortedTopics.sort((Map.Entry<String, Long> l, Map.Entry<String, Long> r) ->
                        {
                            return l.getValue() >= r.getValue() ? -1 : +1;
                        });
                        sortedTopics.forEach((topic) ->
                        {
                            System.out.println(topic.getKey() + ": " + topic.getValue());
                        });
                        System.out.println();
                    }
                    break;

                    case Issues:
                    {
                        account.getRepositories().forEach((repo) ->
                        {
                            System.out.println(repo.getName());

                            if (repo.getIssues().size() > 0)
                                repo.getIssues().forEach((issue) ->
                                {
                                    System.out.println("Title: " + ((Issue) issue).getTitle());
                                    System.out.println("Body: " + ((Issue) issue).getBody());
                                });
                            else
                                System.out.println("No issues");

                            System.out.println();
                        });
                    }
                    break;

                    default:
                    case BasicInfo:
                    {
                        System.out.println(account.getName());
                        System.out.println(account.getCompany());
                        System.out.println(account.getBlog());
                        System.out.println(account.getLocation());
                        System.out.println(account.getEmail());
                        System.out.println(account.getBiography());
                        System.out.println(account.getAvatarURL());
                        System.out.println("Public repos #: " + account.getPublicRepoCount());
                        System.out.println("Public gists #: " + account.getPublicGistCount());
                        System.out.println("Followers #: " + account.getFollowerCount());
                        System.out.println("Following #: " + account.getFollowingCount());
                    }
                    break;
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
