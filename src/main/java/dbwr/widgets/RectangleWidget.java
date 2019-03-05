package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

public class RectangleWidget extends EllipseWidget
{
    private final double rx, ry;

	public RectangleWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "rectangle");
		rx = XMLUtil.getChildDouble(xml, "corner_width").orElse(0.0);
        ry = XMLUtil.getChildDouble(xml, "corner_height").orElse(0.0);
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
        HTMLUtil.indent(html, indent+2);
        // Move the line width 'inside'
        final int inset = line_width / 2;
        html.print("<rect x=\"" + inset + "\" y=\"" + inset + "\" width=\"" + (width-line_width) + "\" height=\"" + (height-line_width) + "\"");
        if (rx > 0.0)
            html.print(" rx=\"" + rx + "\"");
        if (ry > 0.0)
            html.print(" ry=\"" + ry + "\"");
        html.println(" stroke=\"" + line_color + "\" stroke-width=\"" + line_width + "\" fill=\"" + background_color + "\"/>");
	}
}
