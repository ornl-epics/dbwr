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

public class RectangleWidget extends EllipseWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Rectangle", "rectangle");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.RoundedRectangle", "rectangle");
    }

    private final double rx, ry;

	public RectangleWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "rectangle");
		rx = XMLUtil.getChildDouble(xml, "corner_width").orElse(0.0);
        ry = XMLUtil.getChildDouble(xml, "corner_height").orElse(0.0);
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        HTMLUtil.indent(html, indent+2);
        // Move the line width 'inside'
        final int inset = line_width / 2;
        final int w = Math.max(0, width-line_width);
        final int h = Math.max(0, height-line_width);
        html.print("<rect x=\"" + inset + "\" y=\"" + inset + "\" width=\"" + w + "\" height=\"" + h + "\"");
        if (rx > 0.0)
            html.print(" rx=\"" + rx + "\"");
        if (ry > 0.0)
            html.print(" ry=\"" + ry + "\"");
        html.println(" stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\" fill=\"" + background_color + "\"/>");
	}
}
