package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.FontInfo;
import dbwr.parser.XMLUtil;

public class TextUpdateWidget extends PVWidget
{
	public TextUpdateWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "textupdate");
		// classes.add("Debug");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(false))
		{
			final String background = XMLUtil.getColor(xml, "background_color").orElse("#F0F0F0");
			styles.put("background-color", background);
		}
	}

	@Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		super.fillHTML(html, indent);
	}
}
