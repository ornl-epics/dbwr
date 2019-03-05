package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;

public class SvgWidget extends Widget
{
	public SvgWidget(final MacroProvider parent, final Element xml, final String type) throws Exception
	{
		super(parent, xml, type, 100, 20);
	}

	public SvgWidget(final MacroProvider parent, final Element xml, final String type, final int default_width, final int default_height) throws Exception
	{
		super(parent, xml, type, default_width, default_height);
	}

	@Override
	protected void getHTMLElement(final PrintWriter html)
	{
		html.append("svg");
	}
}

