/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

public class TextUpdateWidget extends BaseTextWidget
{
	public TextUpdateWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "textupdate", "#F0F0F0");
	}
}
