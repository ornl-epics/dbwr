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

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.XMLUtil;
import dbwr.widgets.Widget;

/** Rule support
 *
 *  Assume this in a Rectangle widget:
 *
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
 *  Rectangle widget creates RuleSupport with handler for color "background_color".
 *  RuleSupport then finds the rule and creates JavaScript that's added to
 *  a '&lt;script>' tag at the end of the display HTML.
 *
 *  TODO Rules need to be started _AFTER_ connecting to web socket,
 *  then they subscribe to PVs etc.
 */
public class RuleSupport
{
    private final StringBuilder scripts = new StringBuilder();

    public void handleColorRule(final MacroProvider macros, final Element xml, final Widget widget, final String property, final String default_color)
    {
        final Element rules = XMLUtil.getChildElement(xml, "rules");
        if (rules == null)
            return;

        for (final Element re : XMLUtil.getChildElements(rules, "rule"))
        {
            if (! property.equals(re.getAttribute("prop_id")))
                continue;

            // Collect PVs
            final List<String> pvs = new ArrayList<>();
            for (final Element e : XMLUtil.getChildElements(re, "pv_name"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));
            // Legacy PV names
            for (final Element e : XMLUtil.getChildElements(re, "pv"))
                pvs.add(MacroUtil.expand(macros, XMLUtil.getString(e)));

            scripts.append("console.log('RULE FOR COLOR " + property + " based on " + pvs + "');");
        }
    }

    public void addScripts(PrintWriter html)
    {
        html.println("<script>");

        html.println("jQuery(() =>");
        html.println("{");
        html.println(scripts.toString());
        html.println("});");
        html.println("</script>");
    }
}
