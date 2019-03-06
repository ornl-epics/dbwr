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
import dbwr.parser.XMLUtil;

public class PolylineWidget extends SvgWidget
{
    protected final int line_width;
	protected final String line_color, points;

	public PolylineWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    this(parent, xml, "polyline");
	}

	protected PolylineWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type);
		line_width = XMLUtil.getChildInteger(xml, "line_width").orElse(3);
		line_color = XMLUtil.getColor(xml, "line_color").orElse("#00F");

		adjustXMLPoints(xml);

	    final StringBuilder buf = new StringBuilder();
	    final Element pe = XMLUtil.getChildElement(xml, "points");
	    if (pe != null)
	        for (final Element e : XMLUtil.getChildElements(pe, "point"))
	        {
	            final int x = (int) (Double.parseDouble(e.getAttribute("x")) + 0.5);
                final int y = (int) (Double.parseDouble(e.getAttribute("y")) + 0.5);
                if (buf.length() > 0)
                    buf.append(' ');
                buf.append(x).append(',').append(y);
	        }
	    points = buf.toString();
	}

	private void adjustXMLPoints(final Element widget_xml)
	{
	    if (! widget_xml.getAttribute("typeId").startsWith("org.csstudio.opibuilder"))
	        return;

	    // Legacy coordinates were relative to display or parent group.
	    // New coords. are relative to this widget's x/y position.
        final Element xml = XMLUtil.getChildElement(widget_xml, "points");
        if (xml != null)
        {
            for (final Element p_xml : XMLUtil.getChildElements(xml, "point"))
            {   // Fetch legacy x, y attributes
                final int x = Integer.parseInt(p_xml.getAttribute("x"));
                final int y = Integer.parseInt(p_xml.getAttribute("y"));
                // Adjust to be relative to widget
                final int nx = x - this.x;
                final int ny = y - this.y;
                p_xml.setAttribute("x", Integer.toString(nx));
                p_xml.setAttribute("y", Integer.toString(ny));
            }
        }
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        HTMLUtil.indent(html, indent+2);
        html.println("<polyline fill=\"transparent\" points=\"" + points +
                     "\" stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\"/>");
	}
}
