package dbwr.parser;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;
import dbwr.widgets.Widget;

public class DisplayParser implements MacroProvider
{
	final int width, height;
	final Map<String, String> macros;

	public DisplayParser(final InputStream stream, final Map<String, String> macros, final PrintWriter html) throws Exception
	{
		final Element root = XMLUtil.openXMLDocument(stream, "display");

		this.macros = new HashMap<>();
		this.macros.putAll(macros);
		// Fetch macros first to allow use in remaining properties
		this.macros.putAll(MacroUtil.fromXML(root));

		// TODO Replace macros in integer etc. (in XMLUtil)
		width = XMLUtil.getChildInteger(root, "width").orElse(800);
		height = XMLUtil.getChildInteger(root, "height").orElse(600);

		final String background = XMLUtil.getColor(root, "background_color").orElse("#FFF");

		html.println("<div class=\"Screen\" style=\"width: " + width + "px; height: " + height + "px; background-color: " + background + ";\">");
		for (final Element xml : XMLUtil.getChildElements(root, "widget"))
		{
			final Widget widget = WidgetFactory.createWidget(this, xml);
			widget.getHTML(html, 1);
		}
		html.println("</div>");
	}

    @Override
    public Collection<String> getMacroNames()
    {
        return macros.keySet();
    }

    @Override
    public String getMacroValue(final String name)
    {
        return macros.get(name);
    }
}
