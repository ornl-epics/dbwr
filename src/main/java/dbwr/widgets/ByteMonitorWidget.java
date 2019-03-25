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

public class ByteMonitorWidget extends SvgPVWidget
{
    static
    {
        WidgetFactory.addJavaScript("bytemonitor.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.bytemonitor", "byte_monitor");
    }

    private final int bits;
    private final boolean horizontal;

    public ByteMonitorWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "byte_monitor", 160, 20);

		classes.add("Led");

		bits = XMLUtil.getChildInteger(xml, "numBits").orElse(8);
		horizontal = XMLUtil.getChildBoolean(xml, "horizontal").orElse(true);

		attributes.put("data-off-color", XMLUtil.getColor(xml, "off_color").orElse("#3C643C"));
		attributes.put("data-on-color", XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C"));

		if (XMLUtil.getChildBoolean(xml, "bitReverse").orElse(false))
	        attributes.put("data-reverse", "true");
	}

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        if (horizontal)
        {
            final int size = width/bits, rx = size/2, ry = height/2;
            for (int i=0; i<bits; ++i)
                html.append("<ellipse cx=\"" + (i*size + rx) + "\" cy=\"" +  ry + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" fill=\"grey\"></ellipse>");
        }
        else
        {
            final int size = height/bits, rx = width/2, ry = size/2;
            for (int i=0; i<bits; ++i)
                html.append("<ellipse cx=\"" + rx + "\" cy=\"" + (i*size + ry) + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" fill=\"grey\"></ellipse>");
        }
    }
}
