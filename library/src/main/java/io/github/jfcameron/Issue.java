package io.github.jfcameron;

import org.json.simple.JSONObject;

/**
 * models an issue in a repo's issue tracker and kanban system
 */
public final class Issue
{
    public static enum State
    {
        open,
        closed
    }

    private final String m_Title;

    private final State m_State;

    private final String m_Body;

    public String getBody()
    {
        return m_Body;
    }

    public String getTitle()
    {
        return m_Title;
    }

    public State getState()
    {
        return m_State;
    }

    protected Issue(JSONObject aIssueJSON)
    {
        m_Title = (String) aIssueJSON.get("title");

        m_Body = (String) aIssueJSON.get("body");

        m_State = "open".equals((String) aIssueJSON.get("state"))
                ? State.open
                : State.closed;
    }
}
