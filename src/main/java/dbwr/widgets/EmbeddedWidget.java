/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.parser.DisplayParser;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Embedded display widget
 *
 *  <p>Loads the content of embedded display
 *  and prints it into the HTML.
 *
 *  @author Kay Kasemir
 */
public class EmbeddedWidget extends BaseMacroWidget
{
    static
    {
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.linkingContainer", "embedded");
    }

	private String file;
    private final String group_name;
	private final int resize;
	private String embedded_html;

	public EmbeddedWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "embedded", 300, 200);
		// classes.add("Debug");

		file = XMLUtil.getChildString(this, xml, "file").orElse("");
		if (file.isEmpty())
		    file = XMLUtil.getChildString(this, xml, "opi_file").orElse("");

		group_name = XMLUtil.getChildString(this, xml, "group_name").orElse(null);

		resize = XMLUtil.getChildInteger(xml, "resize").orElse(0);
	}

    private String parseContent()
    {
        // System.out.println(file + " with " + macros);
        if (file.isEmpty())
            return "";

        final DisplayParser embedded_display;
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try
        {
            final String resolved = Resolver.resolve(this, file);
            final Resolver display = new Resolver(resolved);
            final PrintWriter buf_writer = new PrintWriter(buf);
            embedded_display = new DisplayParser(display, this, buf_writer, group_name);
            buf_writer.flush();
            buf_writer.close();
        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Cannot read embedded display " + file, ex);
            classes.add("Error");
            classes.add("BorderDisconnected");
            return "Cannot embed '" + HTMLUtil.escape(file) + "'";
        }

        // 0: No resize, scrollbars as needed
        // 1: Resize content
        // 2: Resize container
        // 3: Stretch content separately in X and Y
        if (resize == 1)
        {
            // System.out.println("Resize content " + file + " from " + embedded_display.width + " x " + embedded_display.height + " to " + width + " x " + height);
            final double zoom_x = embedded_display.width > 0 ? (double)width / embedded_display.width : 1.0;
            final double zoom_y = embedded_display.height > 0 ? (double)height / embedded_display.height : 1.0;
            final double zoom = Math.min(zoom_x, zoom_y);

            styles.put("transform-origin", "left top");
            styles.put("transform", "scale(" + zoom + ")");
        }
        else if (resize == 2)
        {
            // System.out.println("Resize container " + width + " x " + height + " to fit " + file + " " + embedded_display.width + " x " + embedded_display.height);
            // Keep 'final' width, height unchanged, but update the style settings
            styles.put("width",  Integer.toString(embedded_display.width) +"px");
            styles.put("height", Integer.toString(embedded_display.height)+"px");
            styles.put("overflow", "hidden");
        }
        else if (resize == 3)
        {
            // System.out.println("Stretch content " + file + " from " + embedded_display.width + " x " + embedded_display.height + " to " + width + " x " + height);
            final double zoom_x = embedded_display.width > 0 ? (double)width / embedded_display.width : 1.0;
            final double zoom_y = embedded_display.height > 0 ? (double)height / embedded_display.height : 1.0;

            styles.put("transform-origin", "left top");
            styles.put("transform", "scale(" + zoom_x + ", " + zoom_y + ")");
        }
        else // resize == 0 or a new option that's ignored
        {
            // System.out.println("Auto-Scroll " + file + " to fit " + embedded_display.width + " x " + embedded_display.height + " into " + width + " x " + height);
            // Enable scrollbars based on self-declared size of container vs. content
            if (embedded_display.width > width  ||
                    embedded_display.height > height)
                styles.put("overflow", "scroll");
            else
                styles.put("overflow", "hidden");
        }

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
