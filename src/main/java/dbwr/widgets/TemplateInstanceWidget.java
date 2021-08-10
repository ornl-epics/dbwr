/*******************************************************************************
 * Copyright (c) 2021 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.DisplayParser;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.XMLUtil;

/** Embedded display widget
 *
 *  <p>Loads the content of embedded display
 *  and prints it into the HTML.
 *
 *  @author Kay Kasemir
 */
public class TemplateInstanceWidget extends Widget
{
    private final String file;
    private final List<Map<String, String>> instances = new ArrayList<>();
    private final boolean horizontal;
    private final int gap;
    private String embedded_html;
    
    public TemplateInstanceWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "template", 300, 200);
        // classes.add("Debug");

        file = XMLUtil.getChildString(this, xml, "file").orElse("");
        final Element insts_xml = XMLUtil.getChildElement(xml, "instances");
        if (insts_xml != null)
            for (Element inst : XMLUtil.getChildElements(insts_xml, "instance"))
            {
                final Map<String, String> macros = MacroUtil.fromXML(inst);
                System.out.println("Instance: " + macros);
                instances.add(macros);
            }
        horizontal = XMLUtil.getChildBoolean(xml, "horizontal").orElse(false);
        gap = XMLUtil.getChildInteger(xml, "gap").orElse(10);
    }

    private String parseContent()
    {
        // System.out.println(file + " with " + macros);
        if (file.isEmpty())
            return "";

        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final PrintWriter buf_writer = new PrintWriter(buf);
        int total_width = 0, total_height = 0;
        
        // Loop over instances
        int x = 0, y = 0;
        for (Map<String, String> instance : instances)
        {
            final MacroProvider macros = MacroProvider.forMap(instance);
            final DisplayParser embedded_display;
            try
            {
                final String resolved = Resolver.resolve(this, file);
                final Resolver display = new Resolver(resolved);
                embedded_display = new DisplayParser(display, macros, buf_writer, "", x, y);
            }
            catch (final Exception ex)
            {
                logger.log(Level.WARNING, "Cannot read embedded display " + file, ex);
                classes.add("Error");
                classes.add("BorderDisconnected");
                return "Cannot embed '" + HTMLUtil.escape(file) + "'";
            }
    
            if (horizontal)
            {
                x += embedded_display.width + gap;
                total_width += embedded_display.width + gap;
                total_height = embedded_display.height;
            }
            else
            {
                y += embedded_display.height + gap;
                total_width = embedded_display.width;
                total_height += embedded_display.height + gap;
            }
        }
        // Update container to overall content size
        styles.put("width",  Integer.toString(total_width) +"px");
        styles.put("height", Integer.toString(total_height)+"px");
        styles.put("overflow", "hidden");
        
        buf_writer.flush();
        buf_writer.close();

        return buf.toString();
    }

    @Override
    protected void startHTML(final PrintWriter html, final int indent)
    {
        // Embedded content might resize the container, i.e. this widget.
        // So need to parse content first..
        embedded_html = parseContent();
        // .. and then start the HTML for this container, which might use the updated width, height
        super.startHTML(html, indent);
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        html.append(embedded_html);
    }
}
