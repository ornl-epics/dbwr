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

/** XY Plot widget
 *  @author Kay Kasemir
 */
public class XYPlotWidget extends Widget
{
    // Reads information for all the traces,
    // their X and Y PV etc.,
    // and places that information into "data-..." attributes.
    // In the HTML, base class then creates a plain <div> with those
    // attributes.
    // xyplot.js in client then connects to the PVs and
    // creates/updates plot with received samples.
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.xyGraph", "xyplot");
        WidgetFactory.addJavaScript("xyplot.js");
    }

    public XYPlotWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "xyplot", 400, 300);

		final Element traces = XMLUtil.getChildElement(xml, "traces");
		if (traces == null)
		{
		    // Check for legacy trace_0_x_pv, trace_0_y_pv
		    int i=0;
		    while (true)
		    {
		        final String x_pv = XMLUtil.getChildString(parent, xml, "trace_" + i + "_x_pv").orElse("");
                final String y_pv = XMLUtil.getChildString(parent, xml, "trace_" + i + "_y_pv").orElse("");
    		    if (y_pv.isEmpty()  &&  x_pv.isEmpty())
    		        break;

    		    attributes.put("data-pvx" + i, x_pv);
                attributes.put("data-pvy" + i, y_pv);
                attributes.put("data-color" + i, XMLUtil.getColor(xml, "trace_" + i + "_trace_color").orElse(Integer.toString(0)));
                final String name = XMLUtil.getChildString(parent, xml, "trace_" + i + "_name").orElse("");
                if (!name.isEmpty())
                    attributes.put("data-name" + i, name);
                ++i;
		    }
		    return;
		}

		// Place PV names into data-pvx0, pvy0, pvx1, pvy1, ...
		int i=0;
		for (final Element trace : XMLUtil.getChildElements(traces, "trace"))
		{
		    final String x_pv = XMLUtil.getChildString(parent, trace, "x_pv").orElse("");
		    final String y_pv = XMLUtil.getChildString(parent, trace, "y_pv").orElse("");
            attributes.put("data-pvx" + i, x_pv);
            attributes.put("data-pvy" + i, y_pv);
            attributes.put("data-color" + i, XMLUtil.getColor(trace, "color").orElse(Integer.toString(0)));
            final String name = XMLUtil.getChildString(parent, trace, "name").orElse("");
            if (!name.isEmpty())
                attributes.put("data-name" + i, name);

            // Use bars instead of lines?
            if (XMLUtil.getChildInteger(trace, "trace_type").orElse(1) == 5)
                attributes.put("data-bars" + i, "true");
            else
            {
                // Use points instead of lines?
                if (XMLUtil.getChildInteger(trace, "point_type").orElse(0) != 0)
                    attributes.put("data-pointsize" + i, XMLUtil.getChildInteger(trace, "point_size").orElse(10).toString());
                else
                    attributes.put("data-linewidth" + i, XMLUtil.getChildInteger(trace, "line_width").orElse(1).toString());
            }
            ++i;
		}
	}
}
