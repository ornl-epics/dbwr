/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class GroupWidget extends Widget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.groupingContainer", "group");
        WidgetFactory.addCSS("group.css");
        WidgetFactory.addJavaScript("group.js");
    }

	private final String name;
	private final int style;
	private final FontInfo font;
	private final List<Widget> children = new ArrayList<>();
	private final Map<String, String> macros;

	public GroupWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "group", 300, 200);
		// Get macros first in case they're used for the name etc.
		macros = MacroUtil.fromXML(xml);
		MacroUtil.expand(parent, macros);

		name = XMLUtil.getChildString(this, xml, "name").orElse("");

		if (xml.getAttribute("typeId").startsWith("org.csstudio.opibuilder"))
		{
		    switch (XMLUtil.getChildInteger(xml, "border_style").orElse(0))
		    {
		    case 13:
		        // GROUP
		        style = 0;
		        break;
		    default:
		        // NONE
		        style = 3;
		    }
		}
		else
		    style = XMLUtil.getChildInteger(xml, "style").orElse(0);

		font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);

		classes.add("Group");
		// classes.add("Debug");

		for (final Element widget_xml : XMLUtil.getChildElements(xml, "widget"))
		{
			final Widget child = WidgetFactory.createWidget(this, widget_xml);
			children.add(child);
		}
	}

	@Override
    public Collection<String> getMacroNames()
	{
	    final List<String> names = new ArrayList<>(super.getMacroNames());
	    names.addAll(macros.keySet());
        return names;
    }

    @Override
    public String getMacroValue(final String name)
    {
        final String result = macros.get(name);
        if (result != null)
            return result;
        return super.getMacroValue(name);
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		html.println();

		if (style == 0)
		{
			final int inset = (font.getSize()+1)/2;
			final int dinset = 2*inset;

			// SVG for border
			HTMLUtil.indent(html, indent+1);
			html.println("<svg width=\"" + width + "px\" height=\"" + height + "px\" style=\"" + font + "\">");
			HTMLUtil.indent(html, indent+2);
			html.println("<rect x=\"" + inset + "\" y=\"" + inset + "\" width=\"" + (width-dinset) + "\" height=\"" + (height-dinset) + "\" stroke=\"#000\" stroke-width=\"2\" fill=\"transparent\"\"/>");
			HTMLUtil.indent(html, indent+1);
			html.println("</svg>");

			// Group name as label on top of border
            HTMLUtil.indent(html, indent+1);
            html.print("<div class=\"GroupLabel\" style=\"top: 0px; left: " + dinset + "px; " + font + " background-color: white;\">");
            html.print(name);
            html.println("</div>");

            // Wrap content in <div>
			HTMLUtil.indent(html, indent+1);
			html.println("<div class=\"GroupBox\" style=\"top: " + dinset + "px; left: " + dinset + "px; width: " + (width-4*inset) + "px; height: " + (height - 4*inset) + "px;\">");
		}

		for (final Widget child : children)
			child.getHTML(html, indent+2);

		if (style == 0)
		{   // Close the content-div
			HTMLUtil.indent(html, indent+1);
			html.println("</div>");
		}

		HTMLUtil.indent(html, indent);
	}
}
