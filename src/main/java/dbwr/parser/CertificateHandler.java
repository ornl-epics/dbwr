/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.security.cert.X509Certificate;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class CertificateHandler
{
	private static boolean trusting = false;

	/**
     * Allow https:// access to self-signed certificates
     *
     * @throws Exception on error
     */
    public static synchronized void trustAnybody() throws Exception
    {
    	if (trusting)
    		return;

        // Create a trust manager that does not validate certificate chains.
        final TrustManager[] trustAllCerts = new TrustManager[]
		{
            new X509TrustManager()
            {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType)
                {
                    /* NOP */ }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType)
                {
                    /* NOP */ }
            }
        };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // All-trusting host name verifier
        final HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        logger.log(Level.INFO, "Disabled https certificate checks");
        trusting = true;
    }
}
