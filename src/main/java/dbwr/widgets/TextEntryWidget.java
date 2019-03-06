/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.XMLUtil;

public class TextEntryWidget extends PVWidget
{
	public TextEntryWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "textentry");
		// classes.add("Debug");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(false))
		{
			final String background = XMLUtil.getColor(xml, "background_color").orElse("#F0F0F0");
			styles.put("background-color", background);
		}
	}
}
