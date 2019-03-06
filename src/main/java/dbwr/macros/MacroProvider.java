/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.macros;

import java.util.Collection;

public interface MacroProvider
{
    public Collection<String> getMacroNames();

    public String getMacroValue(String name);
}
