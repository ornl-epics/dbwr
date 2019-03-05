package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.FontInfo;
import dbwr.parser.HTMLUtil;
import dbwr.parser.XMLUtil;

public class LabelWidget extends Widget
{
	static final FontInfo DEFAULT_FONT = new FontInfo(14, false);
	private final String text;

	public LabelWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "label");
		text = XMLUtil.getChildString(this, xml, "text").orElse("Label text");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(DEFAULT_FONT);
		font.addToStyles(styles);

		final int align = XMLUtil.getChildInteger(xml, "horizontal_alignment").orElse(0);
		if (align == 1)
		    styles.put("text-align", "center");
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		HTMLUtil.escape(html, text);
	}
}
