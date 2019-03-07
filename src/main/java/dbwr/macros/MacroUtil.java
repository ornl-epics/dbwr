/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.macros;

import static dbwr.WebDisplayRepresentation.json_factory;
import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import dbwr.parser.XMLUtil;

public class MacroUtil
{
    /** @param xml XML that contains '&lt;macros>'
     *  @return Name, value map of macros. May be empty.
     */
    public static Map<String, String> fromXML(final Element xml)
    {
        final Map<String, String> macros = new LinkedHashMap<>();
        final Element me = XMLUtil.getChildElement(xml, "macros");
        if (me != null)
            for (final Element mv : XMLUtil.getChildElements(me))
                macros.put(mv.getNodeName(), XMLUtil.getString(mv));
        return macros;
    }


    /** @param json JSON that contains macros
     *  @return Name, value map of macros. May be empty.
     */
    public static Map<String, String> fromJSON(final String json)
    {
        // logger.log(Level.INFO, "Parsing macros from " + json);
        final Map<String, String> macros = new LinkedHashMap<>();
        try
        (
            final JsonParser jp = json_factory.createParser(json);
        )
        {
            if (jp.nextToken() == JsonToken.START_OBJECT)
            {
                while (jp.nextToken() != JsonToken.END_OBJECT)
                {
                    final String name = jp.getCurrentName();
                    final String value = jp.getText();
                    macros.put(name, value);
                }
            }

        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Cannot parse macros from " + json);
        }
        return macros;
    }


    /** @param macros Name, value map of macros
     *  @return JSON for the macros
     *  @throws Exception on error
     */
    public static String toJSON(final Map<String, String> macros) throws Exception
    {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try
        (
            final JsonGenerator g = json_factory.createGenerator(buf);
        )
        {
            g.writeStartObject();
            for (final Map.Entry<String, String> macro : macros.entrySet())
                g.writeStringField(macro.getKey(), macro.getValue());
            g.writeEndObject();

            g.flush();
        }
        return buf.toString();
    }


    /** @param macros Macros
     *  @param text Text that might contain macro references
     *  @return Expanded text
     */
    public static String expand(final MacroProvider macros, final String text)
    {
        String result = text;
        int recursions = 3;
        while (result.contains("$(")  &&  --recursions > 0)
            for (final String name : macros.getMacroNames())
                result = result.replace("$(" + name + ")", macros.getMacroValue(name));

        // Also expand the less frequently used ${M} syntax
        recursions = 3;
        while (result.contains("${")  &&  --recursions > 0)
            for (final String name : macros.getMacroNames())
                result = result.replace("${" + name + "}", macros.getMacroValue(name));

        return result;
    }

    /** Expand all macros in map with values from parent
     *  @param parent Parent macros
     *  @param macros Macros that will be expanded
     */
    public static void expand(MacroProvider parent, Map<String, String> macros)
    {
        macros.replaceAll((name, value) ->  expand(parent, value));
    }
}
