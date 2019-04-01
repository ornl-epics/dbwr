/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.net.URL;

import dbwr.macros.MacroProvider;
import dbwr.rules.RuleSupport;

/** Base for all widgets
 *  @author Kay Kasemir
 */
public interface ParentWidget extends MacroProvider
{
    public URL getDisplay();

    public RuleSupport getRuleSupport();
}

