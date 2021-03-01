/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
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

        // Legacy BOY *.opi and this should be a multi-state LED?
        final int state_count = XMLUtil.getChildInteger(xml, "state_count").orElse(-1);
        if (state_count > 2)
        {
            // Change to multi-state LED
            attributes.put("data-type", "multi_state_led");
            // Read legacy states
            for (int i=0; i<state_count; ++i)
            {
                final int value = XMLUtil.getChildDouble(xml, "state_value_" + i).orElse((double) i).intValue();
                final String color = XMLUtil.getColor(xml, "state_color_" + i).orElse("#000");
                attributes.put("data-state-value-" + i, Integer.toString(value));
                attributes.put("data-state-color-" + i, color);
            }
            attributes.put("data-fallback-color", XMLUtil.getColor(xml, "state_color_fallback").orElse("#F0F"));
            return;
        }

        final String on_color = XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C");
        final String off_color = XMLUtil.getColor(xml, "off_color").orElse("#3C643C");
        attributes.put("data-value", "0");
        attributes.put("data-on-color", on_color);
        attributes.put("data-off-color", off_color);
        attributes.put("data-on-label", XMLUtil.getChildString(this, xml, "on_label").orElse(""));
        attributes.put("data-off-label", XMLUtil.getChildString(this, xml, "off_label").orElse(""));
        attributes.put("data-bit", Integer.toString(XMLUtil.getChildInteger(xml, "bit").orElse(-1)));

        getRuleSupport().handleColorRule(parent, xml, this,
                                         "off_color", off_color,
                                         "set_led_off_color");
        getRuleSupport().handleColorRule(parent, xml, this,
                                         "on_color", on_color,
                                         "set_led_on_color");
    }
}
