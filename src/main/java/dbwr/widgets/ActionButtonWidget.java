/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.Map;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.XMLUtil;

public class ActionButtonWidget extends Widget
{
	private String text;

	public ActionButtonWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "action_button");

		// classes.add("Debug");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		XMLUtil.getColor(xml, "foreground_color").ifPresent(color -> styles.put("color", color));

		final Element el = XMLUtil.getChildElement(xml, "actions");
		if (el != null)
		{
			final int index = 0;
			for (final Element ae : XMLUtil.getChildElements(el, "action"))
			{
			    // Always show description, no matter if open_display, write_pv, ...
			    final String desc = XMLUtil.getChildString(parent, ae, "description").orElse("");
			    attributes.put("data-linked-label-" + index, HTMLUtil.escape(desc));

			    final String action_type = ae.getAttribute("type");
			    if ("open_webpage".equals(action_type))
			    {
			        final String url = XMLUtil.getChildString(parent, ae, "url").orElse("");
                    attributes.put("data-linked-url-" + index, HTMLUtil.escape(url));
			    }
			    else if ("open_display".equalsIgnoreCase(action_type))
			    {
    				final String file = XMLUtil.getChildString(parent, ae, "file").orElse(XMLUtil.getChildString(parent, ae, "path").orElse(null));
    				if (file == null)
    					continue;

    				// Read macros into map
    				final Map<String, String> macros = MacroUtil.fromXML(ae);
    				MacroUtil.expand(parent, macros);

    		        // Add parent macros
    				for (final String name : parent.getMacroNames())
    		            macros.putIfAbsent(name, parent.getMacroValue(name));

    				// TODO Escape file
    				final String resolved = Resolver.resolve(this, file);
    				attributes.put("data-linked-file-" + index, resolved);
    				if (! macros.isEmpty())
                        attributes.put("data-linked-macros-" + index, HTMLUtil.escape(MacroUtil.toJSON(macros)));
			    }
			}
		}

		text = XMLUtil.getChildString(parent, xml, "text").orElse("$(actions)");

		// TODO Handle $(actions) as text
		if (text.equals("$(actions)"))
		    if (attributes.containsKey("data-linked-label-0"))
		        text = HTMLUtil.unescape(attributes.get("data-linked-label-0"));
		    else
		        text = "Button";
	}

	@Override
	protected String getHTMLElement()
	{
	    return "button";
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		html.append(HTMLUtil.escape(text));
	}
}
