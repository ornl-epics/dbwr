package dbwr.servlets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbwr.macros.MacroUtil;
import dbwr.parser.CertificateHandler;
import dbwr.parser.DisplayParser;
import dbwr.parser.HTMLUtil;

/** Servlet that fetches HTML for a display
 *  @author Kay Kasemir
 */
@WebServlet("/screen")
public class ScreenServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	static
    {
        try
        {
            CertificateHandler.trustAnybody();
        }
        catch (final Exception ex)
        {
            logger.log(Level.WARNING, "Cannot install certificate handler", ex);
        }
    }

	private InputStream open(final String display_name) throws Exception
	{
	    try
	    {
	        // Try to 'upgrade' to *.bob file
    	    if (display_name.contains(".opi"))
            {
                final URL url = new URL(display_name.replace(".opi", ".bob"));
                final InputStream stream = url.openStream();
                logger.log(Level.INFO, "Opening *.bob instead of " + display_name);
                return stream;
            }
	    }
	    catch (final Exception ex)
	    {
	        // Ignore error from *.bob attempts
	    }

	    final URL url = new URL(display_name);
        return url.openStream();
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final String display_name = request.getParameter("display");
		logger.log(Level.INFO, "screen/display=" + display_name);

		Map<String, String> macros = Collections.emptyMap();
		String json_macros = request.getParameter("macros");
		if (json_macros != null)
		{
            json_macros = HTMLUtil.unescape(request.getParameter("macros"));
		    macros = MacroUtil.fromJSON(json_macros);
            logger.log(Level.INFO, ".. and macros " + macros);
		}

		if (display_name == null)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'display'");
			return;
		}

		try
		{
		    final InputStream stream = open(display_name);
			final ByteArrayOutputStream html_buf = new ByteArrayOutputStream();
			final PrintWriter html = new PrintWriter(html_buf);
			new DisplayParser(stream, macros, html);
			html.flush();
			html.close();

			response.setContentType("text/html");
			final PrintWriter writer = response.getWriter();
			writer.append(html_buf.toString());
		}
		catch (final Exception ex)
		{
			logger.log(Level.WARNING, "Cannot read " + display_name, ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		}
	}
}
