/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Progress Bar Widget
 *  @author Kay Kasemir
 */
public class ProgressBarWidget extends PVWidget
{
    // A comparably simple dynamic widget.
    // PVWidget base reads the <pv_name> into a "data-pv" attribute.
    // getHTMLElement() and/or fillHTML() [this one uses only the former]
    // create the initial HTML,
    // and via addJavaScript() we register code for the web browser which
    // then handles the PV updates.
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.progressbar", "progressbar");
        WidgetFactory.addJavaScript("progressbar.js");
    }

    public ProgressBarWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "progressbar");

		attributes.put("min", XMLUtil.getChildString(parent, xml, "minimum").orElse("0"));
        attributes.put("max", XMLUtil.getChildString(parent, xml, "maximum").orElse("255"));

        if (XMLUtil.getChildBoolean(xml, "limits_from_pv").orElse(true))
            attributes.put("data-limits-from-pv", "true");
	}

    @Override
    protected String getHTMLElement()
    {
        return "meter";
    }
}
