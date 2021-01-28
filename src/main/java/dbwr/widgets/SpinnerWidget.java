/*******************************************************************************
 * Copyright (c) 2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;

public class SpinnerWidget extends PVWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.spinner", "spinner");
        WidgetFactory.addJavaScript("spinner.js");
    }

    public SpinnerWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "spinner");
        attributes.put("type", "number");
    }
    
    @Override
    protected String getHTMLElement()
    {
        return "input";
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        // No content inside the <input>
    }
}
