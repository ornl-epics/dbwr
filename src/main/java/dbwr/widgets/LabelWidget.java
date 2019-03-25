/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Label Widget
 *  @author Kay Kasemir
 */
public class LabelWidget extends Widget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Label", "label");
    }

    static final FontInfo DEFAULT_FONT = new FontInfo(14, false);
	private final String text;

	public LabelWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "label");
		text = XMLUtil.getChildString(this, xml, "text").orElse("Label text");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(DEFAULT_FONT);
		font.addToStyles(styles);

		int w = width, h = height;
		final int rotate = XMLUtil.getChildInteger(xml, "rotation_step").orElse(0);
        if (rotate > 0)
        {
            if (rotate == 1  ||  rotate == 3)
            {
                // Rotate around upper left corner
                styles.put("transform-origin", "0% 0% 0");
                w = height;
                h = width;
                if (rotate == 1)
                    styles.put("top", Integer.toString(y+w)+"px");
                else if (rotate == 3)
                    styles.put("left", Integer.toString(x+h)+"px");

                styles.put("width", Integer.toString(w)+"px");
                styles.put("height", Integer.toString(h)+"px");
            }
            styles.put("transform", "rotate(-" + (90*rotate) + "deg)");
        }

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(true))
		    styles.put("background-color", XMLUtil.getColor(xml, "background_color").orElse("#FFF"));

        styles.put("color", XMLUtil.getColor(xml, "foreground_color").orElse("#000"));

		int align = XMLUtil.getChildInteger(xml, "horizontal_alignment").orElse(0);
		if (align == 1)
		    styles.put("text-align", "center");
		else if (align == 2)
            styles.put("text-align", "right");

		align = XMLUtil.getChildInteger(xml, "vertical_alignment").orElse(0);
		if (align == 1)
		    styles.put("line-height",  Integer.toString(h) + "px");
		else if (align == 2)
            styles.put("line-height", Integer.toString(2 * h - font.getSize()) + "px");

	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // Turn '\n' into <br>,
	    // then escape each line to handle special characters
	    boolean first = true;
	    for (final String line : text.split("\n"))
	    {
	        if (first)
	            first = false;
	        else
	            html.append("<br>");
	        HTMLUtil.escape(html, line);
	    }
	}
}
