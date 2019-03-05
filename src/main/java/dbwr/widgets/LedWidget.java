package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.XMLUtil;

public class LedWidget extends BaseLedWidget
{
	public LedWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "led");

	    final String on_color = XMLUtil.getColor(xml, "on_color").orElse("#3CFF3C");
	    final String off_color = XMLUtil.getColor(xml, "off_color").orElse("#3C643C");
	    attributes.put("data-on-color", on_color);
	    attributes.put("data-off-color", off_color);
	}
}
