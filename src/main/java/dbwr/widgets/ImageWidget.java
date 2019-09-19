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

public class ImageWidget extends PVWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.intensityGraph", "image");
        WidgetFactory.addJavaScript("image.js");
    }

    public ImageWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "image", 400, 300);

        attributes.put("data-width", XMLUtil.getChildString(parent, xml, "data_width").orElse("100"));
		attributes.put("data-min", XMLUtil.getChildString(parent, xml, "minimum").orElse("0"));
        attributes.put("data-max", XMLUtil.getChildString(parent, xml, "maximum").orElse("255"));
        attributes.put("data-autoscale", XMLUtil.getChildString(parent, xml, "autoscale").orElse("true"));

        final Element e = XMLUtil.getChildElement(xml, "color_map");
        if (e != null)
        {
            final String map = XMLUtil.getChildString(parent, e, "name")
                                      .orElse("VIRIDIS")
                                      .toLowerCase();
            attributes.put("data-colormap", map);
        }

        // Rules: 'maximum'
        getRuleSupport().handleNumericRule(parent, xml, this, "maximum", 255, "set_image_maximum");
	}

    @Override
    protected String getHTMLElement()
    {
        return "canvas";
    }
}
