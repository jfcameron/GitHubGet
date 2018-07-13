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
import io.github.jfcameron.githubget.util.SimpleJSONUtil;

/**
 * Models a github account
 */
public final class Account
{
    private final String m_Login;

    public String getLogin()
    {
        return m_Login;
    }

    /*
    //private final int m_ID;
    
    //private final int m_NodeID;*/
    private final String m_AvatarURL;

    public String getAvatarURL()
    {
        return m_AvatarURL;
    }

    /*private final String m_GravatarURL;
    
    private final String m_URL;
    
    private final String m_HTMLURL;*/
    private final List<Account> m_Followers = new ArrayList<>();

    private final Once m_FetchFollowers;

    public List<Account> getFollowers()
    {
        m_FetchFollowers.run();

        return m_Followers;
    }
    
    private final List<Account> m_Following = new ArrayList<>();
    
    private final Once m_FetchFollowing;
    
    public List<Account> getFollowing()
    {
        m_FetchFollowing.run();
        
        return m_Following;
    }

    /*
    
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

    private final String m_Name;

    public String getName()
    {
        return m_Name;
    }

    private final String m_Company;

    public String getCompany()
    {
        return m_Company;
    }

    private final String m_Blog;

    public String getBlog()
    {
        return m_Blog;
    }

    private final String m_Location;

    public String getLocation()
    {
        return m_Location;
    }

    private final String m_Email;

    public String getEmail()
    {
        return m_Email;
    }

    //private final String m_Hireable;
    //getHireable
    private final String m_Biography;

    public String getBiography()
    {
        return m_Biography;
    }

    private final Integer m_PublicRepos;

    public Integer getPublicRepoCount()
    {
        return m_PublicRepos;
    }

    private final Integer m_PublicGists;

    public Integer getPublicGistCount()
    {
        return m_PublicGists;
    }

    private final Integer m_FollowerCount;

    public Integer getFollowerCount()
    {
        return m_FollowerCount;
    }

    private final Integer m_FollowingCount;

    public Integer getFollowingCount()
    {
        return m_FollowingCount;
    }

    //private final String m_CreatedAt;
    //private final STring m_UpdatedAt;


    public Account(
            final JSONObject account,
            final APIToken aAPIToken,
            final String aGithubAccountName,
            final boolean aIgnoreForks,
            final List<String> aRepoIgnoreList)
    {
        m_Login = SimpleJSONUtil.getStringFromJSONObject(account, "login");//account.get("login").toString();
        m_AvatarURL = SimpleJSONUtil.getStringFromJSONObject(account, "avatar_url");//account.get("avatar_url").toString();

        m_Name = SimpleJSONUtil.getStringFromJSONObject(account, "name");//account.get("name").toString();
        m_Company = SimpleJSONUtil.getStringFromJSONObject(account, "company");
        m_Blog = SimpleJSONUtil.getStringFromJSONObject(account, "blog");
        m_Location = SimpleJSONUtil.getStringFromJSONObject(account, "location");
        m_Email = SimpleJSONUtil.getStringFromJSONObject(account, "email");
        m_Biography = SimpleJSONUtil.getStringFromJSONObject(account, "bio");

        m_PublicRepos = SimpleJSONUtil.getIntegerFromJSONObject(account, "public_repos");
        m_PublicGists = SimpleJSONUtil.getIntegerFromJSONObject(account, "public_gists");
        m_FollowerCount = SimpleJSONUtil.getIntegerFromJSONObject(account, "followers");
        m_FollowingCount = SimpleJSONUtil.getIntegerFromJSONObject(account, "following");

        final JSONParser parser = new JSONParser();

        m_FetchRepos = new Once(() ->
        {
            try
            {
                List<String> ignoreList = aRepoIgnoreList == null ? new ArrayList<>() : aRepoIgnoreList;

                final JSONArray repos = (JSONArray) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                        "https://api.github.com/users/"
                        + aGithubAccountName + "/repos",
                        aAPIToken));

                for (final Object repo : repos)
                {
                    if (aIgnoreForks == true && ((Boolean) ((JSONObject) repo).get("fork")) == true)
                        continue;

                    if (!ignoreList.contains(((JSONObject) repo).get("name")))
                        m_Repositories.add(new Repository((JSONObject) repo, aAPIToken));
                }
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

        m_FetchFollowers = new Once(() ->
        {
            try
            {
                List<String> ignoreList = aRepoIgnoreList == null ? new ArrayList<>() : aRepoIgnoreList;

                final JSONArray followers = (JSONArray) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                        "https://api.github.com/users/"
                        + aGithubAccountName + "/followers",
                        aAPIToken));
                
                //followers.forEach((final Object followerJSON) ->
                for (final Object followerJSON : followers)
                {
                    //If i want more info, I need to use a different CTOR here. it costs more fetches though
                    m_Followers.add(new Account(
                            (JSONObject) followerJSON,
                            aAPIToken,
                            ((JSONObject) followerJSON).get("login").toString(),
                            aIgnoreForks,
                            aRepoIgnoreList));
                };
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
        
        m_FetchFollowing = new Once(() ->
        {
            try
            {
                List<String> ignoreList = aRepoIgnoreList == null ? new ArrayList<>() : aRepoIgnoreList;

                final JSONArray following = (JSONArray) parser.parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                        "https://api.github.com/users/"
                        + aGithubAccountName + "/following",
                        aAPIToken));
                
                //followers.forEach((final Object followerJSON) ->
                for (final Object followingJSON : following)
                {
                    //If i want more info, I need to use a different CTOR here. it costs more fetches though
                    m_Following.add(new Account(
                            (JSONObject) followingJSON,
                            aAPIToken,
                            ((JSONObject) followingJSON).get("login").toString(),
                            aIgnoreForks,
                            aRepoIgnoreList));
                };
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
            final APIToken aAPIToken,
            final String aGithubAccountName,
            final boolean aIgnoreForks,
            final List<String> aRepoIgnoreList
    ) throws Exception
    {
        this((JSONObject) ((new JSONParser()).parse(io.github.jfcameron.githubget.util.API.authenticatedFetch(
                "https://api.github.com/users/"
                + aGithubAccountName,
                aAPIToken))),
                aAPIToken,
                aGithubAccountName,
                aIgnoreForks,
                aRepoIgnoreList);
    }

    public Account(
            final APIToken aAPIToken,
            final String aGithubAccountName,
            final boolean aIgnoreForks) throws Exception
    {
        this(aAPIToken, aGithubAccountName, aIgnoreForks, null);
    }
}
