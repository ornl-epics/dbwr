/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class LedWidget extends BaseLedWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.LED", "led");
        WidgetFactory.addJavaScript("led.js");
    }

    public LedWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "led");

	    final String on_color = XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C");
	    final String off_color = XMLUtil.getColor(xml, "off_color").orElse("#3C643C");
	    attributes.put("data-on-color", on_color);
	    attributes.put("data-off-color", off_color);
        attributes.put("data-bit", Integer.toString(XMLUtil.getChildInteger(xml, "bit").orElse(-1)));
	}
}
