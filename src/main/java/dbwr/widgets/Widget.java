/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

/** Base for all widgets
 *  @author Kay Kasemir
 */
public class Widget implements ParentWidget
{
    // Widget classes can use static initializer
    // to register javascript, CSS and legacy types
    // with the WidgetFactory
    //
    // static
    // {
    //     WidgetFactory.addJavaScript("this_widget.js");
    //     WidgetFactory.addCSS("this_widget.css");
    //     WidgetFactory.registerLegacy("some.old.type.Widget", "this_widget");
    // }

    /** Parent widget */
    protected final ParentWidget parent;

    /** Classes to add to the HTML for this widget */
	protected final Set<String> classes = new HashSet<>();

	/** Attributes to add to the HTML for this widget */
	protected final Map<String, String> attributes = new LinkedHashMap<>();

	/** Styles (inline) to add to the HTML for this widget */
	protected final Map<String, String> styles = new LinkedHashMap<>();

	/** Widget position and size */
	protected final int x, y, width, height;

	/** @param parent Parent widget
	 *  @param xml XML for this widget
	 *  @param type Type to declare in data-type
	 *  @throws Exception on error
	 */
	public Widget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		this(parent, xml, type, 100, 20);
	}

    /** @param parent Parent widget
     *  @param xml XML for this widget
     *  @param type Type to declare in data-type
     *  @param default_width Width ..
     *  @param default_height .. and height to use when not provided in XML
     *  @throws Exception on error
     */
	public Widget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
	    this.parent = parent;
		x = XMLUtil.getChildInteger(xml, "x").orElse(0);
		y = XMLUtil.getChildInteger(xml, "y").orElse(0);
		width = XMLUtil.getChildInteger(xml, "width").orElse(default_width);
		height = XMLUtil.getChildInteger(xml, "height").orElse(default_height);

		classes.add("Widget");
		attributes.put("data-type", type);

		styles.put("top", Integer.toString(y)+"px");
		styles.put("left", Integer.toString(x)+"px");
		styles.put("width", Integer.toString(width)+"px");
		styles.put("height", Integer.toString(height)+"px");
	}

	@Override
    public URL getDisplay()
	{
        return parent.getDisplay();
    }

    @Override
    public Collection<String> getMacroNames()
	{
        return parent.getMacroNames();
    }

    @Override
    public String getMacroValue(final String name)
    {
        return parent.getMacroValue(name);
    }

    protected void appendClasses(final PrintWriter html)
	{
		html.append("class=\"");
		html.append(classes.stream().collect(Collectors.joining(" ")));
		html.append("\"");
	}

	protected void appendAttributes(final PrintWriter html)
	{
		for (final Map.Entry<String, String> entry : attributes.entrySet())
			html.append(" ")
			    .append(entry.getKey())
			    .append("=\"")
			    .append(HTMLUtil.escape(entry.getValue()))
			    .append("\"");
	}

	protected void appendStyles(final PrintWriter html)
	{
		html.append("style=\"");
		boolean first = true;
		for (final Map.Entry<String, String> entry : styles.entrySet())
		{
			if (first)
				first = false;
			else
				html.append(' ');
			html.append(entry.getKey()).append(": ").append(entry.getValue()).append(';');
		}
		html.append("\"");
	}

	/** @return "div" or "svg" or "..." to use as main element */
	protected String getHTMLElement()
	{
	    return "div";
	}

	protected void startHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.indent(html, indent);
		html.append("<").append(getHTMLElement()).append(" ");
		appendClasses(html);
		appendAttributes(html);
		html.append(' ');
		appendStyles(html);
		html.append(">");
	}

	/** Fill body of the HTML element
	 *
	 *  @param html
	 *  @param indent
	 */
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // Derived class most likely implements this
	}

	protected void endHTML(final PrintWriter html, final int indent)
	{
		html.append("</").append(getHTMLElement()).append(">").println();
	}

	public void getHTML(final PrintWriter html, int indent)
	{
		startHTML(html, indent);
		fillHTML(html, indent);
		endHTML(html, indent);
	}
}

