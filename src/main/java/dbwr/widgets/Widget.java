package dbwr.widgets;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

public class Widget implements MacroProvider
{
    protected final MacroProvider parent;
	protected final Set<String> classes = new HashSet<>();
	protected final Map<String, String> attributes = new LinkedHashMap<>();
	protected final Map<String, String> styles = new LinkedHashMap<>();
	protected final int x, y, width, height;

	public Widget(final MacroProvider parent, final Element xml, final String type) throws Exception
	{
		this(parent, xml, type, 100, 20);
	}

	public Widget(final MacroProvider parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
	    this.parent = parent;
		x = XMLUtil.getChildInteger(xml, "x").orElse(0);
		y = XMLUtil.getChildInteger(xml, "y").orElse(0);
		width = XMLUtil.getChildInteger(xml, "width").orElse(default_width);
		height = XMLUtil.getChildInteger(xml, "height").orElse(default_height);

		classes.add("Widget");
		attributes.put("data-type", type);

		styles.put("top", Integer.toString(y)+"px");
		styles.put("left", Integer.toString(x)+"px");
		styles.put("width", Integer.toString(width)+"px");
		styles.put("height", Integer.toString(height)+"px");
	}

	@Override
    public Collection<String> getMacroNames()
	{
        return parent.getMacroNames();
    }

    @Override
    public String getMacroValue(final String name)
    {
        return parent.getMacroValue(name);
    }

    protected void appendClasses(final PrintWriter html)
	{
		html.append("class=\"");
		html.append(classes.stream().collect(Collectors.joining(" ")));
		html.append("\"");
	}

	protected void appendAttributes(final PrintWriter html)
	{
		for (final Map.Entry<String, String> entry : attributes.entrySet())
			html.append(" ")
			    .append(entry.getKey())
			    .append("=\"")
			    .append(entry.getValue())
			    .append("\"");
	}

	protected void appendStyles(final PrintWriter html)
	{
		html.append("style=\"");
		boolean first = true;
		for (final Map.Entry<String, String> entry : styles.entrySet())
		{
			if (first)
				first = false;
			else
				html.append(' ');
			html.append(entry.getKey()).append(": ").append(entry.getValue()).append(';');
		}
		html.append("\"");
	}

	protected void getHTMLElement(final PrintWriter html)
	{
		html.append("div");
	}

	protected void startHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.indent(html, indent);
		html.append("<"); getHTMLElement(html); html.append(" ");
		appendClasses(html);
		appendAttributes(html);
		html.append(' ');
		appendStyles(html);
		html.append(">");
	}

	protected void fillHTML(final PrintWriter html, final int indent)
	{
	}

	protected void endHTML(final PrintWriter html, final int indent)
	{
		html.append("</");
		getHTMLElement(html);
		html.println(">");
	}

	public void getHTML(final PrintWriter html, int indent)
	{
		startHTML(html, indent);
		fillHTML(html, indent);
		endHTML(html, indent);
	}
}

