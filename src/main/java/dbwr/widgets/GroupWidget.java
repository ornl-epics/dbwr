/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.w3c.dom.Element;

import static dbwr.WebDisplayRepresentation.logger;

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
    }

    private final String name;
    private enum Style { GROUP, TITLE, LINE, NONE };
    private final Style style;
    private final FontInfo font;
    private String foreground_color, line_color, background_color, background_color_label;
    private final boolean transparent;
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
                    style = Style.GROUP;
                    break;
                default:
                    style = Style.NONE;
            }
        }
        else
        {
            int index = XMLUtil.getChildInteger(xml, "style").orElse(0);
            if (index >= 0  &&  index < Style.values().length)
                style = Style.values()[index];
            else
            {
                logger.log(Level.WARNING, "Group " + getWID() + ": Invalid group style " + index);
                style = Style.GROUP;
            }
        }

        font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);

        classes.add("Group");
        // classes.add("Debug");

        for (final Element widget_xml : XMLUtil.getChildElements(xml, "widget"))
        {
            final Widget child = WidgetFactory.createWidget(this, widget_xml);
            children.add(child);
        }

        foreground_color = XMLUtil.getColor(xml, "foreground_color").orElse("#000");
        background_color = XMLUtil.getColor(xml, "background_color").orElse("#FFF");
        background_color_label = background_color;
        // Line color was introduced in version 3.0.0 and defaults to black since then
        line_color = XMLUtil.getColor(xml, "line_color").orElse("#000");
        if (version.major < 3)
        {
            line_color = foreground_color;
            if (style == Style.TITLE)
                foreground_color = background_color;
        }

        transparent = XMLUtil.getChildBoolean(xml, "transparent").orElse(false);
        if (transparent)
            background_color = "#FFF";
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        html.println();

        int hinset = (font.getSize()+1)/2;
        int vinset = hinset;
        if (style == Style.LINE  ||  style == Style.NONE)
            hinset = vinset = 0;
        else if (style == Style.TITLE)
            hinset = 0;
        
        // For the following lines, 'stroke-width=1' should match the desktop display builder look,
        // but colors are a bit dimmed, black shows as gray, unless we use a slightly larger stroke-width 
        if (style == Style.TITLE)
        {   // Group name as box on top with outline below
            HTMLUtil.indent(html, indent+1);
            html.print("<div class=\"GroupTitle\" style=\"top: 0px; left: 0px; width: " + (width-3) +"px; " + font + " color: " + foreground_color + "; background-color: " + line_color + ";\">");
            html.print(name);
            html.println("</div>");
            HTMLUtil.indent(html, indent+1);
            html.println("<svg width=\"" + width + "px\" height=\"" + height + "px\" style=\"" + font + " background-color : " + background_color + "\">");
            HTMLUtil.indent(html, indent+2);
            html.println("<rect x=\"1\" y=\"1\" width=\"" + (width-3) + "\" height=\"" + (height-3) + "\" stroke=\"" + line_color + "\" stroke-width=\"1.2\" fill=\"transparent\"\"/>");
            HTMLUtil.indent(html, indent+1);
            html.println("</svg>");
        }
        else
        {    // SVG for border
            HTMLUtil.indent(html, indent+1);
            html.println("<svg width=\"" + width + "px\" height=\"" + height + "px\" style=\"" + font + " background-color : " + background_color + "\">");
            if (style != Style.NONE)
            {
                HTMLUtil.indent(html, indent+2);
                html.println("<rect x=\"" + hinset + "\" y=\"" + vinset + "\" width=\"" + (width-2*hinset) + "\" height=\"" + (height-2*vinset) + "\" stroke=\"" + line_color + "\" stroke-width=\"1.2\" fill=\"transparent\"\"/>");
                HTMLUtil.indent(html, indent+1);
            }
            html.println("</svg>");
        }
        if (style == Style.GROUP)
        {   // Group name as label on top of border
            HTMLUtil.indent(html, indent+1);
            html.print("<div class=\"GroupLabel\" style=\"top: 0px; left: " + 2*hinset + "px; " + font + " color: " + foreground_color + "; background-color: " + background_color_label + ";\">");
            html.print(name);
            html.println("</div>");
        }

        // Wrap content in <div>
        HTMLUtil.indent(html, indent+1);
        html.println("<div class=\"GroupBox\" style=\"top: " + 2*vinset + "px; left: " + 2*hinset + "px; width: " + (width-4*hinset) + "px; height: " + (height - 4*vinset) + "px;\">");

        for (final Widget child : children)
            child.getHTML(html, indent+2);

        // Close the content-div
        HTMLUtil.indent(html, indent+1);
        html.println("</div>");

        HTMLUtil.indent(html, indent);
    }
}
