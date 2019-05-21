/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;

import java.util.Map;
import java.util.Objects;

/** Display with macros
 *  @author Kay Kasemir
 */
public class DisplayKey
{
    private final String display;
    private final Map<String, String> macros;

    public DisplayKey(final String display, final Map<String, String> macros)
    {
        this.display = display;
        this.macros = macros;
    }

    public String getDisplay()
    {
        return display;
    }

    public Map<String, String> getMacros()
    {
        return macros;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(display, macros);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DisplayKey))
            return false;
        final DisplayKey other = (DisplayKey) obj;
        return display.equals(other.display) &&
                macros.equals(other.macros);
    }

    @Override
    public String toString()
    {
        return display + " " + macros;
    }
}
