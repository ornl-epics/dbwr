/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.rules;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AtomicInteger id = new AtomicInteger();
    private final StringBuilder scripts = new StringBuilder();


    public void handleNumericRule(final MacroProvider macros, final Element xml,
                                  final Widget widget, final String property, final double default_value,
                                  final String update_code) throws Exception
    {
        final Element rules = XMLUtil.getChildElement(xml, "rules");
        if (rules == null)
            return;

        for (final Element re : XMLUtil.getChildElements(rules, "rule"))
        {
            if (! property.equals(re.getAttribute("prop_id")))
                continue;

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
            for (final Element e : XMLUtil.getChildElements(re, "exp"))
            {
                // TODO Check/convert expression
                expr.add(MacroUtil.expand(macros, e.getAttribute("bool_exp")));
                values.add(XMLUtil.getChildString(macros, e, "value").orElseThrow(() -> new Exception("Missing value")));
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
            scripts.append("let " + rule +
                           " = new WidgetRule('" + widget.getWID() + "', '" + property + "', [" +
                           pvs.stream().map(pv -> "'" + pv + "'").collect(Collectors.joining(",")) +
                           "]);\n");
            scripts.append(rule + ".eval = function()\n");
            scripts.append("{\n");
            int N = pvs.size();
            for (int i=0; i<N; ++i)
                scripts.append("  let pv" + i + " = this.value['" + pvs.get(i) + "'];\n");

            N = expr.size();
            for (int i=0; i<N; ++i)
                scripts.append("  if (" + expr.get(i) + ") return " + values.get(i) + ";\n");
            scripts.append("  return " + default_value + ";\n");

            scripts.append("}\n");
            scripts.append(rule + ".update = " + update_code + "\n");
        }
    }


    public void handleColorRule(final MacroProvider macros, final Element xml,
                                final Widget widget, final String property, final String default_color,
                                final String update_code) throws Exception
    {
        final Element rules = XMLUtil.getChildElement(xml, "rules");
        if (rules == null)
            return;

        for (final Element re : XMLUtil.getChildElements(rules, "rule"))
        {
            if (! property.equals(re.getAttribute("prop_id")))
                continue;

            // Collect PVs,..
            final List<String> pvs = new ArrayList<>();
            for (final Element e : XMLUtil.getChildElements(re, "pv_name"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));
            // Legacy PV names
            for (final Element e : XMLUtil.getChildElements(re, "pv"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));

            // Expressions, values
            final List<String> expr = new ArrayList<>();
            final List<String> colors = new ArrayList<>();
            for (final Element e : XMLUtil.getChildElements(re, "exp"))
            {
                // TODO Check/convert expression
                expr.add(e.getAttribute("bool_exp"));
                colors.add(XMLUtil.getColor(e, "value").orElseThrow(() -> new Exception("Missing color")));
            }

            // Created <script>:
            // // Rule for color of background_color
            // let rule1 = new WidgetRule('w9180', 'background_color', ['sim://ramp']);
            // rule1.eval = function()
            // {
            //   let pv0 = this.value['sim://ramp'];
            //   if (pv0>2) return '#00FF00';
            //   return '#1E90FF';
            // }
            // rule1.update = set_svg_background_color
            final String rule = "rule" + id.incrementAndGet();
            scripts.append("// Rule for color of "  + property + "\n");
            scripts.append("let " + rule +
                           " = new WidgetRule('" + widget.getWID() + "', '" + property + "', [" +
                           pvs.stream().map(pv -> "'" + pv + "'").collect(Collectors.joining(",")) +
                           "]);\n");
            scripts.append(rule + ".eval = function()\n");
            scripts.append("{\n");
            int N = pvs.size();
            for (int i=0; i<N; ++i)
                scripts.append("  let pv" + i + " = this.value['" + pvs.get(i) + "'];\n");

            N = expr.size();
            for (int i=0; i<N; ++i)
                scripts.append("  if (" + expr.get(i) + ") return '" + colors.get(i) + "';\n");
            scripts.append("  return '" + default_color + "';\n");

            scripts.append("}\n");
            scripts.append(rule + ".update = " + update_code + "\n");
        }
    }

    public void handleVisibilityRule(final MacroProvider macros, final Element xml,
                                     final Widget widget, final boolean default_visibility) throws Exception
    {
        final String property = "visible";
        final String update_code = "set_visibility";

        final Element rules = XMLUtil.getChildElement(xml, "rules");
        if (rules == null)
            return;

        for (final Element re : XMLUtil.getChildElements(rules, "rule"))
        {
            if (! property.equals(re.getAttribute("prop_id")))
                continue;

            // Collect PVs,..
            final List<String> pvs = new ArrayList<>();
            for (final Element e : XMLUtil.getChildElements(re, "pv_name"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));
            // Legacy PV names
            for (final Element e : XMLUtil.getChildElements(re, "pv"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));

            // Expressions, values
            final List<String> expr = new ArrayList<>();
            final List<Boolean> values = new ArrayList<>();
            for (final Element e : XMLUtil.getChildElements(re, "exp"))
            {
                // TODO Check/convert expression
                expr.add(e.getAttribute("bool_exp"));
                values.add(XMLUtil.getChildBoolean(e, "value").orElseThrow(() -> new Exception("Missing true/false value")));
            }

            // Created <script>:
            // // Rule for visible
            // let rule1 = new WidgetRule('w9180', 'visible', ['sim://ramp']);
            // rule1.eval = function()
            // {
            //   let pv0 = this.value['sim://ramp'];
            //   if (pv0>2) return true;
            //   return false;
            // }
            // rule1.update = set_svg_background_color
            final String rule = "rule" + id.incrementAndGet();
            scripts.append("// Rule for "  + property + "\n");
            scripts.append("let " + rule +
                           " = new WidgetRule('" + widget.getWID() + "', '" + property + "', [" +
                           pvs.stream().map(pv -> "'" + pv + "'").collect(Collectors.joining(",")) +
                           "]);\n");
            scripts.append(rule + ".eval = function()\n");
            scripts.append("{\n");
            int N = pvs.size();
            for (int i=0; i<N; ++i)
                scripts.append("  let pv" + i + " = this.value['" + pvs.get(i) + "'];\n");

            N = expr.size();
            for (int i=0; i<N; ++i)
                scripts.append("  if (" + expr.get(i) + ") return " + values.get(i) + ";\n");
            scripts.append("  return " + default_visibility + ";\n");

            scripts.append("}\n");
            scripts.append(rule + ".update = " + update_code + "\n");
        }
    }


    public void addScripts(final PrintWriter html)
    {
        html.println("<script>");
        html.println(scripts.toString());
        html.println("</script>");
    }
}
