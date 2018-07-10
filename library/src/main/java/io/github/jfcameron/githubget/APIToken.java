package io.github.jfcameron.githubget;

/**
 * token used to perform gh api requests. A token is not strictly necessary but
 * unauth requests are limited to 60/hr vs auth 5k/hr. the token object holds
 * the raw token and counts how many times it has been used. warn: strongly
 * recommend that the user limits the generated oauth token's scopes as much as
 * possible. eg: If you dont need to read your private repos, dont add rpivate
 * repo access.
 */
public class APIToken
{
    /**
     * the oauth token used to perform authenticated Github API requests.
     */
    private final String m_OAuthToken;

    public String getOAuthToken()
    {
        return m_OAuthToken;
    }

    /**
     * total number of API calls made. GH limits requests/hr (5k authenticated,
     * 60 unauthenticated). Useful for tracking api usage.
     */
    private int m_RequestCout = 0;

    public int getRequestCout()
    {
        return m_RequestCout;
    }

    public void setRequestCout(int m_RequestCout)
    {
        this.m_RequestCout = m_RequestCout;
    }

    public void incrementRequestCout()
    {
        this.m_RequestCout++;
    }

    /**
     * construct the token object using a raw oauth token string
     *
     * @param aOAuthToken oauth token for authenticating gh api calls
     */
    public APIToken(final String aOAuthToken)
    {
        m_OAuthToken = aOAuthToken;
    }
}
