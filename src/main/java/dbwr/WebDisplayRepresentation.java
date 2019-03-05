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
