/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class DataBrowserWidget extends Widget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.trends.databrowser.opiwidget", "databrowser");
        WidgetFactory.addJavaScript("databrowser.js");
        WidgetFactory.addCSS("databrowser.css");
    }

	public DataBrowserWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    super(parent, xml, "databrowser", 400, 300);

        // Get *.plt file, falling back to legacy property
        String file = XMLUtil.getChildString(parent, xml, "file").orElse(null);
        if (file == null)
            file = XMLUtil.getChildString(parent, xml, "filename").orElse(null);
        if (file == null)
        {
            logger.log(Level.WARNING, "Data browser widget without file");
            return;
        }

        // Parse the *.plt file
        final String resolved = Resolver.resolve(this, file);
        final URL url = new URL(resolved);
        final Element plt = XMLUtil.openXMLDocument(url.openStream(), "databrowser");
        final Element pvlist = XMLUtil.getChildElement(plt, "pvlist");
        int i = 0;
        if (pvlist != null)
            for (final Element pv : XMLUtil.getChildElements(pvlist, "pv"))
            {
                final String pv_name = XMLUtil.getChildString(parent, pv, "name").orElse(null);
                if (pv_name == null)
                    continue;

                attributes.put("data-pv" + i, pv_name);

                final String label = XMLUtil.getChildString(parent, pv, "display_name").orElse(null);
                if (label != null)
                    attributes.put("data-label" + i, label);
                attributes.put("data-linewidth" + i, XMLUtil.getChildInteger(pv, "linewidth").orElse(1).toString());
                attributes.put("data-color" + i, getColor(pv, "color").orElse(Integer.toString(i)));
                ++i;
            }
	}

    private Optional<String> getColor(Element pv, String tag) throws Exception
    {
        final Element color = XMLUtil.getChildElement(pv, tag);
        if (color != null)
        {
            final int red = XMLUtil.getChildInteger(color, "red").orElse(0);
            final int green = XMLUtil.getChildInteger(color, "green").orElse(0);
            final int blue = XMLUtil.getChildInteger(color, "blue").orElse(0);
            return Optional.of(String.format("rgb(%d,%d,%d)", red, green, blue));
        }
        return Optional.empty();
    }
}
