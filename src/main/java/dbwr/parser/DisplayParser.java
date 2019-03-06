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
	public final int width, height;
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
	public DisplayParser(final InputStream stream, final MacroProvider macros, final PrintWriter html) throws Exception
	{
	    this(stream, macros, html, null);
	}

    /** Parse display into HTML
     *  @param stream Stream for the display
     *  @param macros Macros
     *  @param html HTML is appended to this writer
     *  @param group_name Parse only this group?
     *  @throws Exception on error
     */
	public DisplayParser(final InputStream stream, final MacroProvider macros, final PrintWriter html, final String group_name) throws Exception
	{
		final Element root = XMLUtil.openXMLDocument(stream, "display");

		// Fetch macros first to allow use in remaining properties,
		// combining macros passed in..
		this.macros = new HashMap<>();
		for (final String name : macros.getMacroNames())
		    this.macros.put(name, macros.getMacroValue(name));
		// .. with those defined in the display
		this.macros.putAll(MacroUtil.fromXML(root));

		// Read from root, or look for a sub-group?
		Element top = root;
		if (group_name != null)
		{
		    // System.err.println("Looking for " + group_name + " - " + macros);
		    for (final Element xml : XMLUtil.getChildElements(top, "widget"))
		    {
		        if (! xml.getAttribute("type").equals("group"))
		            continue;

		        final String name = XMLUtil.getChildString(this, xml, "name").orElse(null);
		        if (group_name.equals(name))
		        {
		            top = xml;
		            break;
		        }
		    }
		}

		// TODO Replace macros in integer etc. (in XMLUtil)
		width = XMLUtil.getChildInteger(top, "width").orElse(800);
		height = XMLUtil.getChildInteger(top, "height").orElse(600);

		final String background = XMLUtil.getColor(root, "background_color").orElse("#FFF");

		// Create HTML for the screen and all its widgets
		html.println("<div class=\"Screen\" style=\"width: " + width + "px; height: " + height + "px; background-color: " + background + ";\">");
		for (final Element xml : XMLUtil.getChildElements(top, "widget"))
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
