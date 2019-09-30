/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Scroll bar
 *  @author Kay Kasemir
 */
public class ScrollbarWidget extends PVWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.scrollbar", "scrollbar");
        WidgetFactory.addJavaScript("scrollbar.js");
    }

    public ScrollbarWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "scrollbar");

		attributes.put("min", XMLUtil.getChildString(parent, xml, "minimum").orElse("0"));
        attributes.put("max", XMLUtil.getChildString(parent, xml, "maximum").orElse("255"));

        if (XMLUtil.getChildBoolean(xml, "limits_from_pv").orElse(true))
            attributes.put("data-limits-from-pv", "true");
	}

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        HTMLUtil.indent(html, indent);
        String css = "position: absolute; ";
        if (width > height)
            css += "width: " + Integer.toString(width) + "px; " +
                   "height: " + Integer.toString(height) + "px";
        else
            css += "width: " + Integer.toString(height) + "px; " +
                   "height: " + Integer.toString(width) + "px; " +
                   "transform-origin: " + Integer.toString(height/2) + "px " + Integer.toString(height/2) + "px; " +
                   "transform: rotate(-90deg)";

        html.append("<input type=\"range\" style=\"" + css + "\">");
    }
}
