/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Tabs Widget
 *  @author Kay Kasemir
 */
public class TabsWidget extends Widget
{
    static
    {
        WidgetFactory.addCSS("tabs.css");
        WidgetFactory.addJavaScript("tabs.js");
        // WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Label", "label");
    }

    private final int active;
    private final List<String> labels = new ArrayList<>();
    private final List<List<Widget>> tabs = new ArrayList<>();

	public TabsWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "tabs");

		// classes.add("Debug");

		active = XMLUtil.getChildInteger(xml, "active_tab").orElse(0);
		// Locate labels and content of tabs
		final Element tbs = XMLUtil.getChildElement(xml, "tabs");
		if (tbs != null)
		{
		    int i=0;
		    for (final Element tb : XMLUtil.getChildElements(tbs, "tab"))
		    {
		        final String label = XMLUtil.getChildString(parent, tb, "name").orElse("Tab " + (i+1));
		        labels.add(label);

		        final List<Widget> tab = new ArrayList<>();
		        final Element children = XMLUtil.getChildElement(tb, "children");
		        if (children != null)
		            for (final Element widget_xml : XMLUtil.getChildElements(children, "widget"))
		                tab.add(WidgetFactory.createWidget(this, widget_xml));
		        tabs.add(tab);

		        ++i;
		    }
		}
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        html.append("\n");

        // Tab headers
	    HTMLUtil.indent(html, indent+1);
        html.append("<ul class=\"Tabs\">\n");
        int i=0;
        for (final String label : labels)
        {
            final String classes;
            if (i == active)
                classes = "Tab ActiveTab";
            else
                classes = "Tab";
            HTMLUtil.indent(html, indent+2);
            html.append("<li data-index=\"" + i + "\" class=\"" + classes + "\">")
                .append(HTMLUtil.escape(label))
                .append("</li>\n");

            ++i;
        }
        HTMLUtil.indent(html, indent+1);
        html.append("</ul>\n");

        // Tab bodies
        i = 0;
        for (final List<Widget> body : tabs)
        {
            final String classes;
            if (i == active)
                classes = "TabBody ActiveTab";
            else
                classes = "TabBody";

            HTMLUtil.indent(html, indent+1);
            html.append("<div data-index=\"" + i + "\" class=\"" + classes + "\">\n");
            for (final Widget w : body)
                w.getHTML(html, indent+2);
            HTMLUtil.indent(html, indent+1);
            html.append("</div>\n");

            ++i;
        }

	    HTMLUtil.indent(html, indent);
	}
}
