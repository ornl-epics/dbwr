/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

/** Base class for all widgets that have a "pv_name"
 * 
 *  Adds a "data-pv" property to the generated HTML widget.
 *  Javascript in dbwr.js detects that attribute,
 *  connects PV and invokes widget_update_methods[type]
 *  for each received value.
 *  
 *  @author Kay Kasemir
 */
public class PVWidget extends Widget
{
	protected final String pv_name;

	/** Modify certain PV name elements
	 *  @param pv Original PV name
	 *  @return Potentially updated PV name
	 */
	public static String munchPV(String pv)
	{
	    if (pv == null)
	        return null;

	    // Remove ' {"longString":true}'
	    final int i = pv.indexOf(" {\"long");
	    if (i > 0)
	        pv = pv.substring(0, i);

	    return pv;
	}

	public PVWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		this(parent, xml, type, 100, 20);
	}

	public PVWidget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
		pv_name = munchPV(XMLUtil.getChildString(parent, xml, "pv_name").orElse(null));
		attributes.put("data-pv", pv_name);
		// Show PV name as tool-tip
        attributes.put("title", pv_name);

        XMLUtil.getChildBoolean(xml, "border_alarm_sensitive").ifPresent(alarm_border ->
        {
            if (! alarm_border)
                attributes.put("data-alarm-border", "false");
        });

        XMLUtil.getChildBoolean(xml, "show_units").ifPresent(units ->
        {
            if (! units)
                attributes.put("data-show-units", "false");
        });
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.escape(html, "<" + pv_name + ">");
	}
}
