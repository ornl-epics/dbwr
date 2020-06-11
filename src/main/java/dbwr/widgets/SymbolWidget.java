/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Symbol Widget: One of N images
 *  @author Kay Kasemir
 */
public class SymbolWidget extends PVWidget
{
    static
    {
        WidgetFactory.addJavaScript("symbol.js");
    }

    public SymbolWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "symbol", 100, 100);

        Element e = XMLUtil.getChildElement(xml, "symbols");
        if (e != null)
        {
            int i = 0;
            for (Element s : XMLUtil.getChildElements(e, "symbol"))
            {
                final String file = XMLUtil.getString(s);
                final String resolved = Resolver.resolve(parent, file);
                attributes.put("data-symbol-" + i, resolved);
                ++i;
            }
        }

        XMLUtil.getChildInteger(xml, "array_index")
               .ifPresent(index -> attributes.put("data-index", Integer.toString(index)));
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