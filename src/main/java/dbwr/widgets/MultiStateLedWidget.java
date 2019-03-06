/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
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
                attributes.put("data-state-value-" + index, Integer.toBinaryString(value));
                attributes.put("data-state-color-" + index, color);
                ++index;
    	    }
		}
        attributes.put("data-fallback-color", XMLUtil.getColor(xml, "fallback_color").orElse("#F0F"));
	}
}
