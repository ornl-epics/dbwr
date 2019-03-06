/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.macros;

import java.util.Collection;
import java.util.Map;

/** Provider of macro values
 *  @author Kay Kasemir
 */
public interface MacroProvider
{
    /** @return Names of known macros */
    public Collection<String> getMacroNames();

    /** @param name Macro name
     *  @return Value of the macro
     */
    public String getMacroValue(String name);

    /** @param map Name/Value map
     *  @return {@link MacroProvider} for the map
     */
    public static MacroProvider forMap(final Map<String, String> map)
    {
        return new MacroProvider()
        {
            @Override
            public Collection<String> getMacroNames()
            {
                return map.keySet();
            }

            @Override
            public String getMacroValue(final String name)
            {
                return map.get(name);
            }
        };
    }
}
