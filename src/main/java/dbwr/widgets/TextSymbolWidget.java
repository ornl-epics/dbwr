/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

/** Text Symbol Widget: One of N strings
 *  @author Kay Kasemir
 */
public class TextSymbolWidget extends PVWidget
{
    public TextSymbolWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "text-symbol", 32, 32);

        final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
        font.addToStyles(styles);

        styles.put("color", XMLUtil.getColor(xml, "foreground_color").orElse("#000"));

        Element e = XMLUtil.getChildElement(xml, "symbols");
        if (e != null)
        {
            int i = 0;
            for (Element s : XMLUtil.getChildElements(e, "symbol"))
            {
                final String symbol = XMLUtil.getString(s);
                attributes.put("data-symbol-" + i, HTMLUtil.escape(symbol));
                ++i;
            }
        }

        XMLUtil.getChildInteger(xml, "array_index")
               .ifPresent(index -> attributes.put("data-index", Integer.toString(index)));
    }
}