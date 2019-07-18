/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class TextEntryWidget extends BaseTextWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.TextInput", "textentry");
        WidgetFactory.addJavaScript("textentry.js");
        WidgetFactory.addCSS("textentry.css");
    }

    private final boolean multiline;

	public TextEntryWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "textentry", "#7FF");

		multiline = XMLUtil.getChildBoolean(xml, "multi_line").orElse(false);

		attributes.put("type", "text");
		// <input> uses value, multiline uses fillHTML()
		if (! multiline)
		    attributes.put("value", "<" + pv_name + ">");
	}

    @Override
    protected String getHTMLElement()
    {
        return multiline ? "textarea" : "input";
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        // No HTML inside the <input> element
        if (multiline)
            html.append("<" + pv_name + ">");
    }
}
