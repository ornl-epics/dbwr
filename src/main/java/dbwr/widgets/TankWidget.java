package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import dbwr.parser.HTMLUtil;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/**
 * Tank Widget
 *  @author Davy Dequidt
 */
public class TankWidget extends SvgPVWidget
{
    static
    {
        WidgetFactory.addJavaScript("tank.js");
        WidgetFactory.addCSS("tank.css");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.tank", "tank");
    }

    private final String background, fill_color;

    public TankWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "tank", 160, 20);

        attributes.put("min", XMLUtil.getChildString(parent, xml, "minimum").orElse("0"));
        attributes.put("max", XMLUtil.getChildString(parent, xml, "maximum").orElse("255"));

        if (XMLUtil.getChildBoolean(xml, "limits_from_pv").orElse(true))
            attributes.put("data-limits-from-pv", "true");

        // Fall back to BOY's fillbackground
        background = XMLUtil.getColor(xml, "empty_color")
                            .orElse(XMLUtil.getColor(xml, "color_fillbackground").orElse("#CCC"));
        fill_color = XMLUtil.getColor(xml, "fill_color").orElse("#00F");
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        // Gap to eventually allow for scale
        final int left_gap = 30;
        final int vert_gap = 10;
        int w = width - left_gap - 2;
        int h = height - 2*vert_gap;
        HTMLUtil.indent(html, indent + 2);
        html.format("<rect class=\"TankBorder\" x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" " +
                    "style=\"fill:%s\"/>",
                    left_gap, vert_gap, w, h,
                    background);

        final int inset = 4;
        w -= 2*inset;
        h -= 2*inset;

        HTMLUtil.indent(html, indent + 2);
        html.format("<rect class=\"TankBar\" x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" " +
                    "data-top=\"%d\" data-height=\"%d\" " +
                    "style=\"fill:%s\"/>",
                    left_gap + inset, vert_gap + inset + h/2, w, h/2,
                    vert_gap + inset, h,
                    fill_color);
    }
}
