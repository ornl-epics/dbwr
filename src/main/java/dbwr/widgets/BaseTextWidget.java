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
import dbwr.parser.XMLUtil;

public class BaseTextWidget extends PVWidget
{
	protected BaseTextWidget(final ParentWidget parent, final Element xml, final String type, final String default_background) throws Exception
	{
		super(parent, xml, type);
		classes.add("TextField");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(false))
		{
			final String background = XMLUtil.getColor(xml, "background_color").orElse(default_background);
			styles.put("background-color", background);
		}

		XMLUtil.getChildInteger(xml, "horizontal_alignment").ifPresent(align ->
		{
		    if (align == 1)
		        styles.put("text-align", "center");
            else if (align == 2)
                styles.put("text-align", "right");
        });
	}

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        super.fillHTML(html, indent);
    }
}
