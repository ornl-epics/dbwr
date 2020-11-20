/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import org.w3c.dom.Element;

public class FileSelectorWidget extends Widget
{
    // Selecting a file in the web client doesn't fully make sense.
    // Even if there's write access to the PV,
    // the file name would not be useful on the remote system.
    //
    // This implementation simply suppresses the "{fileselector}" placeholder
    // that would be shown if this wasn't implemented.
    public FileSelectorWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "fileselector");

        // classes.add("Debug");
    }
}
