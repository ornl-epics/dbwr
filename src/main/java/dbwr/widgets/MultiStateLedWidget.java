/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.XMLUtil;

public class MultiStateLedWidget extends BaseLedWidget
{
    public MultiStateLedWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "multi_state_led");

		final Element states = XMLUtil.getChildElement(xml, "states");
		if (states != null)
		{
    	    int index = 0;
    	    for (final Element state : XMLUtil.getChildElements(states, "state"))
    	    {
    	        final int value = XMLUtil.getChildInteger(state, "value").orElse(index);
    	        final String color = XMLUtil.getColor(state, "color").orElse("#000");
                final String label = XMLUtil.getChildString(this, state, "label").orElse("");
                attributes.put("data-state-value-" + index, Integer.toString(value));
                attributes.put("data-state-color-" + index, color);
                attributes.put("data-state-label-" + index, label);
                ++index;
    	    }
		}
        attributes.put("data-fallback-color", XMLUtil.getColor(xml, "fallback_color").orElse("#F0F"));
        attributes.put("data-fallback_label", XMLUtil.getChildString(this, xml, "fallback_label").orElse("Err"));
	}
}
