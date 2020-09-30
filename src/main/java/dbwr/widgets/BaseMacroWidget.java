/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;

/** Base for widget with 'macros'
 *  @author Kay Kasemir
 */
abstract public class BaseMacroWidget extends Widget
{
    private final Map<String, String> macros;

    public BaseMacroWidget(final ParentWidget parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
    {
        super(parent, xml, type, default_width, default_height);

        macros = MacroUtil.fromXML(xml);
        MacroUtil.expand(parent, macros);
    }

    public BaseMacroWidget(final ParentWidget parent, final Element xml, final String type) throws Exception
    {
        super(parent, xml, type);

        macros = MacroUtil.fromXML(xml);
        MacroUtil.expand(parent, macros);
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
}
