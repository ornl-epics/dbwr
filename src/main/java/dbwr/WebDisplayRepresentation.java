/*******************************************************************************
 * Copyright (c) 2019-2021 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.fasterxml.jackson.core.JsonFactory;

@WebListener
public class WebDisplayRepresentation implements ServletContextListener
{
    /** Common logger */
	public static final Logger logger = Logger.getLogger(WebDisplayRepresentation.class.getPackage().getName());

	/** Common JSON factory */
	public static final JsonFactory json_factory = new JsonFactory();

	public static final List<String> display_options = new ArrayList<>();

	public static final List<Pattern> whitelist_options = new ArrayList<>();

	/** Custom ws://some_host.org:8081/pvws URL or <code>null</code> */
    public static final String pvws_ws_url;

	/** Custom http://some_host.org:8081/pvws URL or <code>null</code> */
    public static final String pvws_http_url;
	
	static
	{
	    // Load display links for the start page from environment variables "DBWR1", "DBWR2", ...
	    int i=1;
	    String display = System.getenv("DBWR" + i);
	    while (display != null)
	    {
	        display_options.add(display);
	        display = System.getenv("DBWR" + (++i));
	    }

	    // If none are provided, add some defaults
	    if (display_options.isEmpty())
	    {
	        display_options.add("https://raw.githubusercontent.com/ControlSystemStudio/phoebus/master/app/display/model/src/main/resources/examples/01_main.bob");
	        display_options.add("file:/some/local/display.bob");
	    }

	    // Load whitelist patterns from environment variables "WHITELIST1", "WHITELIST2", ...
	    i=1;
	    String whitelist = System.getenv("WHITELIST" + i);
        while (whitelist != null)
        {
            logger.log(Level.INFO, "WHITELIST" + i + " = " + whitelist);
            whitelist_options.add(Pattern.compile(whitelist));
            whitelist = System.getenv("WHITELIST" + (++i));
        }

        // If none are provided, allow everything
        if (whitelist_options.isEmpty())
        {
            logger.log(Level.INFO, "No WHITELIST1 etc., allowing all display links");
            whitelist_options.add(Pattern.compile(".*"));
        }
        
        final String pvws_host = System.getenv("PVWS_HOST");
        final String pvws_ws_protocol = System.getenv("PVWS_WS_PROTOCOL");
        final String pvws_http_protocol = System.getenv("PVWS_HTTP_PROTOCOL");

        if (pvws_host == null  || pvws_host.trim().isEmpty())
        {
            pvws_ws_url = null;
            pvws_http_url = null;
        }
        else
        {
            if (pvws_ws_protocol == null  || pvws_ws_protocol.trim().isEmpty())
                pvws_ws_protocol = "ws";
            if (pvws_http_protocol == null  || pvws_http_protocol.trim().isEmpty())
                pvws_http_protocol = "http";

            pvws_ws_url = pvws_ws_protocol.trim() + "://" + pvws_host.trim() + "/pvws";
            pvws_http_url = pvws_http_protocol.trim() + "://" + pvws_host.trim() + "/pvws";
        }
	}

	@Override
	public void contextInitialized(final ServletContextEvent ev)
    {
        final ServletContext context = ev.getServletContext();

        logger.log(Level.INFO, "===========================================");
        logger.log(Level.INFO, context.getContextPath() + " started");
        logger.log(Level.INFO, "===========================================");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent ev)
    {
        final ServletContext context = ev.getServletContext();

        logger.log(Level.INFO, "===========================================");
        logger.log(Level.INFO, context.getContextPath() + " shut down");
        logger.log(Level.INFO, "===========================================");
    }
}
