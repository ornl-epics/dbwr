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

public class CheckboxWidget extends PVWidget
{
    static
    {
        WidgetFactory.addJavaScript("checkbox.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.checkbox", "checkbox");
    }

    private final String label;

    public CheckboxWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "checkbox");

		label = XMLUtil.getChildString(parent, xml, "label").orElse("Label");

		attributes.put("data-bit", XMLUtil.getChildString(parent, xml, "bit").orElse("0"));
	}

    @Override
    protected void fillHTML(PrintWriter html, int indent)
    {
        html.append("<label>")
            .append("<input type=\"checkbox\">")
            .append(" ").append(HTMLUtil.escape(label))
            .append("</label>");
    }
}
