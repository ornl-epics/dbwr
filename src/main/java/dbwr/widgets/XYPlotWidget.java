/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Element;

public class XYPlotWidget extends Widget
{
    private static final AtomicInteger id = new AtomicInteger();

    public XYPlotWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "xyplot", 400, 300);

		// Flot lib needs an ID to place plot
		attributes.put("id", "plot" + id.incrementAndGet());

		// TODO get primary value PV
		final String pv_name = "sim://sinewave";
		attributes.put("data-pv", pv_name);
	}
}
