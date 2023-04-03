/*******************************************************************************
 * Copyright (c) 2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.awt.Color;
import java.util.Optional;

import org.w3c.dom.Element;

import dbwr.parser.XMLUtil;

public class StripchartWidget extends Widget
{
    // Uses databrowser.js, databrowser.css, and creates a data-type "databrowser"

	public StripchartWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    super(parent, xml, "databrowser", 400, 300);

        final String start_spec = XMLUtil.getChildString(parent, xml, "start").orElse("-60 sec").toLowerCase();
        final String end_spec = XMLUtil.getChildString(parent, xml, "end").orElse("now").toLowerCase();
        attributes.put("data-timespan", Long.toString(DataBrowserWidget.decodeTimespan(start_spec, end_spec)));
        attributes.put("data-autospan", "false");

        final Element traces = XMLUtil.getChildElement(xml, "traces");
        int i = 0;
        if (traces != null)
            for (final Element trace : XMLUtil.getChildElements(traces, "trace"))
            {
                final String pv_name = XMLUtil.getChildString(parent, trace, "y_pv").orElse(null);
                if (pv_name == null)
                    continue;

                attributes.put("data-pv" + i, pv_name);

                final String label = XMLUtil.getChildString(parent, trace, "name").orElse(null);
                if (label != null)
                {   // Patch property reference "$(traces[i].y_pv)" into PV name
                    if (label.startsWith("$(traces[") && label.endsWith("].y_pv)"))
                        attributes.put("data-label" + i, pv_name);
                    else
                        attributes.put("data-label" + i, label);
                }
                attributes.put("data-linewidth" + i, XMLUtil.getChildInteger(trace, "line_width").orElse(1).toString());
                attributes.put("data-color" + i, getColor(trace, "color").orElse("rgb(0,0,255)"));
                attributes.put("data-ringsize" + i, Integer.toString(DataBrowserWidget.DEFAULT_PLOT_RING_SIZE));
                ++i;
            }
	}

    public static Optional<String> getColor(Element pv, String tag) throws Exception
    {
        final Color color = XMLUtil.getAWTColor(pv, tag);
        if (color == null)
            return Optional.empty();
        return Optional.of(String.format("rgb(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue()));
    }
}
