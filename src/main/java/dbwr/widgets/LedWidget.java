package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.XMLUtil;

public class LedWidget extends SvgPVWidget
{

	public LedWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "led", 20, 20);
		classes.add("Led");

		final String on_color = XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C");
		final String off_color = XMLUtil.getColor(xml, "off_color").orElse("#3C643C");
		attributes.put("data-on-color", on_color);
		attributes.put("data-off-color", off_color);
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		final int rx = width/2, ry = height/2;

		html.append("<ellipse cx=\"" + rx + "\" cy=\"" +  ry + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" fill=\"grey\"></ellipse>");
	}
}
