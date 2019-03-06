/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.XMLUtil;

public class SvgPVWidget extends SvgWidget
{
	protected final String pv_name;

	public SvgPVWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type, 100, 20);
		pv_name = XMLUtil.getChildString(parent, xml, "pv_name").orElse(null);
		attributes.put("data-pv", pv_name);
	}

	public SvgPVWidget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
		pv_name = XMLUtil.getChildString(parent, xml, "pv_name").orElse(null);
		attributes.put("data-pv", pv_name);
	}
}

