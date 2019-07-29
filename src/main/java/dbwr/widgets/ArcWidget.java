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

public class ArcWidget extends SvgWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.arc", "arc");
    }

    protected final double arc_start, arc_size;
	protected final int line_width;
	protected final String line_color, background_color;
	protected final boolean transparent;

	public ArcWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    this(parent, xml, "arc");
	}

	protected ArcWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type);
		arc_start = XMLUtil.getChildDouble(xml, "start_angle").orElse(0.0);
		arc_size = XMLUtil.getChildDouble(xml, "total_angle").orElse(90.0);
		line_width = XMLUtil.getChildInteger(xml, "line_width").orElse(3);
		line_color = XMLUtil.getColor(xml, "line_color").orElse("#00F");
		// Check legacy 'fill' option
		final Boolean fill = XMLUtil.getChildBoolean(xml, "fill").orElse(null);
		if (fill != null)
		    transparent = ! fill;
		else
		    transparent = XMLUtil.getChildBoolean(xml, "transparent").orElse(false);
		if (transparent)
		    background_color = "transparent";
		else
		{
		    background_color = XMLUtil.getColor(xml, "background_color").orElse("#1E90FF");
	        getRuleSupport().handleColorRule(parent, xml, this,
                                             "background_color", background_color,
                                             "set_svg_background_color");
		}
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // Debug: Mark outline
        // HTMLUtil.indent(html, indent+2);
        // html.println("<rect x=\"0\" y=\"0\" width=\"" + width + "\" height=\"" + height +
        //              "\" stroke=\"green\" stroke-width=\"1\" fill=\"transparent\"/>");

        HTMLUtil.indent(html, indent+2);
        html.print("<path stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\" fill=\"" + background_color + "\" d=\"");

        // Line is inside the shape, i.e. reduce radius by half the line width
        final int cx = width / 2, cy = height / 2;
        final int rx = cx - line_width/2, ry = cy - line_width/2;
        double angle = Math.toRadians(arc_start);
        final double x0 = cx + rx * Math.cos(angle);
        final double y0 = cy - ry * Math.sin(angle);

        final boolean filled = ! background_color.equals("transparent");
        if (filled)
        {   // Move to center
            html.print("M" + cx + "," + cy);

            // Line to start angle
            html.print(" L" + x0 + "," + y0);
        }
        else
        {   // Move to start angle
            html.print("M" + x0 + "," + y0);
        }
        // Arc to end angle
        // Arc cannot draw a full circle where start and end point of arc match.
        // Easiest workaround is to draw almost but not quite 360 degrees
        angle = Math.toRadians(arc_start + Math.min(359.5, arc_size));
        final double x1 = cx + rx * Math.cos(angle);
        final double y1 = cy - ry * Math.sin(angle);

        // Sweep clockwise (1) or CCW (0)?
        final int sweep = arc_size >= 0.0 ? 0 : 1;

        // Draw the 'large' (1) or 'small' (0) arc?
        final int large = arc_size > 180.0 ? 1 : 0;

        html.print(" A" + rx + "," + ry + " 0 " + large + " " + sweep + " " + x1 + "," + y1);

        if (filled)
        {   // Close, i.e. line back to center
            html.print(" Z");
        }
        html.print("\"/>");
	}
}
