/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dbwr.parser.HTMLUtil;
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
        return "img";
    }

    @Override
	protected void fillHTML(final PrintWriter html, final int indent)
	{
		// Nothing inside 'img'
	}
}
