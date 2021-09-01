/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
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

/** Ellipse Widget
 *  @author Kay Kasemir
 */
public class EllipseWidget extends SvgWidget
{
    // A comparably simple static widget that uses <svg> instead of <div>.
    // Reads various parameters from XML,
    // which fillHTML() uses to create an <ellipse ...>
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Ellipse", "ellipse");
    }

	protected final int line_width;
	protected final String line_color, background_color;
	protected final boolean transparent;

	public EllipseWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    this(parent, xml, "ellipse");
	}

	protected EllipseWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type);
		line_width = XMLUtil.getChildInteger(xml, "line_width").orElse(3);
		line_color = XMLUtil.getColor(xml, "line_color").orElse("#00F");
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
        HTMLUtil.indent(html, indent+2);
        // Move the line width 'inside'
        html.println("<ellipse cx=\"" + (width+line_width)/2 + "\" cy=\"" + (height+line_width)/2 + "\" rx=\"" + (width-line_width)/2 + "\" ry=\"" + (height-line_width)/2 +
                     "\" stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\" fill=\"" + background_color + "\"/>");
	}
}
