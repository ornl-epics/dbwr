package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroProvider;
import dbwr.widgets.UnknownWidget;
import dbwr.widgets.Widget;

@SuppressWarnings("unchecked")
public class WidgetFactory
{
	private static final Map<String, String> BOY_TYPES = new HashMap<>();
	private static final Map<String, Class<Widget>> widget_classes = new HashMap<>();
	public static final List<String> js = new ArrayList<>();
	public static final List<String> css = new ArrayList<>();

	static
	{
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.Label", "label");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.groupingContainer", "group");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.TextUpdate", "textupdate");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.LED", "led");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.ActionButton", "action_button");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.MenuButton", "action_button");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.TextInput", "textentry");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.Rectangle", "rectangle");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.Ellipse", "ellipse");
		BOY_TYPES.put("org.csstudio.opibuilder.widgets.polyline", "polyline");
        BOY_TYPES.put("org.csstudio.opibuilder.widgets.polygon", "polygon");
        BOY_TYPES.put("org.csstudio.opibuilder.widgets.arc", "arc");
//      BOY_TYPES.put("", "");
//      BOY_TYPES.put("", "");
//      BOY_TYPES.put("", "");
//      BOY_TYPES.put("", "");


		try
		{
			final Properties wp = new Properties();
			wp.load(WidgetFactory.class.getResourceAsStream("/widget.properties"));
			for (final Object type : wp.keySet())
			{
				final String[] info = wp.getProperty(type.toString()).split("\\s*,\\s*");
				final String clazz = info[0];
				widget_classes.put(type.toString(), (Class<Widget>)Class.forName(clazz));
				if (info.length > 1  &&  !info[1].isEmpty())
					js.add(info[1]);
				if (info.length > 2  &&  !info[2].isEmpty())
					css.add(info[2]);
			}
		}
		catch (final Exception ex)
		{
			logger.log(Level.SEVERE, "Cannot load widget info", ex);
		}

		for (final Map.Entry<String, Class<Widget>> entry : widget_classes.entrySet())
			logger.log(Level.CONFIG, entry.getKey() + " - " + entry.getValue());
		logger.log(Level.CONFIG, "\n\nJavaScript: " + js);
		logger.log(Level.CONFIG, "CSS: " + css);
	}

	public static Widget createWidget(final MacroProvider parent, final Element xml) throws Exception
	{
		String type = xml.getAttribute("type");
		if (type.isEmpty())
			type = BOY_TYPES.get(xml.getAttribute("typeId"));

		final Class<Widget> clazz = widget_classes.get(type);
		if (clazz == null)
			return new UnknownWidget(parent, xml, type);

		final Constructor<Widget> constructor = clazz.getDeclaredConstructor(MacroProvider.class, Element.class);
		return constructor.newInstance(parent, xml);
	}
}
