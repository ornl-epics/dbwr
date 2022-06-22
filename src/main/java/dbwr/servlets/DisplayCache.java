/*******************************************************************************
 * Copyright (c) 2019-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;


import static dbwr.WebDisplayRepresentation.logger;

import java.lang.ref.SoftReference;
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
        logger.log(Level.FINE, "Deleting " + key);
        cache.remove(key);
    }

    /** @param key Entry to either fetch or create if not found
     *  @param creator Function used to create missing entries
     *  @return Display
     */
    public static CachedDisplay getOrCreate(final DisplayKey key, final DisplayCreator creator)
    {
        final SoftReference<CachedDisplay> ref = cache.computeIfAbsent(key, k -> new SoftReference<>(creator.create(k)));
        CachedDisplay cached = ref.get();
        if (cached == null)
        {   // Expired
            logger.log(Level.FINE, "Cache expired for " + key);
            cached = creator.create(key);
            cache.put(key, new SoftReference<>(cached));
        }
        else
        {
            logger.log(Level.FINE, "Cached " + cached);
            cached.registerAccess();
        }
        return cached;
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
