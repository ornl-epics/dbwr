/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.rules;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.XMLUtil;
import dbwr.widgets.Widget;

/** Rule support
 *
 *  Example from a Rectangle widget:
 *  <pre>
 *  &lt;rule name="Color" prop_id="background_color" out_exp="false">
 *    &lt;exp bool_exp="pv0&gt;2">
 *      &lt;value>
 *        &lt;color name="OK" red="0" green="255" blue="0" />
 *      &lt;/value>
 *    &lt;/exp>
 *    &lt;pv_name>sim://ramp&lt;/pv_name>
 *  &lt;/rule>
 *  </pre>
 *
 *  Rectangle widget invokes RuleSupport to handle rules for "background_color".
 *  RuleSupport then finds the rule and creates JavaScript that's added to
 *  a '&lt;script>' tag at the end of the display HTML.
 */
public class RuleSupport
{
    private static final AtomicInteger id = new AtomicInteger();
    private final StringBuilder scripts = new StringBuilder();

    @FunctionalInterface
    private interface ValueParser
    {
        /** Parse value from a rule's '&lt;exp&gt;'
         *
         *  @param macros Macro provider, typically parent widget
         *  @param use_expression Use use '&lt;expression&gt;'? Otherwise use '&lt;value&gt;'
         *  @param exp XML '&lt;exp&gt;'
         *  @return Value to place in client-side javaScript
         *  @throws Exception on error
         */
        String parse(MacroProvider macros, boolean use_expression, Element exp) throws Exception;
    }

    /** Create client-side JavaScript for rule
     *  @param macros Macro provider, typically parent widget
     *  @param xml XML for this widget
     *  @param widget Currently handled widget
     *  @param property Property for which to convert rules
     *  @param value_parser Parser that turns rule expression into value for the property
     *  @param default_value Default value of property
     *  @param value_format Formatter for property's value
     *  @param update_code Javascript code to call to update the property with the rule-based value
     *  @throws Exception
     */
    private void handleRule(final MacroProvider macros, final Element xml,
                            final Widget widget, final String property,
                            final ValueParser value_parser,
                            final String default_value,
                            final Function<String, String> value_format,
                            final String update_code) throws Exception
    {
        try
        {
            final Element rules = XMLUtil.getChildElement(xml, "rules");
            if (rules == null)
                return;

            for (final Element re : XMLUtil.getChildElements(rules, "rule"))
            {
                if (! property.equals(re.getAttribute("prop_id")))
                    continue;

                final boolean use_exp = Boolean.parseBoolean(re.getAttribute("out_exp"));

                // Collect PVs,..
                final List<String> pvs = new ArrayList<>();
                for (final Element e : XMLUtil.getChildElements(re, "pv_name"))
                    pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));
                // Legacy PV names
                for (final Element e : XMLUtil.getChildElements(re, "pv"))
                    pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));

                // Expressions, values
                final List<String> expr = new ArrayList<>();
                final List<String> values = new ArrayList<>();
                for (final Element exp : XMLUtil.getChildElements(re, "exp"))
                {
                    // TODO Better expression check/convert
                    expr.add(convertExp(MacroUtil.expand(macros, exp.getAttribute("bool_exp"))));
                    values.add(value_parser.parse(macros, use_exp, exp));
                }

                // Created <script>:
                // let rule1 = new WidgetRule('w9180', 'property', ['sim://ramp', 'sim://sine' ]);
                // rule1.eval = function()
                // {
                //   let pv0 = this.value['sim://ramp'];
                //   let pv1 = this.value['sim://sine'];
                //   if (pv0>2) return 24;
                //   return 42;
                // }
                // rule1.update = set_svg_background_color
                final String rule = "rule" + id.incrementAndGet();

                // Create script for this rule
                final StringBuilder buf = new StringBuilder();
                buf.append("// Rule '").append(re.getAttribute("name")).append("'\n");
                buf.append("let " + rule +
                               " = new WidgetRule('" + widget.getWID() + "', '" + property + "', [" +
                               pvs.stream().map(pv -> "'" + pv + "'").collect(Collectors.joining(",")) +
                               "]);\n");
                buf.append(rule + ".eval = function()\n");
                buf.append("{\n");

                int N = pvs.size();
                for (int i=0; i<N; ++i)
                {
                    buf.append("  let pv" + i + " = this.value['" + pvs.get(i) + "'];\n");
                    buf.append("  let pvStr" + i + " = this.valueStr['" + pvs.get(i) + "'];\n");
                }

                N = expr.size();
                for (int i=0; i<N; ++i)
                    buf.append("  if (" + expr.get(i) + ") return " + value_format.apply(values.get(i)) + ";\n");
                buf.append("  return " + value_format.apply(default_value) + ";\n");

                buf.append("}\n");
                buf.append(rule + ".update = " + update_code + "\n");

                final String script = buf.toString();
                if (logger.isLoggable(Level.INFO))
                {
                    // Show XML for the rule, but skip the <?xml.. header and trim whitespace
                    final String rule_xml = Arrays.stream(XMLUtil.toString(re).split("\\n"))
                                                   .filter(line -> !line.startsWith("<?xml"))
                                                   .map(String::trim)
                                                   .collect(Collectors.joining("\n"));
                    logger.log(Level.INFO,
                               widget + " rule:\n" +
                               rule_xml +
                               "\n" +
                               script);
                }

                scripts.append(buf.toString());
            }
        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Error in rule:\n" + XMLUtil.toString(xml), ex);
        }
    }

    /** @param exp Jython expression from rule
     *  @return JavaScript expression
     */
    private String convertExp(final String exp)
    {
        // Instead of 'pvInt0' for integer value
        // use plain 'pv0' value.
        // Map python 'and', 'or', 'not' to Javascript
        return exp.replace("pvInt", "pv")
                  .replace("and", " && ")
                  .replace("or", " || ")
                  .replace("not", " ! ");
    }

    private static final ValueParser parse_string_value = (mac, use_expression, exp) ->
    {
        String value = null;
        if (use_expression)
        {   // Rules that use_expression should contain <expression>...
            value = XMLUtil.getChildString(mac, exp, "expression").orElse(null);
            if (value != null)
                return value;
            logger.log(Level.WARNING, "Rule should contain <expression>:\n" + XMLUtil.toString(exp.getParentNode()));
            // .. but older *.opi files might just have a constant <value> just as in the non-use_expression case
        }
        return XMLUtil.getChildString(mac, exp, "value").orElseThrow(() -> new Exception("Missing value"));
    };

    public void handleNumericRule(final MacroProvider macros, final Element xml,
            final Widget widget, final String property, final double default_value,
            final String update_code) throws Exception
    {
        handleRule(macros, xml, widget, property,
                   parse_string_value,
                   Double.toString(default_value),
                   text -> text, update_code);
    }

    private static final ValueParser parse_color_value = (mac, use_expression, exp) ->
    {
        if (use_expression)
            return XMLUtil.getColor(exp, "expression").orElseThrow(() -> new Exception("Missing expression for color"));
        else
            return XMLUtil.getColor(exp, "value").orElseThrow(() -> new Exception("Missing value for color"));
    };

    /** If widget has a rule for a color property, implement it
     *
     *  @param macros Parent widget that provides macros
     *  @param xml XML for this widget
     *  @param widget The widget
     *  @param property The color property for which to check
     *  @param default_color Default color that rule will use as fallback
     *  @param update_code Javascript function that rule will call with widget and color
     *  @throws Exception on error
     */
    public void handleColorRule(final MacroProvider macros, final Element xml,
                                final Widget widget, final String property, final String default_color,
                                final String update_code) throws Exception
    {
        handleRule(macros, xml, widget, property,
                   parse_color_value,
                   default_color,
                   color_text -> "'" + color_text + "'", update_code);
    }

    private static final ValueParser parse_boolean_value = (mac, use_expression, exp) ->
    {
        if (use_expression)
            return XMLUtil.getChildString(mac, exp, "expression").orElseThrow(() -> new Exception("Missing boolean expression"));
        else
            return Boolean.toString(XMLUtil.getChildBoolean(exp, "value").orElseThrow(() -> new Exception("Missing true/false value, got '" + exp + "'")));
    };

    /** Check if there is a rule for the visibility
     *
     *  @param macros Macro provider, usually the parent widget
     *  @param xml XML for this widget
     *  @param widget Widget where rule might need to be added
     *  @param default_visibility Original value for the visibility
     *  @throws Exception on error
     */
    public void handleVisibilityRule(final MacroProvider macros, final Element xml,
                                     final Widget widget, final boolean default_visibility) throws Exception
    {
        handleRule(macros, xml, widget, "visible",
                   parse_boolean_value,
                   Boolean.toString(default_visibility),
                   truefalse -> truefalse, "set_visibility");
    }

    public void addScripts(final PrintWriter html)
    {
        final String text = scripts.toString();
        if (text.trim().isEmpty())
            return;
        html.println("<script>");
        html.println(text);
        html.println("</script>");
    }
}

