/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.widgets.Widget;

/** Parses a display file (*.opi, *.bob) into HTML
 *  @author Kay Kasemir
 */
public class DisplayParser implements MacroProvider
{
	final int width, height;
	final Map<String, String> macros;

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

	/** Open a display stream, potentially updating *.opi into *.bob
	 *  @param display_name Full path to display, file: or http:, *.opi or *.bob
	 *  @return Stream for the *.bob in case it exists, otherwise using the *.opi
	 *  @throws Exception on error
	 */
	public static InputStream open(final String display_name) throws Exception
    {
        try
        {
            // Try to 'upgrade' to *.bob file
            if (display_name.contains(".opi"))
            {
                final URL url = new URL(display_name.replace(".opi", ".bob"));
                final InputStream stream = url.openStream();
                logger.log(Level.INFO, "Opening *.bob instead of " + display_name);
                return stream;
            }
        }
        catch (final Exception ex)
        {
            // Ignore error from *.bob attempts
        }

        final URL url = new URL(display_name);
        return url.openStream();
    }

	/** Parse display into HTML
	 *  @param stream Stream for the display
	 *  @param macros Macros
	 *  @param html HTML is appended to this writer
	 *  @throws Exception on error
	 */
	public DisplayParser(final InputStream stream, final Map<String, String> macros, final PrintWriter html) throws Exception
	{
	    // TODO Pass in x, y for embedded display
		final Element root = XMLUtil.openXMLDocument(stream, "display");

		this.macros = new HashMap<>();
		this.macros.putAll(macros);
		// Fetch macros first to allow use in remaining properties
		this.macros.putAll(MacroUtil.fromXML(root));

		// TODO Replace macros in integer etc. (in XMLUtil)
		width = XMLUtil.getChildInteger(root, "width").orElse(800);
		height = XMLUtil.getChildInteger(root, "height").orElse(600);

		final String background = XMLUtil.getColor(root, "background_color").orElse("#FFF");

		html.println("<div class=\"Screen\" style=\"width: " + width + "px; height: " + height + "px; background-color: " + background + ";\">");
		for (final Element xml : XMLUtil.getChildElements(root, "widget"))
		{
			final Widget widget = WidgetFactory.createWidget(this, xml);
			widget.getHTML(html, 1);
		}
		html.println("</div>");
	}

    @Override
    public Collection<String> getMacroNames()
    {
        return macros.keySet();
    }

    @Override
    public String getMacroValue(final String name)
    {
        return macros.get(name);
    }
}
