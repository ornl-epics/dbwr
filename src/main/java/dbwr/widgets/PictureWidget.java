/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Picture Widget
 *  @author Kay Kasemir
 */
public class PictureWidget extends Widget
{
    static
    {
        WidgetFactory.addJavaScript("picture.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Image", "picture");
    }

	public PictureWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "picture", 150, 100);

		final String file = XMLUtil.getChildString(this, xml, "file").orElse("missing_image.png");
		attributes.put("data-file", file);

		// See label for rotation?
	}

	@Override
    protected String getHTMLElement()
	{
        return "img";
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // Nothing inside 'img'
	}
}
