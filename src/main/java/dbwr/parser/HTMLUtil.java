/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import java.io.PrintWriter;

import org.apache.commons.text.StringEscapeUtils;

public class HTMLUtil
{
	public static String escape(final String text)
	{
		return StringEscapeUtils.escapeHtml4(text);
	}

	public static void escape(final PrintWriter html, final String text)
	{
		html.append(StringEscapeUtils.escapeHtml4(text));
	}

	public static String unescape(final String text)
    {
	    return StringEscapeUtils.unescapeHtml4(text);
    }

	public static void indent(final PrintWriter html, final int indent)
	{
		for (int i=0; i<indent; ++i)
			html.append("  ");
	}
}
