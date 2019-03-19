/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.parser.DisplayParser;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;

/** Servlet that fetches HTML for a display
 *
 *  <p>On error, returns a 'pre' with id 'error'
 *  @author Kay Kasemir
 */
@WebServlet("/screen")
public class ScreenServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final String display_name = request.getParameter("display");
		logger.log(Level.INFO, "screen/display=" + display_name);

		Map<String, String> macro_map = Collections.emptyMap();
		String json_macros = request.getParameter("macros");
		if (json_macros != null)
		{
            json_macros = HTMLUtil.unescape(request.getParameter("macros"));
		    macro_map = MacroUtil.fromJSON(json_macros);
            logger.log(Level.INFO, ".. and macros " + macro_map);
		}

		if (display_name == null)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'display'");
			return;
		}

		try
		{
		    final Resolver display = new Resolver(display_name);
			final ByteArrayOutputStream html_buf = new ByteArrayOutputStream();
			final PrintWriter html = new PrintWriter(html_buf);
			new DisplayParser(display, MacroProvider.forMap(macro_map), html);
			html.flush();
			html.close();

			response.setContentType("text/html");
			final PrintWriter writer = response.getWriter();
			writer.append(html_buf.toString());
		}
		catch (final Exception ex)
		{
			logger.log(Level.WARNING, "Cannot read " + display_name, ex);

			response.setContentType("text/html");
            final PrintWriter writer = response.getWriter();
            writer.append("<pre id=\"error\">\n");
            writer.append(ex.toString());
            writer.append("\n</pre>\n");
		}
	}
}
