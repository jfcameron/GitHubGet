package io.github.jfcameron.githubget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import io.github.jfcameron.githubget.util.Once;

/**
 * Models a github account
 */
public final class Account
{
    /*private final String m_Login;
    
    //private final int m_ID;
    
    //private final int m_NodeID;
    
    private final String m_AvatarURL;
    
    //private final String m_GravatarURL;
    
    private final String m_URL;
    
    private final String m_HTMLURL;
    
    private final List<Account> m_Followers;
    
    private final List<Account> m_Following;
    
    private final List<Gist> m_Gists;
    
    private final List <Repository> m_Starred;
    
    private final List <Repository> m_Subscriptions;
    
    //private final List<Organization> m_Organizations;
    */

    /**
     * list of repositories associated with the account
     */
    private final List<Repository> m_Repositories = new ArrayList<>();
    
    private final Once m_FetchRepos;

    public List<Repository> getRepositories()
    {
        m_FetchRepos.run();
        
        return m_Repositories;
    }

    /**
     * generates data models for a github account
     *
     * @param aGithubAccountName Name of the account to generate models for
     * @param aIgnoreForks do not generate models for downstream forks
     * @param aRepoIgnoreList ignore repos by name (to avoid data collection on
     * e.g: vim preferences)
     * @param aAPIToken an oauth token to perform authenticated api calls with.
     * This token does NOT have to be associated with aGithubAccountName (for
     * public repos) and is entirely optional. It is only needed in case the
     * user's external IP has exceeded the github api usage limit. (e.g. the
     * user is in the office of a software development company)
     * @throws Exception etcetc
     */
    public Account(
            final String aGithubAccountName,
            final boolean aIgnoreForks,
            final List<String> aRepoIgnoreList,
            final APIToken aAPIToken
    ) throws Exception
    {
        m_FetchRepos = new Once(() ->
        {
            JSONParser parser = new JSONParser();

            try
            {
                final JSONArray repos = (JSONArray) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                                "https://api.github.com/users/"
                                + aGithubAccountName + "/repos",
                                aAPIToken));

                for (final Object repo : repos)
                    if (((Boolean) ((JSONObject) repo).get("fork")) != aIgnoreForks
                            && !aRepoIgnoreList.contains(((JSONObject) repo).get("name")))
                        m_Repositories.add(new Repository((JSONObject) repo, aAPIToken));
            }
            catch (ParseException ex)
            {
                Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex)
            {
                Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
