/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
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

    static final FontInfo DEFAULT_FONT = new FontInfo(14, false, false);
	private final String text;

	public LabelWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "label");
		text = XMLUtil.getChildString(this, xml, "text").orElse("Label text");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(DEFAULT_FONT);
		font.addToStyles(styles);

		handleRotationStep(this, xml);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(true))
		    styles.put("background-color", XMLUtil.getColor(xml, "background_color").orElse("#FFF"));

        styles.put("color", XMLUtil.getColor(xml, "foreground_color").orElse("#000"));

        handleTextAlignment(this, xml);
	}

    static int handleRotationStep(final Widget widget, final Element xml) throws Exception
    {
        int w = widget.width, h = widget.height;
		final int rotate = XMLUtil.getChildInteger(xml, "rotation_step").orElse(0);
        if (rotate > 0)
        {
            if (rotate == 1  ||  rotate == 3)
            {
                // Rotate around upper left corner
                widget.styles.put("transform-origin", "0% 0% 0");
                w = widget.height;
                h = widget.width;
                if (rotate == 1)
                    widget.styles.put("top", Integer.toString(widget.y+w)+"px");
                else if (rotate == 3)
                    widget.styles.put("left", Integer.toString(widget.x+h)+"px");

                widget.styles.put("width", Integer.toString(w)+"px");
                widget.styles.put("height", Integer.toString(h)+"px");
            }
            widget.styles.put("transform", "rotate(-" + (90*rotate) + "deg)");
        }
        return h;
    }

    static void handleTextAlignment(final Widget widget, final Element xml) throws Exception
    {
        widget.classes.add("AlignedText");
        int align = XMLUtil.getChildInteger(xml, "horizontal_alignment").orElse(0);
        if (align == 1)
            widget.classes.add("AlignedHorizCenter");
        else if (align == 2)
            widget.classes.add("AlignedRight");

        align = XMLUtil.getChildInteger(xml, "vertical_alignment").orElse(0);
        if (align == 1)
            widget.classes.add("AlignedVertCenter");
        else if (align == 2)
            widget.classes.add("AlignedBottom");
    }

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // To support vertical centering with single as well as multi-line text,
	    // wrap in inner span
	    html.append("<span class=\"InnerText\">");
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
        html.append("</span>");
	}
}
