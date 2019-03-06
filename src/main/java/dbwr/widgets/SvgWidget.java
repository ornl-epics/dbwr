/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

public class SvgWidget extends Widget
{
	public SvgWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type, 100, 20);
	}

	public SvgWidget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
	}

	@Override
	protected String getHTMLElement()
	{
		return "svg";
	}
}

