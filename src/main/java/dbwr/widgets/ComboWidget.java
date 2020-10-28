/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.PrintWriter;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class ComboWidget extends PVWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.combo", "combo");
        WidgetFactory.addJavaScript("combo.js");
        WidgetFactory.addCSS("combo.css");
    }

    public ComboWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "combo");

        classes.add("TextField");
		classes.add("Combo");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		// Set 'line-height' to support vertical alignment of text
		styles.put("line-height", styles.get("height"));

		final String background = XMLUtil.getColor(xml, "background_color").orElse("#D2D2D2");
		styles.put("background-color", background);

		// Are items set on the widget?
		if (! XMLUtil.getChildBoolean(xml, "items_from_pv").orElse(true))
		{
			int i = 0;
		    final Element ie = XMLUtil.getChildElement(xml, "items");
		    if (ie != null)
		    {
    		    for (final Element item : XMLUtil.getChildElements(ie, "item"))
    		    {
    		        attributes.put("data-item-" + i, XMLUtil.getString(item));
    		        ++i;
    		    }
    		    // Legacy used <s> per item?!
    		    if (i == 0)
        		    for (final Element item : XMLUtil.getChildElements(ie, "s"))
        		    {
        		        attributes.put("data-item-" + i, XMLUtil.getString(item));
        		        ++i;
        		    }
		    }
		    if (i <= 0)
		    	logger.log(Level.WARNING, "Combo " + getWID() + " without <items>");
		}
	}

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        // Initially, show PV name.
        // Javascript will replace this.
        html.append("<span>");
        HTMLUtil.escape(html, "<" + pv_name + ">");
        html.append("</span>");
    }
}
