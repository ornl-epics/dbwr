/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;

public class TextEntryWidget extends BaseTextWidget
{
    static
    {
        WidgetFactory.addJavaScript("textentry.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.TextInput", "textentry");
    }

	public TextEntryWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "textentry", "#7FF");
	}
}
