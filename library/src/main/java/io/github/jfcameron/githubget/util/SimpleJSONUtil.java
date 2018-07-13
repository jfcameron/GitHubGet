package io.github.jfcameron.githubget.util;

import org.json.simple.JSONObject;

/**
 *
 * @author josephcameron
 */
public class SimpleJSONUtil
{
    private SimpleJSONUtil(){}
    
    public static String getStringFromJSONObject(final JSONObject aJSONObject, final String aFieldName)
    {
        final Object output = aJSONObject.get(aFieldName);

        return output == null ? null : output.toString();
    }

    public static Integer getIntegerFromJSONObject(final JSONObject aJSONObject, final String aFieldName)
    {
        final String output = getStringFromJSONObject(aJSONObject, aFieldName);

        return output == null ? null : Integer.parseInt(output);
    }
}
