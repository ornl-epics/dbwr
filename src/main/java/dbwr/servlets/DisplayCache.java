/*******************************************************************************
 * Copyright (c) 2019-2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;


import static dbwr.WebDisplayRepresentation.logger;

import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/** Cached display html content
 * 
 *  <p>Caches displays using soft references,
 *  basically keeping as much in memory as garbage collection allows.
 *  
 *  @author Kay Kasemir
 */
public class DisplayCache
{
    private static final ConcurrentHashMap<DisplayKey, SoftReference<CachedDisplay>> cache = new ConcurrentHashMap<>();

    /** Function that creates a display */
    @FunctionalInterface
    public interface DisplayCreator
    {
        public CachedDisplay create(final DisplayKey key) throws Error;
    }
    
    /** @param key Entry to remove from cache */
    public static void remove(final DisplayKey key)
    {
        cache.remove(key);
        logger.log(Level.INFO, "Cache deleted " + key);
    }

    /** @param key Entry to either fetch or create if not found
     *  @param creator Function used to create missing entries
     *  @return Display
     */
    public static CachedDisplay getOrCreate(final DisplayKey key, final DisplayCreator creator)
    {
        final Instant start = Instant.now();
        final SoftReference<CachedDisplay> ref = cache.computeIfAbsent(key, k -> new SoftReference<>(creator.create(k)));
        CachedDisplay cached = ref.get();
        if (cached == null)
        {   // Entry was garbage collected
            logger.log(Level.INFO, "Cache re-creates dropped " + key);
            cached = creator.create(key);
            cache.put(key, new SoftReference<>(cached));
        }
        else if (start.compareTo(cached.getCreated()) <= 0)
        {   // Entry is new
            logger.log(Level.INFO, "Cache created " + cached);
        }
        else
        {   // Found a cached entry
            if (isValid(cached))
            {   // .. which looks good
                logger.log(Level.FINE, "Cache re-used " + cached);
                cached.registerAccess();
            }
            else
            {   // .. but file has been modified
                logger.log(Level.INFO, "Cache re-creates outdated " + cached);
                cached = creator.create(key);
                cache.put(key, new SoftReference<>(cached));
            }
        }
        return cached;
    }

    /** @param display Display path for "http:", "https:", "file:"
     *  @return Modification time
     *  @throws Exception
     */
    private static Instant getModificationTime(final String display) throws Exception
    {
        final URLConnection connection = URI.create(display).toURL().openConnection();
        long ms = 0;
        if (connection instanceof HttpURLConnection)
        {
            final HttpURLConnection http = (HttpURLConnection) connection;
            try
            {
                http.setRequestMethod("HEAD");
                http.connect();
                ms = http.getLastModified();
            }
            finally
            {
                http.disconnect();
            }
        }
        else
            ms = connection.getLastModified();
        
        if (ms == 0)
            throw new FileNotFoundException(display);
        return Instant.ofEpochMilli(ms);
    }

    /** @param display Display path for "http:", "https:", "file:", testing ".bob" in case of ".opi"
     *  @return Modification time
     *  @throws Exception
     */
    private static Instant getBobModificationTime(final String display) throws Exception
    {
        if (display.contains(".opi"))
        {   // Try to 'upgrade' to *.bob file
            String updated = display.replace(".opi", ".bob");
            try
            {
                return getModificationTime(updated);
            }
            catch (Exception ex)
            {
                // Ignore error from *.bob attempts
            }
        }

        return getModificationTime(display);
    }

    private static boolean isValid(CachedDisplay cached)
    {
        try
        {
            final Instant modified = getBobModificationTime(cached.getDisplay());
            if (modified.isAfter(cached.getCreated()))
            {
                logger.log(Level.FINE, "Cache entry from " + cached.getCreated() + " is older than last update at " + modified);
                return false;
            }
 
            logger.log(Level.FINE, "Last update at " + modified + " is still covered by cache entry from " + cached.getCreated());
            return true;
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Cache validation error for " + cached, ex);
        }
        return false;
    }

    public static Collection<SoftReference<CachedDisplay>> getEntries()
    {
        return cache.values();
    }

    public static void clear()
    {
        cache.clear();
        logger.log(Level.INFO, "Cache cleared");
    }
}
