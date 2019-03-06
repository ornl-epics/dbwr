/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;

public class BaseLedWidget extends SvgPVWidget
{
	protected BaseLedWidget(final MacroProvider parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type, 20, 20);
		classes.add("Led");
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		final int rx = width/2, ry = height/2;
		html.append("<ellipse cx=\"" + rx + "\" cy=\"" +  ry + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" fill=\"grey\"></ellipse>");
	}
}
