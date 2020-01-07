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
import dbwr.parser.XMLUtil;

public class BaseTextWidget extends PVWidget
{
	protected BaseTextWidget(final ParentWidget parent, final Element xml, final String type, final String default_background) throws Exception
	{
		super(parent, xml, type);
		classes.add("TextField");

		switch (XMLUtil.getChildInteger(xml, "format").orElse(0))
		{
		case 1:
		    attributes.put("data-format", "decimal");
		    break;
        case 2:
            attributes.put("data-format", "exponential");
            break;
        case 3:
            attributes.put("data-format", "engineering");
            break;
        case 4:
            attributes.put("data-format", "hex");
            break;
        case 6:
            attributes.put("data-format", "string");
            break;
        case 10:
            attributes.put("data-format", "binary");
            break;
        default:
		}

		final int precision = XMLUtil.getChildInteger(xml, "precision").orElse(-1);
		if (precision != -1)
		    attributes.put("data-precision", Integer.toString(precision));

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(false))
		{
			final String background_color = XMLUtil.getColor(xml, "background_color").orElse(default_background);
			styles.put("background-color", background_color);

			getRuleSupport().handleColorRule(parent, xml, this,
                                             "background_color", background_color,
                                             "set_text_background_color");
		}

		LabelWidget.handleTextAlignment(this, xml);
	}

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        html.append("<span>");
        HTMLUtil.escape(html, "<" + pv_name + ">");
        html.append("</span>");
    }
}
