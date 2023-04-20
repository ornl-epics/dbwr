/*******************************************************************************
 * Copyright (c) 2019-2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    /** @param primary Primary macros
     *  @param fallback Secondary macros
     *  @return Combined macros
     */
    public static MacroProvider combine(final MacroProvider primary, final MacroProvider fallback)
    {
        final List<String> names = new ArrayList<>(fallback.getMacroNames());
        names.addAll(primary.getMacroNames());
        
        return new MacroProvider()
        {
            @Override
            public Collection<String> getMacroNames()
            {
                return names;
            }

            @Override
            public String getMacroValue(final String name)
            {
                String value = primary.getMacroValue(name);
                if (value == null)
                    value = fallback.getMacroValue(name);
                return value;
            }
        };
    }
}
