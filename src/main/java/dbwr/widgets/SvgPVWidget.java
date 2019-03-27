/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

public class SvgPVWidget extends SvgWidget
{
	protected final String pv_name;

	public SvgPVWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		this(parent, xml, type, 100, 20);
	}

	public SvgPVWidget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
		pv_name = XMLUtil.getChildString(parent, xml, "pv_name").orElse(null);
		attributes.put("data-pv", pv_name);

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
    protected void startHTML(final PrintWriter html, final int indent)
	{
	    super.startHTML(html, indent);
	    // Show PV name as tool-tip
	    HTMLUtil.indent(html, indent);
        html.append("<title>").append(HTMLUtil.escape(pv_name)).append("</title>");
    }
}

