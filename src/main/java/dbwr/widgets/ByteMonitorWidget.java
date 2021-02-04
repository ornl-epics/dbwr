/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.FontInfo;
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
    private final boolean square;
    private final List<String> labels = new ArrayList<>();

    public ByteMonitorWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "byte_monitor", 160, 20);

		classes.add("Led");

		bits = XMLUtil.getChildInteger(xml, "numBits").orElse(8);
		horizontal = XMLUtil.getChildBoolean(xml, "horizontal").orElse(true);
		square = XMLUtil.getChildBoolean(xml, "square")
		                .orElse(XMLUtil.getChildBoolean(xml, "square_led").orElse(false));

		attributes.put("data-off-color", XMLUtil.getColor(xml, "off_color").orElse("#3C643C"));
		attributes.put("data-on-color", XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C"));

		final int start = XMLUtil.getChildInteger(xml, "startBit").orElse(0);
		if (start > 0)
            attributes.put("data-start", Integer.toString(start));

		if (XMLUtil.getChildBoolean(xml, "bitReverse").orElse(false))
	        attributes.put("data-reverse", "true");
		
		final Element el = XMLUtil.getChildElement(xml, "labels");
		if (el != null)
		    for (Element text : XMLUtil.getChildElements(el, "text"))
		        labels.add(MacroUtil.expand(parent, XMLUtil.getString(text)));
		
		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        final boolean reversed = Boolean.parseBoolean(attributes.get("data-reverse"));
        if (square)
        {
            if (horizontal)
            {
                final int size = width/bits;
                for (int i=0; i<bits; ++i)
                {
                    html.append("<rect x=\"" + (i*size) + "\" y=\"" +  0 + "\" width=\"" + size + "\" height=\"" + height + "\" fill=\"grey\"></rect>");
                    if (i < labels.size())
                        html.append("<text text-anchor=\"middle\" dominant-baseline=\"middle\" transform=\"rotate(-90) translate(" + (-height/2) + "," + (i*size + size/2) + ")\">" + labels.get(reversed ? i : bits-i-1) + "</text>");
                }
            }
            else
            {
                final int size = height/bits;
                for (int i=0; i<bits; ++i)
                {
                    html.append("<rect x=\"" + 0 + "\" y=\"" + (i*size) + "\" width=\"" + width + "\" height=\"" + size + "\" fill=\"grey\"></rect>");
                    if (i < labels.size())
                        html.append("<text text-anchor=\"middle\" dominant-baseline=\"middle\" x=\"" + (width/2) + "\" y=\"" + (i*size + size/2) + "\">" + labels.get(reversed ? i : bits-i-1) + "</text>");
                }
            }
        }
        else
        {
            if (horizontal)
            {
                final int size = width/bits, r = size/2;
                for (int i=0; i<bits; ++i)
                {
                    html.append("<ellipse cx=\"" + (i*size + r) + "\" cy=\"" +  r + "\" rx=\"" + r + "\" ry=\"" + r + "\" fill=\"grey\"></ellipse>");
                    if (i < labels.size())
                        html.append("<text text-anchor=\"end\" dominant-baseline=\"middle\" transform=\"rotate(-90) translate(" + (-size-2) + "," + (i*size + size/2) + ")\">" + labels.get(reversed ? i : bits-i-1) + "</text>");
                }
            }
            else
            {
                final int size = height/bits, r = size/2;
                for (int i=0; i<bits; ++i)
                {
                    html.append("<ellipse cx=\"" + r + "\" cy=\"" + (i*size + r) + "\" rx=\"" + r + "\" ry=\"" + r + "\" fill=\"grey\"></ellipse>");
                    if (i < labels.size())
                        html.append("<text dominant-baseline=\"middle\" x=\"" + (2+size) + "\" y=\"" + (i*size + size/2) + "\">" + labels.get(reversed ? i : bits-i-1) + "</text>");
                }
            }
        }
    }
}
