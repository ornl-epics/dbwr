/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import dbwr.widgets.ParentWidget;

/** Resolve a display, potentially updating *.opi into *.bob
 *  @author Kay Kasemir
 */
public class Resolver
{
    private URL url;
    private InputStream stream;

    static
    {
        try
        {
            CertificateHandler.trustAnybody();
        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Cannot install certificate handler", ex);
        }
    }

    /** @param display_name Full path to display, file: or http:, *.opi or *.bob
     *  @throws Exception on error
     */
    public Resolver(final String display_name) throws Exception
    {
        if (display_name.isEmpty())
        {
            url = getClass().getResource("/no_name.bob");
            stream = url.openStream();
            return;
        }

        try
        {
            // Try to 'upgrade' to *.bob file
            if (display_name.contains(".opi"))
            {
                url = new URL(display_name.replace(".opi", ".bob"));
                stream = url.openStream();
                logger.log(Level.INFO, "Opening *.bob instead of " + display_name);
                return;
            }
        }
        catch (final Exception ex)
        {
            // Ignore error from *.bob attempts
        }

        url = new URL(display_name);
        stream = url.openStream();
    }

    /** @return URL that was used to open the display */
    public URL getUrl()
    {
        return url;
    }

    /**  @return Stream for the *.bob in case it exists, otherwise using the *.opi */
    public InputStream getStream()
    {
        return stream;
    }

    /** Resolve a file name relative to a parent widget
     *
     *  @param parent Parent widget
     *  @param file File to resolve
     *  @return Resolved file
     *  @throws Exception on error
     */
    public static String resolve(final ParentWidget parent, final String file) throws Exception
    {
        if (file.startsWith("http"))
            return file;

        final URL display = parent.getDisplay();
        String path = display.getPath();
        final int end = path.lastIndexOf('/');
        path = path.substring(0, end+1) + file;

        final URL resolved = new URL(display.getProtocol(), display.getHost(), display.getPort(), path);
        logger.log(Level.FINE, () -> "Resolving " + display + ": " + file + " to " + resolved);

        return resolved.toExternalForm();
    }
}

