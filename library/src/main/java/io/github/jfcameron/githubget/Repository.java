package io.github.jfcameron.githubget;

import io.github.jfcameron.githubget.util.Once;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Models a github repository
 *
 * @version https://developer.github.com/v3/repos/
 */
public final class Repository
{
    /**
     * Name of the repository
     */
    private final String m_Name;

    /**
     * repo description
     */
    private final String m_Description;

    /**
     * Collection of the languages used in the project and the size of their
     * source code in bytes
     */
    private final Map<String, Long> m_LanguageBytes = new HashMap<>();

    /**
     * list of the topics covered by the project. Keep in mind these are
     * optional and defined by the repository owner
     */
    private final List<String> m_Topics = new ArrayList<>();

    /**
     * List of issues (kanban tickets) associate with the project
     */
    private final List<Issue> m_Issues = new ArrayList<>();

    private final Once m_FetchLanguageBytes;
    private final Once m_RetrieveTopics;
    private final Once m_RetrieveIssues;

    public String getDescription()
    {
        return m_Description;
    }

    public List<Issue> getIssues()
    {
        m_RetrieveIssues.run();

        return m_Issues;
    }

    public List<String> getTopics()
    {
        m_RetrieveTopics.run();

        return m_Topics;
    }

    public String getName()
    {
        return m_Name;
    }

    public Map<String, Long> getLanguageBytes()
    {
        m_FetchLanguageBytes.run();

        return m_LanguageBytes;
    }

    // https://api.github.com/repos/jfcameron/blapblop
    // public Repository(final String aAccountName, final String aRepoName, final String aAPIToken)
    public Repository(final String aOwnerName, final String aRepoName, final APIToken aAPIToken) throws ParseException, Exception
    {
        this((JSONObject) (new JSONParser()).parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                "https://api.github.com/repos/" + aOwnerName + "/" + aRepoName, aAPIToken)),
                aAPIToken
        );
    }

    /**
     * constructs the repo model from repo JSON data received from GHAPI
     *
     * @param aRepositoryJSON
     * @throws Exception
     */
    protected Repository(final JSONObject aRepositoryJSON, final APIToken aAPIToken) throws Exception
    {
        m_Name = aRepositoryJSON.get("name").toString();

        m_Description = aRepositoryJSON.get("description").toString();

        JSONParser parser = new JSONParser();

        m_FetchLanguageBytes = new Once(() ->
        {
            try
            {
                JSONObject languages = (JSONObject) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                                ((JSONObject) aRepositoryJSON).get("languages_url").toString(), aAPIToken));

                for (Iterator iterator = languages.keySet().iterator(); iterator.hasNext();)
                {
                    String key = (String) iterator.next();

                    m_LanguageBytes.put(key, m_LanguageBytes.containsKey(key)
                            ? (long) m_LanguageBytes.get(key) + (long) languages.get(key)
                            : (long) languages.get(key));
                }
            }
            catch (ParseException ex)
            {
                Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex)
            {
                Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        m_RetrieveTopics = new Once(() ->
        {
            ((JSONArray) aRepositoryJSON.get("topics")).forEach((topic) ->
            {
                m_Topics.add((String) topic);
            });
        });

        m_RetrieveIssues = new Once(() ->
        {
            try
            {
                JSONArray issues;

                issues = (JSONArray) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                                ((JSONObject) aRepositoryJSON).get("issues_url").toString(), aAPIToken));

                issues.forEach((issue) -> m_Issues.add(new Issue(((JSONObject) issue))));
            }
            catch (ParseException ex)
            {
                Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex)
            {
                Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
