package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Boolean Monitor Widget
 *  @author Davy Dequidt
 */
public class BoolMonitorWidget extends PVWidget
{
    static
    {
        WidgetFactory.addJavaScript("boolmonitor.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.symbol.bool.BoolMonitorWidget", "boolmonitor");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.symbol.bool.BoolControlWidget", "boolmonitor");
    }

    private final String imgUrl;

    public BoolMonitorWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "boolmonitor", 150, 100);

        final String file = XMLUtil.getChildString(this, xml, "file")
                                   .orElse(XMLUtil.getChildString(this, xml, "image_file")
                                                  .orElse("missing_image.png"));

        imgUrl = Resolver.resolve(parent, file);
        attributes.put("data-img-off", imgUrl);
        attributes.put("data-img-on", imgUrl.replaceAll("Off\\.svg", "On.svg"));

        //TODO get non zero color from opi

        // Rotation
        /*
        <permutation_matrix>
        <row>
          <col>0.0</col>
          <col>-1.0</col>
        </row>
        <row>
          <col>1.0</col>
          <col>0.0</col>
        </row>
      </permutation_matrix>

        // --> transform: matrix(0,1,-1,0,0,0);
         */

        Element permutationMatrix = XMLUtil.getChildElement(xml, "permutation_matrix");
        NodeList rows = permutationMatrix.getChildNodes();
        NodeList colsOfRow1 = rows.item(1).getChildNodes();
        NodeList colsOfRow2 = rows.item(3).getChildNodes();
        if (colsOfRow1.getLength() == 5 && colsOfRow2.getLength() == 5) {
            styles.put("transform",
                    "matrix(" + colsOfRow2.item(3).getTextContent() + "," + colsOfRow2.item(1).getTextContent() + ","
                            + colsOfRow1.item(3).getTextContent() + "," + colsOfRow1.item(1).getTextContent()
                            + ",1,1)");
        }
    }

    @Override
    protected String getHTMLElement()
    {
        return "svg";
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        HTMLUtil.indent(html, indent + 2);
        html.println("<defs><filter id=\"blackToBlue\">"
                + " <feColorMatrix\n" +
                "        type=\"matrix\"\n" +
                "        values=\"1 0 0 0 0\n" +
                "                0 1 0 0 0\n" +
                "                0 0 1 1 0\n" +
                "                0 0 0 1 0\"/>"
                + "</filter></defs>");
        //url(#blackToBlue)
        html.println("<image filter=\"\" width=\"" + width + "\" height =\"" + height + "\" xlink:href=\"" + imgUrl + "\" />");
    }
}