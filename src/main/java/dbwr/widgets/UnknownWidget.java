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

public class UnknownWidget extends Widget
{
	private final String type;

	public UnknownWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, "unknown");
		this.type = type;
		classes.add("Debug");
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.escape(html, "{" + type + "}");
	}
}
