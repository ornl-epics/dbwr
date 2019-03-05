package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

public class PVWidget extends Widget
{
	protected final String pv_name;

	public PVWidget(final MacroProvider parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type);
		pv_name = XMLUtil.getChildString(parent, xml, "pv_name").orElse(null);
		attributes.put("data-pv", pv_name);
	}

	public PVWidget(final MacroProvider parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
		pv_name = XMLUtil.getChildString(parent, xml, "pv_name").orElse(null);
		attributes.put("data-pv", pv_name);
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.escape(html, "<" + pv_name + ">");
	}
}
