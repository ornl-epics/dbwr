package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;

/**
 * Tank Widget
 *  @author Davy Dequidt
 */
public class TankWidget extends SvgPVWidget
{
    static
    {
        WidgetFactory.addJavaScript("tank.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.tank", "tank");
    }

    public TankWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "tank", 160, 20);
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        HTMLUtil.indent(html, indent + 2);
        html.println("\n" +
                "      <rect\n" +
                "         style=\"fill:#fffcfc;stroke:#000000;stroke-width:2;\"\n" +
                "         width=\"64\" height=\"304\" x=\"38\" y=\"48\" />\n" +
                "      <rect class=\"tankbar\"\n" +
                "         style=\"fill:#0000ff;stroke:#000000;stroke-width:0.68848258;stroke-miterlimit:4;stroke-dasharray:none\"\n" +
                "         stroke-linecap=\"null\"\n" +
                "         stroke-linejoin=\"null\"\n" +
                "         width=\"60\"\n" +
                "         x=\"40\"\n" +
                "         height=\"150\"\n" +
                "         y=\"200\"\n" +
                "         maxheight=\"300\"\n" +
                "         inity=\"50\"\n" +
                "      />\n" +
                "");
    }
}