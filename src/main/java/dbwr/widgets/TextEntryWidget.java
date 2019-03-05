package dbwr.widgets;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.parser.FontInfo;
import dbwr.parser.XMLUtil;

public class TextEntryWidget extends PVWidget
{
	public TextEntryWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		super(parent, xml, "textentry");
		// classes.add("Debug");

		final FontInfo font = XMLUtil.getFont(xml, "font").orElse(LabelWidget.DEFAULT_FONT);
		font.addToStyles(styles);

		if (! XMLUtil.getChildBoolean(xml, "transparent").orElse(false))
		{
			final String background = XMLUtil.getColor(xml, "background_color").orElse("#F0F0F0");
			styles.put("background-color", background);
		}
	}
}
