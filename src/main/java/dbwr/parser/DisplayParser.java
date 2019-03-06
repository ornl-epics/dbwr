/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.widgets.ParentWidget;
import dbwr.widgets.Widget;

/** Parses a display file (*.opi, *.bob) into HTML
 *  @author Kay Kasemir
 */
public class DisplayParser implements ParentWidget
{
    private final URL display;
	public final int width, height;
	/* TODO private */ final Map<String, String> macros;

	/** Parse display into HTML
     *  @param display Resolved display
	 *  @param macros Macros
	 *  @param html HTML is appended to this writer
	 *  @throws Exception on error
	 */
	public DisplayParser(final Resolver display, final MacroProvider macros, final PrintWriter html) throws Exception
	{
	    this(display, macros, html, null);
	}

    /** Parse display into HTML
     *  @param display Resolved display
     *  @param macros Macros
     *  @param html HTML is appended to this writer
     *  @param group_name Parse only this group?
     *  @throws Exception on error
     */
	public DisplayParser(final Resolver display, final MacroProvider macros, final PrintWriter html, final String group_name) throws Exception
	{
	    this.display = display.getUrl();

		final Element root = XMLUtil.openXMLDocument(display.getStream(), "display");

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
    public URL getDisplay()
    {
        return display;
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
