/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Cached display html content
 *  @author Kay Kasemir
 */
public class CachedDisplay
{
	private final DisplayKey key;
	private final String html;
	private final AtomicInteger calls = new AtomicInteger();
	private final long ms;

	CachedDisplay(final DisplayKey key, final String html, final long ms)
	{
        this.key = key;
        this.html = html;
        this.ms = ms;
    }

	void registerCall()
	{
	    calls.incrementAndGet();
	}

    public String getDisplay()
    {
        return key.getDisplay();
    }

    public Map<String, String> getMacros()
    {
        return key.getMacros();
    }

    public int getCalls()
	{
	    return calls.get();
	}

	public long getMillisec()
	{
	    return ms;
    }

    public String getHTML()
    {
        return html;
    }

    @Override
    public String toString()
    {
        return key + ": Load time " + ms + "ms";
    }
}
