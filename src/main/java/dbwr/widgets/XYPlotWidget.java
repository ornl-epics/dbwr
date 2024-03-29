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
		
		final Element x_axis = XMLUtil.getChildElement(xml, "x_axis");
		if(x_axis != null) {
		    final String x_axis_title = XMLUtil.getChildString(parent, x_axis, "title").orElse("");
		    attributes.put("data-x_axis_title", x_axis_title);

		    final String x_axis_visible = XMLUtil.getChildString(parent, x_axis, "visible").orElse("");
		    if(x_axis_visible.isEmpty())
		    	attributes.put("data-x_axis_visible", "true");
		    else 
		    	attributes.put("data-x_axis_visible", x_axis_visible);
		}
		else
		{
            attributes.put("data-x_axis_visible", "true");
		    attributes.put("data-x_axis_title", "");
		}

		final Element y_axes = XMLUtil.getChildElement(xml, "y_axes");
		// We currently only support 2 Y Axis, 0 being the primary and 1 being the secondary
		int axis_index = 0;
		if (y_axes != null) 
		{
			for (final Element y_axis : XMLUtil.getChildElements(y_axes, "y_axis"))
			{
				if(axis_index == 0)
				{
				    final String y_axis_title = XMLUtil.getChildString(parent, y_axis, "title").orElse("");
					attributes.put("data-y_axis_0_title", y_axis_title);
					final Boolean y_axis_log_scale = XMLUtil.getChildBoolean(y_axis, "log_scale").orElse(false);
					attributes.put("data-y_axis_0_mode", y_axis_log_scale ? "log" : "null");
				} 
				else if (axis_index == 1) 
				{
					attributes.put("data-y_axis_1_visible", "true");
				    final String y_axis_title = XMLUtil.getChildString(parent, y_axis, "title").orElse("");
					attributes.put("data-y_axis_1_title", y_axis_title);
					final Boolean y_axis_log_scale = XMLUtil.getChildBoolean(y_axis, "log_scale").orElse(false);
					attributes.put("data-y_axis_1_mode", y_axis_log_scale ? "log" : "null");
					final Boolean y_axis_1_on_right = XMLUtil.getChildBoolean(y_axis, "on_right").orElse(false);
					attributes.put("data-y_axis_1_on_right", y_axis_1_on_right ? "right" : "left");
				}
				else
					break;
				++axis_index;
			}
		}
		
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
                attributes.put("data-y_axis" + i, XMLUtil.getChildInteger(xml, "trace_" + i + "_axis").orElse(0).toString());
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
            attributes.put("data-y_axis" + i, XMLUtil.getChildInteger(trace, "axis").orElse(0).toString());
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
