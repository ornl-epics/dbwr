/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.XMLUtil;

public class ImageWidget extends PVWidget
{
    public ImageWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "image", 400, 300);

		classes.add("Debug");

        attributes.put("data-width", XMLUtil.getChildString(parent, xml, "data_width").orElse("100"));
		attributes.put("data-min", XMLUtil.getChildString(parent, xml, "minimum").orElse("0"));
        attributes.put("data-max", XMLUtil.getChildString(parent, xml, "maximum").orElse("255"));
	}

    @Override
    protected String getHTMLElement()
    {
        return "canvas";
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        // TODO Auto-generated method stub
    }
}
