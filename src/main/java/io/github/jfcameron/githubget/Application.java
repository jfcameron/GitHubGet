package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.taf.Parameter;
import io.github.jfcameron.githubget.taf.Command;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            private void printGHData(final APIToken credentials) throws Exception
            {
                Account githubdata = new Account(
                        credentials,
                        "jfcameron", //Account name
                        true, //ignore forks
                        Arrays.asList( //ignore repos by name
                                "bash-settings",
                                "TF2-Bindings"));

                // AggregateLanguageScores
                final Map<String, Long> totalLanguageScores = new HashMap<>();
                {
                    githubdata.getRepositories().stream().map((repo) -> repo.getLanguageBytes()).forEachOrdered((repoLanguageScores)
                            -> repoLanguageScores.keySet().forEach((key)
                                    -> totalLanguageScores.put(key, totalLanguageScores.containsKey(key)
                                    ? (long) totalLanguageScores.get(key) + (long) repoLanguageScores.get(key)
                                    : (long) repoLanguageScores.get(key))));
                }

                // AggregateTopics
                final Map<String, Long> totalTopics = new HashMap<>();
                {
                    githubdata.getRepositories().forEach((repo) -> repo.getTopics().forEach((topic)
                            -> totalTopics.put(topic, totalTopics.containsKey(topic)
                                    ? totalTopics.get(topic) + 1
                                    : 1)));
                }

                // ********************************************************************
                // PRINT
                //
                System.out.println("**DATA**\n\n");
                githubdata.getRepositories().stream().map((repo) ->
                {
                    System.out.println("Repository: " + repo.getName());

                    System.out.println("Description: " + repo.getDescription());

                    return repo;
                }).map((repo) ->
                {
                    System.out.println("Languages:");
                    if (repo.getLanguageBytes().size() > 0)
                        repo.getLanguageBytes().keySet().forEach((key)
                                -> System.out.println(key + ": " + repo.getLanguageBytes().get(key)));
                    else
                        System.out.println("NONE");

                    System.out.println("Topics:");
                    if (repo.getTopics().size() > 0)
                        repo.getTopics().forEach((topic) -> System.out.println(topic));
                    else
                        System.out.println("NONE");

                    System.out.println("Issues:");
                    if (repo.getIssues().size() > 0)
                        repo.getIssues().forEach((issue) ->
                        {
                            System.out.println("Title: " + ((Issue) issue).getTitle());
                            System.out.println("Body: " + ((Issue) issue).getBody());
                        });
                    else
                        System.out.println("NONE");

                    return repo;
                }).forEachOrdered((_item) ->
                {
                    System.out.println();
                });

                System.out.println("Account-wide language usage:");
                final List<Map.Entry<String, Long>> sortedEntires = new ArrayList<>(totalLanguageScores.entrySet());
                sortedEntires.sort((Map.Entry<String, Long> l, Map.Entry<String, Long> r) ->
                {
                    return l.getValue() >= r.getValue() ? -1 : +1;
                });
                sortedEntires.forEach((entry) ->
                {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                });
                System.out.println();

                System.out.println("Account-wide topics");
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

                System.out.println("Request count: " + credentials.getRequestCout());
            }

            @Override
            protected void usermain(Parameter.List aParameters)
            {
                String oauthToken = aParameters.getPositional(0);

                if (null != oauthToken && oauthToken.length() == 40)
                    token = new APIToken(oauthToken);
            }
        }.run(args);
    }
}
