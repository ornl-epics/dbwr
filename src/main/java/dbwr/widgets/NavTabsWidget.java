/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Navigation Tabs Widget
 *  @author Kay Kasemir
 */
public class NavTabsWidget extends BaseMacroWidget
{
    static
    {
        WidgetFactory.addCSS("navtabs.css");
        WidgetFactory.addJavaScript("navtabs.js");
    }

    private final List<String> labels = new ArrayList<>();
    private final List<String> files = new ArrayList<>();
    private final List<Map<String, String>> macros = new ArrayList<>();
    private final boolean horizontal;
    private final int tab_width, tab_height, tab_spacing, active_tab;

    public NavTabsWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "navtabs");

        // classes.add("Debug");

        // Locate labels and content links of tabs
        final Element tbs = XMLUtil.getChildElement(xml, "tabs");
        if (tbs != null)
        {
            int i=0;
            for (final Element tb : XMLUtil.getChildElements(tbs, "tab"))
            {
                final String label = XMLUtil.getChildString(parent, tb, "name").orElse("Tab " + (i+1));

                final String file = XMLUtil.getChildString(parent, tb, "file").orElse("");
                final String resolved = Resolver.resolve(this, file);

                final Map<String, String> m = MacroUtil.fromXML(tb);
                MacroUtil.expand(parent, m);

                labels.add(label);
                files.add(resolved);
                macros.add(m);

                ++i;
            }
        }

        horizontal = XMLUtil.getChildInteger(xml, "direction").orElse(1) == 0;
        tab_width = XMLUtil.getChildInteger(xml, "tab_width").orElse(100);
        tab_height = XMLUtil.getChildInteger(xml, "tab_height").orElse(30);
        tab_spacing = XMLUtil.getChildInteger(xml, "tab_spacing").orElse(2);
        active_tab = XMLUtil.getChildInteger(xml, "active_tab").orElse(0);
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        final String classes = horizontal
                             ? "NavTabsButton horizontal"
                             : "NavTabsButton vertical";
        int i = 0, bx = 0, by = 0;
        for (String label : labels)
        {
            if (! label.isEmpty())
            {
                final StringBuilder style = new StringBuilder();
                style.append("left: ").append(bx).append("px;");
                style.append("top: ").append(by).append("px;");
                style.append("width: ").append(tab_width).append("px;");
                style.append("height: ").append(tab_height).append("px;");

                // Mark 'active' tab via a 'selected' button
                html.append("<button class=\"" + classes +
                            (active_tab == i ? " selected" : "") + "\" style=\"" + style.toString() + "\"");
                // Note original size, used by script to resize selected/not-selected
                html.append(" data-width=\"" + tab_width + "\";");
                html.append(" data-height= \"" + tab_height + "\";");
                html.append(" data-file=\"" + HTMLUtil.escape(files.get(i)) + "\"");
                if (! macros.get(i).isEmpty())
                    try
                    {
                        html.append(" data-macros=\"" + HTMLUtil.escape(MacroUtil.toJSON(macros.get(i))) + "\"");
                    }
                    catch (Exception ex)
                    {
                        logger.log(Level.WARNING, "Cannot add navtab button macros", ex);
                    }

                html.append(">");
                HTMLUtil.escape(html, label);
                html.append("</button>");
            }

            if (horizontal)
                bx += tab_width + tab_spacing;
            else
                by += tab_height + tab_spacing;

            ++i;
        }

        final int bw, bh;
        if (horizontal)
        {
            bx = 0;
            by = tab_height;
            bw = width;
            bh = height - by;
        }
        else
        {
            bx = tab_width;
            by = 0;
            bw = width - bx;
            bh = height;
        }

        html.append("<div class=\"NavTabsBody\" style=\"");
        html.append("left: " + bx + "px;");
        html.append("top: " + by + "px;");
        html.append("width: " + bw + "px;");
        html.append("height: " + bh + "px;");
        html.append("\">");
        html.append("</div>");
    }
}
