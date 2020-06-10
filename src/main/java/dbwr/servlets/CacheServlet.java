/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.servlets;

import static dbwr.WebDisplayRepresentation.json_factory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

/** Servlet that fetches cache info
 *  @author Kay Kasemir
 */
@WebServlet("/cache")
public class CacheServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final JsonGenerator g = json_factory.createGenerator(buf);
        g.writeStartObject();
        g.writeArrayFieldStart("displays");
        for (final SoftReference<CachedDisplay> ref : DisplayCache.getEntries())
        {
            final CachedDisplay cached = ref.get();
            if (cached == null)
                continue;

            g.writeStartObject();
            g.writeStringField("display", cached.getDisplay());

            g.writeFieldName("macros");
            g.writeStartObject();
            for (final Map.Entry<String, String> entry : cached.getMacros().entrySet())
                g.writeStringField(entry.getKey(), entry.getValue());
            g.writeEndObject();

            g.writeNumberField("created", cached.getCreated().toEpochMilli());
            g.writeNumberField("stamp", cached.getTimestamp().toEpochMilli());
            g.writeNumberField("calls", cached.getCalls());
            g.writeNumberField("size", cached.getHTML().length());
            g.writeNumberField("ms", cached.getMillisec());
            g.writeEndObject();
        }

        g.writeEndArray();
        g.writeEndObject();
        g.flush();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final PrintWriter writer = response.getWriter();
        writer.append(buf.toString());
	}

	@Override
	protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
	    DisplayCache.clear();
	}
}
