/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class RadioWidget extends PVWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.radioBox", "radio");

        // For now also use as choice button
        WidgetFactory.registerLegacy("choice", "radio");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.choiceButton", "radio");


        WidgetFactory.addJavaScript("radio.js");
    }

    public RadioWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "radio");

        // classes.add("Debug");

		final boolean items_from_pv = XMLUtil.getChildBoolean(xml, "items_from_pv").orElse(true);
        if (! items_from_pv )
		{
            int i = 0;
		    final Element e = XMLUtil.getChildElement(xml, "items");
		    if (e == null)
                attributes.put("data-item-" + (i++), "Missing Items");
		    else
		        for (final Element item : XMLUtil.getChildElements(e, "item"))
	                attributes.put("data-item-" + (i++), MacroUtil.expand(parent, XMLUtil.getString(item)));
		}

        if (! XMLUtil.getChildBoolean(xml, "horizontal").orElse(true))
            attributes.put("data-horizontal", "false");
	}
}
