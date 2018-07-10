package io.github.jfcameron.githubget.util;

import io.github.jfcameron.githubget.APIToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

/**
 * Wrapper for GH API
 * @author josephcameron
 */
public class API
{
    private API()
    {
    }

    /**
     * This was in Resources. Resources is not finalized. Refactor this.
     */
    private static String fetch(String urlToRead, List<Pair<String, String>> aHeaders) throws Exception
    {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) new URL(urlToRead).openConnection();

        if (aHeaders != null)
            aHeaders.forEach((header) ->
            {
                conn.setRequestProperty(header.getKey(), header.getValue());
            });

        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = rd.readLine()) != null)
            result.append(line);

        rd.close();

        return result.toString();
    }

    /**
     * wrapper for fetch, appends header and querystring data common to all
     * Github API GET requests
     *
     * @param aURL
     * @return
     * @throws Exception
     */
    public static String authenticatedFetch(String aURL, final APIToken aAPIToken) throws Exception
    {
        //NOTE: removes all ghapi params. Should replace with a parser and optional args
        aURL = aURL.replaceAll("\\{.*\\}", "");
        //aURL = 

        if (aAPIToken != null)
            aAPIToken.incrementRequestCout();

        return fetch(
                aAPIToken != null
                        ? aURL + "?access_token=" + aAPIToken.getOAuthToken()
                        : aURL,
                Arrays.asList(new Pair<>("Accept", "application/vnd.github.mercy-preview+json")));
    }
}
