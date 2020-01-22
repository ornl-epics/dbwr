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

public class PolygonWidget extends PolylineWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.polygon", "polygon");
    }

//	private final String background_color;
	private final String foregroundColor;
	private final String fillLevel;

	public PolygonWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "polygon");
//	    background_color = XMLUtil.getColor(xml, "background_color").orElse("#3232FF");
//        getRuleSupport().handleColorRule(parent, xml, this,
//                                         "background_color", background_color,
//                                         "set_svg_background_color");
		foregroundColor = XMLUtil.getColor(xml, "foreground_color").orElse("#3232FF");
        getRuleSupport().handleColorRule(parent, xml, this,
                                         "foreground_color", foregroundColor,
                                         "set_svg_foreground_color");

        fillLevel = XMLUtil.getChildString(this, xml, "fill_level").orElse("0");
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        HTMLUtil.indent(html, indent+2);
        html.println("<polygon points=\"" + points +
                     "\" stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\" fill=\"" + foregroundColor + "\""
                     		+ "fill-opacity=\"" + fillLevel + "\"/>");
	}
}
