/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class BoolButtonWidget extends PVWidget
{
    static
    {
        WidgetFactory.addJavaScript("boolbutton.js");
        WidgetFactory.addCSS("boolbutton.css");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.BoolButton", "bool_button");
    }

    private boolean show_led;

	public BoolButtonWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "bool_button");

		// classes.add("Debug");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (XMLUtil.getChildBoolean(xml, "labels_from_pv").orElse(false))
	        attributes.put("data-pv-labels", "true");

	    final String on = XMLUtil.getChildString(parent, xml, "on_label").orElse("On");
	    final String off = XMLUtil.getChildString(parent, xml, "off_label").orElse("Off");
        attributes.put("data-on", on);
        attributes.put("data-off", off);

		show_led = XMLUtil.getChildBoolean(xml, "show_led").orElse(true);

		// Don't show LED when using this widget as a stand-in for the slide button
		if (xml.getAttribute("type").equals("slide_button"))
		    show_led = false;

		final String on_color = XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C");
		final String off_color = XMLUtil.getColor(xml, "off_color").orElse("#3C643C");
        attributes.put("data-on-color", on_color);
		attributes.put("data-off-color", off_color);

		attributes.put("data-bit", Integer.toString(XMLUtil.getChildInteger(xml, "bit").orElse(-1)));

		XMLUtil.getColor(xml, "foreground_color").ifPresent(color -> styles.put("color", color));
	}

	@Override
	protected String getHTMLElement()
	{
	    return "button";
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    if (show_led)
	    {
	        int size = Math.min(width, height);
            final int r = (int) (size/3.7);
            size = r+r;

            html.append("<svg class=\"LED\" style=\"width: " + size + "px; height: " + size + "px;\">");
            html.append("<ellipse cx=\"" + r + "\" cy=\"" +  r + "\" rx=\"" + r + "\" ry=\"" + r + "\" fill=\"grey\"></ellipse>");
    	    html.append("</svg>");
	    }
        html.append("<span>");
        html.append(" The Label");
        html.append("</span>");
	}
}
