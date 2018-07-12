package io.github.jfcameron.githubget;

import com.google.common.collect.ImmutableMap;
import io.github.jfcameron.githubget.taf.Parameter;
import io.github.jfcameron.githubget.taf.Program;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * entry for this project.
 */
public class Application
{
    public static void main(String[] args) throws Exception
    {
        new Program(ImmutableMap.of(
                "Blar", new Program()
        {
            @Override
            protected void usermain(List<Parameter> aParameters)
            {
                System.out.println("Greetings from blar!");
            }
        }))
        {
            private void printGHData(final APIToken credentials) throws Exception
            {
                Account githubdata = new Account(
                        "jfcameron", //Account name
                        true, //ignore forks
                        Arrays.asList( //ignore repos by name
                                "bash-settings",
                                "TF2-Bindings"),
                        credentials);

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
            protected void usermain(List<Parameter> aParameters)
            {
                aParameters.forEach((param) -> 
                {
                    if (param instanceof Parameter.OptionList)
                    {
                        if (((Parameter.OptionList)param).contains('h'))
                        {
                            BuildInfo.prettyPrint();
                        }
                    }
                    else if (param instanceof Parameter.LongOption)
                    {
                        if ("help".equals(((Parameter.LongOption)param).getName().toLowerCase()))
                        {
                            BuildInfo.prettyPrint();
                        }
                    }
                });
                
                /*if (aParameters.isEmpty())
                    throw new RuntimeException(BuildInfo.NAME + " requires arguments!");

                if (aParameters.get(0) instanceof Parameter.OptionList)
                {
                    if (((Parameter.OptionList) aParameters.get(0)).contains('h'))
                        io.github.jfcameron.githubget.BuildInfo.prettyPrint();
                }
                else
                    if ((aParameters.get(0) instanceof Parameter.Positional))
                    {
                        if (((Parameter.Positional) aParameters.get(0)).getValue().length() != 40)
                            throw new RuntimeException(BuildInfo.NAME + " first argument must be an OAuth token!");

                        try
                        {
                            printGHData(new APIToken(((Parameter.Positional) aParameters.get(0)).getValue()));
                        }
                        catch (Exception ex)
                        {
                            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }*/
            }
        }.run(args);
    }
}
