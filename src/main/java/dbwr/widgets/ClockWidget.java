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

public class ClockWidget extends Widget
{
    static
    {
        WidgetFactory.addJavaScript("clock.js");
    }

	public ClockWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "clock");

		// classes.add("Debug");

		final boolean date = XMLUtil.getChildBoolean(xml, "date_visible").orElse(false);
		if (date)
		    attributes.put("data-date", "true");
	}
}
