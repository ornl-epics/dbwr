/*******************************************************************************
 * Copyright (c) 2019-2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

public class DataBrowserWidget extends Widget
{
    /** Number of live data samples kept in ring buffer */
    static final int DEFAULT_PLOT_RING_SIZE;

    static
    {
        WidgetFactory.registerLegacy("org.csstudio.trends.databrowser.opiwidget", "databrowser");
        WidgetFactory.addJavaScript("databrowser.js");
        WidgetFactory.addCSS("databrowser.css");

        DEFAULT_PLOT_RING_SIZE = Integer.parseInt(System.getenv().getOrDefault("DEFAULT_PLOT_RING_SIZE", "5000"));
        logger.log(Level.INFO, "DEFAULT_PLOT_RING_SIZE = " + DEFAULT_PLOT_RING_SIZE);
    }

    /** Decode start..end time
     *  @param start_spec "-52 sec", minutes, hours, days with a little flexibility
     *  @param end_spec Expected to be "end"
     *  @return time span of plot in seconds
     */
    public static long decodeTimespan(final String start_spec, final String end_spec)
    {
        logger.log(Level.FINE, "Data browser time range from '" + start_spec + "' to '" + end_spec + "'");
        if (!"now".equalsIgnoreCase(end_spec))
        {
            logger.log(Level.WARNING, "Cannot decode data browser time range from '" + start_spec + "' to '" + end_spec + "'");
            return 5*60;
        }

        Pattern p = Pattern.compile("\\s*-?([0-9.]+)\\s*day.*");
        Matcher matcher = p.matcher(start_spec);
        if (matcher.matches())
            return Math.round(Double.parseDouble(matcher.group(1)) * 24*60*60);

        p = Pattern.compile("\\s*-?([0-9.]+)\\s*hour.*");
        matcher = p.matcher(start_spec);
        if (matcher.matches())
            return Math.round(Double.parseDouble(matcher.group(1)) * 60*60);

        p = Pattern.compile("\\s*-?([0-9.]+)\\s*min.*");
        matcher = p.matcher(start_spec);
        if (matcher.matches())
            return Math.round(Double.parseDouble(matcher.group(1)) * 60);

        p = Pattern.compile("\\s*-?([0-9.]+)\\s*sec.*");
        matcher = p.matcher(start_spec);
        if (matcher.matches())
            return Math.round(Double.parseDouble(matcher.group(1)));

        logger.log(Level.WARNING, "Cannot parse data browser time range from '" + start_spec + "' to '" + end_spec + "'");
        return -1;
    }

	public DataBrowserWidget(final ParentWidget parent, final Element xml) throws Exception
	{
	    super(parent, xml, "databrowser", 400, 300);

        // Get *.plt file, falling back to legacy property
        String file = XMLUtil.getChildString(parent, xml, "file").orElse(null);
        if (file == null)
            file = XMLUtil.getChildString(parent, xml, "filename").orElse(null);
        if (file == null)
        {
            logger.log(Level.WARNING, "Data browser widget without file");
            return;
        }

        // Parse the *.plt file
        final String resolved = Resolver.resolve(this, file);
        final URL url = new URL(resolved);
        final Element plt = XMLUtil.openXMLDocument(url.openStream(), "databrowser");

        final String start_spec = XMLUtil.getChildString(parent, plt, "start").orElse("-60 sec").toLowerCase();
        final String end_spec = XMLUtil.getChildString(parent, plt, "end").orElse("now").toLowerCase();
        attributes.put("data-timespan", Long.toString(decodeTimespan(start_spec, end_spec)));
        attributes.put("data-autospan", "false");

        final Element pvlist = XMLUtil.getChildElement(plt, "pvlist");
        int i = 0;
        if (pvlist != null)
            for (final Element pv : XMLUtil.getChildElements(pvlist, "pv"))
            {
                final String pv_name = XMLUtil.getChildString(parent, pv, "name").orElse(null);
                if (pv_name == null)
                    continue;

                attributes.put("data-pv" + i, pv_name);

                final String label = XMLUtil.getChildString(parent, pv, "display_name").orElse(null);
                if (label != null)
                    attributes.put("data-label" + i, label);
                attributes.put("data-linewidth" + i, XMLUtil.getChildInteger(pv, "linewidth").orElse(1).toString());
                attributes.put("data-color" + i, getColor(pv, "color").orElse(Integer.toString(i)));
                attributes.put("data-ringsize" + i, XMLUtil.getChildInteger(pv, "ring_size").orElse(DEFAULT_PLOT_RING_SIZE).toString());
                ++i;
            }
	}

    private Optional<String> getColor(Element pv, String tag) throws Exception
    {
        final Element color = XMLUtil.getChildElement(pv, tag);
        if (color != null)
        {
            final int red = XMLUtil.getChildInteger(color, "red").orElse(0);
            final int green = XMLUtil.getChildInteger(color, "green").orElse(0);
            final int blue = XMLUtil.getChildInteger(color, "blue").orElse(0);
            return Optional.of(String.format("rgb(%d,%d,%d)", red, green, blue));
        }
        return Optional.empty();
    }
}
