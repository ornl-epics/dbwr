/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class ActionButtonWidget extends Widget
{
    static
    {
        WidgetFactory.addJavaScript("actionbutton.js");
        WidgetFactory.addCSS("actionbutton.css");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.ActionButton", "action_button");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.MenuButton", "action_button");
    }

    private String text;

    private static final Color DEFAULT_BACKGROUND = new Color(0xD2, 0xD2, 0xD1);

    public ActionButtonWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "action_button", 100, 30);

        // classes.add("Debug");

        final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
        font.addToStyles(styles);

        XMLUtil.getColor(xml, "foreground_color").ifPresent(color -> styles.put("color", color));

        final String background_color;
        final Element transparency = XMLUtil.getChildElement(xml, "transparent");
        final Color bg = XMLUtil.getAWTColor(xml, "background_color");
        
        if (transparency != null && transparency.getTextContent().equals("true"))
        {
        	background_color = "#0000";
        	styles.put("background-image", "linear-gradient(to right, rgba(0,0,0,0), rgba(0,0,0,0))");
        	styles.put("background-color", background_color);
        	styles.put("border-width", "0");
        }
        else if (bg != null  &&  ! DEFAULT_BACKGROUND.equals(bg))
        {
            background_color = XMLUtil.getWebColor(bg);
            final String highlight = XMLUtil.getWebColor(bg.brighter());
            styles.put("background-image",
                       "linear-gradient(to bottom right, " +
                                        background_color + " 0%, " +
                                        highlight + " 90%)");
        }
        else
            background_color = "#D2D2D2";

        final Element el = XMLUtil.getChildElement(xml, "actions");
        if (el != null)
        {
            int index = 0;
            for (final Element ae : XMLUtil.getChildElements(el, "action"))
            {
                // Always show description, no matter if open_display, write_pv, ...
                final String desc = XMLUtil.getChildString(parent, ae, "description").orElse("");
                attributes.put("data-linked-label-" + index, HTMLUtil.escape(desc));

                final String action_type = ae.getAttribute("type");
                if ("open_webpage".equals(action_type))
                {
                    final String url = XMLUtil.getChildString(parent, ae, "url").orElse("");
                    //attributes.put("data-linked-url-" + index, HTMLUtil.escape(url));
                    attributes.put("data-linked-url-" + index, url);
                }
                else if ("open_display".equalsIgnoreCase(action_type))
                {
                    final String file = XMLUtil.getChildString(parent, ae, "file").orElse(XMLUtil.getChildString(parent, ae, "path").orElse(null));
                    if (file == null)
                        continue;
                    final String target = XMLUtil.getChildString(parent, ae, "target").orElse("");

                    // Read macros into map
                    final Map<String, String> macros = MacroUtil.fromXML(ae);
                    MacroUtil.expand(parent, macros);

                    // Add parent macros
                    for (final String name : parent.getMacroNames())
                        macros.putIfAbsent(name, parent.getMacroValue(name));

                    // TODO Escape file
                    final String resolved = Resolver.resolve(this, file);
                    attributes.put("data-linked-file-" + index, resolved);
                    attributes.put("data-target-" + index, target);
                    if (! macros.isEmpty())
                        attributes.put("data-linked-macros-" + index, HTMLUtil.escape(MacroUtil.toJSON(macros)));

                    // Show link as tool-tip
                    attributes.put("title", file);
                }
                else if ("write_pv".equalsIgnoreCase(action_type))
                {
                    // Add Local pv_name as macro
                    final Optional<String> pv_name_macro = XMLUtil.getChildString(parent, xml, "pv_name");
                    final MacroProvider macros;
                    if (pv_name_macro.isPresent())
                    {
                        final Map<String, String> map = new HashMap<String, String>();
                        for (final String name : parent.getMacroNames())
                            map.put(name, parent.getMacroValue(name));
                        map.put("pv_name", pv_name_macro.get());
                        macros = MacroProvider.forMap(map);
                    }
                    else
                        macros = parent;

                    final String pv = XMLUtil.getChildString(macros, ae, "pv_name").orElse("");
                    final String value = XMLUtil.getChildString(macros, ae, "value").orElse("");
                    attributes.put("data-pv-" + index, pv);
                    attributes.put("data-value-" + index, value);

                    // Show PV name as tool-tip
                    attributes.put("title", pv);
                }
                ++index;
            }
        }

        text = XMLUtil.getChildString(parent, xml, "text").orElse("$(actions)");

        LabelWidget.handleRotationStep(this, xml);

        // TODO Handle $(actions) as text
        if (text.equals("$(actions)"))
            if (attributes.containsKey("data-linked-label-0"))
                text = HTMLUtil.unescape(attributes.get("data-linked-label-0"));
            else
                text = "Button";

        // Rule: Set background color
        getRuleSupport().handleColorRule(parent, xml, this,
                "background_color", background_color,
                "set_action_button_background_color");
        
        handleTextAlignment(this, xml);
    }

    @Override
    protected String getHTMLElement()
    {
        return "button";
    }
    
    static void handleTextAlignment(final Widget widget, final Element xml) throws Exception
    {
        widget.classes.add("AlignedText");
        int align = XMLUtil.getChildInteger(xml, "horizontal_alignment").orElse(1);
        if (align == 0)
            widget.classes.add("AlignedHorizLeft");
        else if (align == 1)
            widget.classes.add("AlignedHorizCenter");
        else if (align == 2)
            widget.classes.add("AlignedRight");

        align = XMLUtil.getChildInteger(xml, "vertical_alignment").orElse(1);
        if (align == 0)
            widget.classes.add("AlignedVertTop");
        else if (align == 1)
            widget.classes.add("AlignedVertCenter");
        else if (align == 2)
            widget.classes.add("AlignedBottom");
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        html.append(HTMLUtil.escape(text));
    }
}

