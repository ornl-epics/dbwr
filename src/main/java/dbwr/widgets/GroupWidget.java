/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Group Widget
 *  @author Kay Kasemir
 */
public class GroupWidget extends BaseMacroWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.groupingContainer", "group");
        WidgetFactory.addCSS("group.css");
        WidgetFactory.addJavaScript("group.js");
    }

    private final String name;
    private final int style;
    private final FontInfo font;
    private final List<Widget> children = new ArrayList<>();

    public GroupWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "group", 300, 200);

        name = XMLUtil.getChildString(this, xml, "name").orElse("");

        if (xml.getAttribute("typeId").startsWith("org.csstudio.opibuilder"))
        {
            switch (XMLUtil.getChildInteger(xml, "border_style").orElse(0))
            {
            case 13:
                // GROUP
                style = 0;
                break;
            default:
                // NONE
                style = 3;
            }
        }
        else
            style = XMLUtil.getChildInteger(xml, "style").orElse(0);

        font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);

        classes.add("Group");
        // classes.add("Debug");

        for (final Element widget_xml : XMLUtil.getChildElements(xml, "widget"))
        {
            final Widget child = WidgetFactory.createWidget(this, widget_xml);
            children.add(child);
        }
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        html.println();

        int hinset = (font.getSize()+1)/2;
        int vinset = hinset;
        if (style == 2)
            hinset = vinset = 0;
        else if (style == 1)
            hinset = 0;

        // Style 0: Group, 1:Title, 2:Line, 3:None
        if (style == 1)
        {   // Group name as box on top with outline below
            HTMLUtil.indent(html, indent+1);
            html.print("<div class=\"GroupTitle\" style=\"top: 0px; left: 0px; width: " + (width-3) +"px; " + font + " color: white; background-color: black;\">");
            html.print(name);
            html.println("</div>");
            HTMLUtil.indent(html, indent+1);
            html.println("<svg width=\"" + width + "px\" height=\"" + height + "px\" style=\"" + font + "\">");
            HTMLUtil.indent(html, indent+2);
            html.println("<rect x=\"1\" y=\"1\" width=\"" + (width-3) + "\" height=\"" + (height-3) + "\" stroke=\"#000\" stroke-width=\"2\" fill=\"transparent\"\"/>");
            HTMLUtil.indent(html, indent+1);
            html.println("</svg>");
        }
        else if (style != 3)
        {    // SVG for border
            HTMLUtil.indent(html, indent+1);
            html.println("<svg width=\"" + width + "px\" height=\"" + height + "px\" style=\"" + font + "\">");
            HTMLUtil.indent(html, indent+2);
            html.println("<rect x=\"" + hinset + "\" y=\"" + vinset + "\" width=\"" + (width-2*hinset) + "\" height=\"" + (height-2*vinset) + "\" stroke=\"#000\" stroke-width=\"2\" fill=\"transparent\"\"/>");
            HTMLUtil.indent(html, indent+1);
            html.println("</svg>");
        }
        if (style == 0)
        {   // Group name as label on top of border
            HTMLUtil.indent(html, indent+1);
            html.print("<div class=\"GroupLabel\" style=\"top: 0px; left: " + 2*hinset + "px; " + font + " background-color: white;\">");
            html.print(name);
            html.println("</div>");
        }
        if (style != 3)
        {   // Wrap content in <div>
            HTMLUtil.indent(html, indent+1);
            html.println("<div class=\"GroupBox\" style=\"top: " + 2*vinset + "px; left: " + 2*hinset + "px; width: " + (width-4*hinset) + "px; height: " + (height - 4*vinset) + "px;\">");
        }

        for (final Widget child : children)
            child.getHTML(html, indent+2);

        if (style != 3)
        {   // Close the content-div
            HTMLUtil.indent(html, indent+1);
            html.println("</div>");
        }

        HTMLUtil.indent(html, indent);
    }
}
