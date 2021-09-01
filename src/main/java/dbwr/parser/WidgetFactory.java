/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.widgets.ParentWidget;
import dbwr.widgets.UnknownWidget;
import dbwr.widgets.Widget;

/** Factory for creating widget based on XML
 *
 *  <p>Creates widget class for each type based on
 *  mapping defined in <file>widget.properties</file>.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("unchecked")
public class WidgetFactory
{
    /** Mapping of legacy BOY widget types to current widget types */
    private static final Map<String, String> BOY_TYPES = new HashMap<>();

    /** Map of widget type like 'label' to {@link Widget} class */
    private static final Map<String, Class<Widget>> widget_classes = new HashMap<>();

    /** Javascript files that are used by the widgets */
    public static final List<String> js = new ArrayList<>();

    /** Stylesheet files that are used by the widgets */
    public static final List<String> css = new ArrayList<>();

    static
    {
        // Load *Widget classes from /widget.properties
        try
        {
            final Properties wp = new Properties();
            wp.load(WidgetFactory.class.getResourceAsStream("/widget.properties"));
            for (final Object type : wp.keySet())
            {
                final String typename = type.toString().trim();
                final String clazz = wp.getProperty(typename);
                widget_classes.put(typename, (Class<Widget>)Class.forName(clazz));
            }
        }
        catch (final Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot load widget info", ex);
        }

        for (final Map.Entry<String, Class<Widget>> entry : widget_classes.entrySet())
            logger.log(Level.CONFIG, entry.getKey() + " - " + entry.getValue());
    }

    /** Register a JavaScript file to be included by the generated web page.
     *  @param script JavaScript file to add
     */
    public static void addJavaScript(final String script)
    {
        js.add(script);
    }

    /** Register a stype sheet to be included in the generated web page
     *  @param stylesheet CSS file to add
     */
    public static void addCSS(final String stylesheet)
    {
        css.add(stylesheet);
    }

    /** Register a legacy widget type
     *  
     *  Called by widgets which implement not just a current 'type'
     *  but also a different 'legacy' type.
     *  
     *  @param legacy 'BOY' widget type
     *  @param type Current widget type
     */
    public static void registerLegacy(final String legacy, final String type)
    {
        BOY_TYPES.put(legacy, type);
    }

    /** @param parent Parent widget
     *  @param xml XML for this widget
     *  @return {@link Widget}
     *  @throws Exception on error
     */
    public static Widget createWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        String type = xml.getAttribute("type");
        if (type.isEmpty())
            type = BOY_TYPES.get(xml.getAttribute("typeId"));
        if (type == null)
            type = xml.getAttribute("typeId");

        if (type == null  ||  type.isEmpty())
            throw new Exception("Cannot determine widget type for " + XMLUtil.toString(xml));

        final Class<Widget> clazz = widget_classes.get(type);
        if (clazz == null)
            return new UnknownWidget(parent, xml, type);

        final Constructor<Widget> constructor = clazz.getDeclaredConstructor(ParentWidget.class, Element.class);
        return constructor.newInstance(parent, xml);
    }
}
