/*******************************************************************************
 * Copyright (c) 2019-2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;

import java.time.Instant;
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
    private final Instant created;
    private volatile Instant stamp;
    private final long ms;

    /** @param key Display and macros (but only display is used in most cases)
     *  @param html HTML of the display
     *  @param ms Time it took to create the HTML
     */
    CachedDisplay(final DisplayKey key, final String html, final long ms)
    {
        this.key = key;
        this.html = html;
        this.created = stamp = Instant.now();
        this.ms = ms;
    }

    /** Notify cache that entry has been re-used */
    void registerAccess()
    {
        stamp = Instant.now();
        calls.incrementAndGet();
    }

    /** @return Display path */
    public String getDisplay()
    {
        return key.getDisplay();
    }

    /** @return Display macros */
    public Map<String, String> getMacros()
    {
        return key.getMacros();
    }

    /** @return Time when cache entry was created */
    public Instant getCreated()
    {
        return created;
    }

    /** @return Time when cache entry was last re-used */
    public Instant getTimestamp()
    {
        return stamp;
    }

    /** @return Number of cache entry re-use */
    public int getCalls()
    {
        return calls.get();
    }

    /** @return Time it took to create the display */
    public long getMillisec()
    {
        return ms;
    }

    /** @return HTML representation of display */
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
