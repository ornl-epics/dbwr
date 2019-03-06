/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.DisplayParser;
import dbwr.parser.XMLUtil;

public class EmbeddedWidget extends Widget
{
	private final Map<String, String> macros;
	private String file;

	public EmbeddedWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "embedded", 300, 200);

		// Get macros first in case they're used for the name etc.
		macros = MacroUtil.fromXML(xml);
		classes.add("Debug");

		file = XMLUtil.getChildString(this, xml, "file").orElse("");
		if (file.isEmpty())
		    file = XMLUtil.getChildString(this, xml, "opi_file").orElse("");
	}

	@Override
    public Collection<String> getMacroNames()
	{
	    final List<String> names = new ArrayList<>(super.getMacroNames());
	    names.addAll(macros.keySet());
        return names;
    }

    @Override
    public String getMacroValue(final String name)
    {
        final String result = macros.get(name);
        if (result != null)
            return result;
        return super.getMacroValue(name);
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        System.out.println(file + " with " + macros);

        if (file.isEmpty())
            return;

        try
        {
            // TODO Resolve file
            final String resolved = "file:/Users/ky9/Downloads/Display%20Builder/embedded/" + file;

            final InputStream stream = DisplayParser.open(resolved);
            new DisplayParser(stream, macros, html);
            html.flush();
        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Cannot read embedded display " + file, ex);
        }
	}
}
