/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

/** Label Widget
 *  @author Kay Kasemir
 */
public class LabelWidget extends Widget
{
	static final FontInfo DEFAULT_FONT = new FontInfo(14, false);
	private final String text;

	public LabelWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "label");
		text = XMLUtil.getChildString(this, xml, "text").orElse("Label text");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(DEFAULT_FONT);
		font.addToStyles(styles);

		final int align = XMLUtil.getChildInteger(xml, "horizontal_alignment").orElse(0);
		if (align == 1)
		    styles.put("text-align", "center");
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
