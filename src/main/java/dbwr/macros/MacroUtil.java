/*******************************************************************************
 * Copyright (c) 2019-2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.macros;

import static dbwr.WebDisplayRepresentation.json_factory;
import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import dbwr.parser.XMLUtil;

/* Macro support */
public class MacroUtil
{
    // This is simplified from the macro support in the desktop version
    // TODO Update to use desktop version of macro support?
    
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

    // From Desktop 'Macros.MACRO_NAME_PATTERN'
    private static String MACRO_NAME_PATTERN = "[A-Za-z][A-Za-z0-9_.\\-\\[\\]]*";
    // Find NAME and DEFAULT in "$(NAME=DEFAULT)"
    private static Pattern MACRO_WITH_DEFAULT = Pattern.compile("\\$\\((" + MACRO_NAME_PATTERN + ")=([^)]+)\\)");

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
        
        // Expand unresolved macros that have default values
        Matcher mac_w_def = MACRO_WITH_DEFAULT.matcher(result);
        while (mac_w_def.find())
        {
            final String expression = mac_w_def.group(0);
            final String name = mac_w_def.group(1);
            final String def_val = mac_w_def.group(2);
            final String value = macros.getMacroValue(name);
            result =result.replace(expression, value != null ? value : def_val);
            mac_w_def = MACRO_WITH_DEFAULT.matcher(result);
        }

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

    // Demo
    public static void main(String[] args)
    {
        final Map<String, String> values = new HashMap<>();
        values.put("S", "System");
        values.put("N", "2");
        final MacroProvider macros = MacroProvider.forMap(values);

        System.out.println(expand(macros, "$(S):Motor$(N)"));
        System.out.println(expand(macros, "$(TAB=7)"));
        System.out.println(expand(macros, "$(S):Motor$(N=99)"));
        System.out.println(expand(macros, "$(S):Motor$(N=99) on tab $(TAB=7)"));
    }
}
