/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Picture Widget
 *  @author Kay Kasemir
 */
public class PictureWidget extends Widget
{
    static
    {
        WidgetFactory.addJavaScript("picture.js");
        WidgetFactory.registerLegacy("org.csstudio.opibuilder.widgets.Image", "picture");
    }

	public PictureWidget(final ParentWidget parent, final Element xml) throws Exception
	{
		super(parent, xml, "picture", 150, 100);

		// BOY used "image_file"
		final String file = XMLUtil.getChildString(this, xml, "file")
		                           .orElse(XMLUtil.getChildString(this, xml, "image_file")
		                                          .orElse("missing_image.png"));


		attributes.put("data-file", file);

        // Rotation
		final int rotate = XMLUtil.getChildDouble(xml, "rotation").orElse(0.0).intValue();
		if (rotate != 0)
		{
		    // Plain rotation around center
		    // For multiples of 90 degree, this matches the desktop display builder.
		    // For other angles, the desktop version adjusts the image size to stay
		    // within original bounds, which is omitted here.
		    styles.put("transform", "rotate(" + rotate + "deg)");
		}

		final Element matrix = XMLUtil.getChildElement(xml, "permutation_matrix");
		if (matrix != null)
		{
	        // Legacy (BOY):
	        // <permutation_matrix>
	        //   <row>
	        //     <col>0.0</col>
	        //     <col>-1.0</col>
	        //   </row>
	        //   <row>
	        //     <col>1.0</col>
	        //     <col>0.0</col>
	        //   </row>
	        // </permutation_matrix>
	        // --> transform: matrix(0,1,-1,0,0,0);
            final NodeList rows = matrix.getChildNodes();
            final NodeList row1 = rows.item(1).getChildNodes();
            final NodeList row2 = rows.item(3).getChildNodes();
            if (row1.getLength() == 5 && row2.getLength() == 5)
                styles.put("transform",
                           "matrix(" + row2.item(3).getTextContent() + "," + row2.item(1).getTextContent() + ","
                                + row1.item(3).getTextContent() + "," + row1.item(1).getTextContent()
                                + ",1,1)");
		}
	}

	@Override
    protected String getHTMLElement()
	{
        return "img";
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
	    // Nothing inside 'img'
	}
}
