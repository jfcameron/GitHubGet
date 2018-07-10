package io.github.jfcameron.githubget.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Performs behaviour once. Utility class. Possibly break out to common lib See
 * c++ call_once & once_flag
 */
public class Once
{
    private final AtomicBoolean m_Done = new AtomicBoolean();
    
    private final Runnable m_Behaviour;

    public void run()
    {
        if (m_Done.get())
            return;

        if (m_Done.compareAndSet(false, true))
            m_Behaviour.run();
    }

    public Once(Runnable aBehaviour)
    {
        m_Behaviour = aBehaviour;
    }
}
