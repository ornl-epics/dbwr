/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr;

import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonFactory;

public class WebDisplayRepresentation
{
    /** Common logger */
	public static final Logger logger = Logger.getLogger(WebDisplayRepresentation.class.getPackage().getName());

	/** Common JSON factory */
	public static final JsonFactory json_factory = new JsonFactory();
}
