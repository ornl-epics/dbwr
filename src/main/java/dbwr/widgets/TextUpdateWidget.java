/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;

public class TextUpdateWidget extends BaseTextWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.TextUpdate", "textupdate");
        WidgetFactory.addJavaScript("textupdate.js");
        WidgetFactory.addCSS("textupdate.css");
    }

    public TextUpdateWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "textupdate", "#F0F0F0");

        LabelWidget.handleRotationStep(this, xml);
	}
}
