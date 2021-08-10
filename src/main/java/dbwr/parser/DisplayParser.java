/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
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
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.rules.RuleSupport;
import dbwr.widgets.ParentWidget;
import dbwr.widgets.Widget;

/** Parses a display file (*.opi, *.bob) into HTML
 *  @author Kay Kasemir
 */
public class DisplayParser implements ParentWidget
{
    private static final AtomicInteger IDs = new AtomicInteger();

    private final int id;
    private final URL display;
    private final RuleSupport rules = new RuleSupport();
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
     *  @param parent Macros
     *  @param html HTML is appended to this writer
     *  @param group_name Parse only this group?
     *  @throws Exception on error
     */
    public DisplayParser(final Resolver display, final MacroProvider parent, final PrintWriter html, final String group_name) throws Exception
    {
        this(display, parent, html, group_name, -1, -1);
    }
    
    /** Parse display into HTML
     *  @param display Resolved display
     *  @param parent Macros
     *  @param html HTML is appended to this writer
     *  @param group_name Parse only this group?
     *  @param x X offset and ..
     *  @param y .. Y offset for absolute positioning
     *  @throws Exception on error
     */
	public DisplayParser(final Resolver display, final MacroProvider parent,
	                     final PrintWriter html, final String group_name,
	                     final int x, final int y) throws Exception
	{
	    id = IDs.incrementAndGet();
	    this.display = display.getUrl();

		final Element root = XMLUtil.openXMLDocument(display.getStream(), "display");

		// Fetch macros first to allow use in remaining properties,
		macros = new HashMap<>();
		macros.putAll(MacroUtil.fromXML(root));
		macros.put("DID", "DID" + id);
		MacroUtil.expand(parent, macros);

		// Combining macros passed in with those defined in the display
		for (final String name : parent.getMacroNames())
		    macros.putIfAbsent(name, parent.getMacroValue(name));

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

		final String name = XMLUtil.getChildString(this, top, "name").orElse("Display");
		// TODO Replace macros in integer etc. (in XMLUtil)
		width = XMLUtil.getChildInteger(top, "width").orElse(800);
		height = XMLUtil.getChildInteger(top, "height").orElse(600);

		final String background = XMLUtil.getColor(root, "background_color").orElse("#FFF");

		// Create HTML for the screen and all its widgets
		html.println("<div class=\"Screen\" data-name=\"" + HTMLUtil.escape(name) + "\" ");
		html.print("style=\"");
		if (x >= 0  &&  y >= 0)
		{
		    html.println("position: absolute; ");
		    html.println("top: " + y + "px; ");
		    html.println("left: " + x + "px; ");
		}
		html.print("width: " + width + "px; height: " + height + "px; ");
		html.println("background-color: " + background + ";\">");
		for (final Element xml : XMLUtil.getChildElements(top, "widget"))
		{
			final Widget widget = WidgetFactory.createWidget(this, xml);
			widget.getHTML(html, 1);
		}
		html.println("</div>");

		// Create scripts for rules
		rules.addScripts(html);
	}

    @Override
    public URL getDisplay()
    {
        return display;
    }

    @Override
    public RuleSupport getRuleSupport()
    {
        return rules;
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
