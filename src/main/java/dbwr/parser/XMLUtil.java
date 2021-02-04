/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package dbwr.parser;

import static dbwr.WebDisplayRepresentation.logger;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import dbwr.macros.MacroProvider;
import dbwr.macros.MacroUtil;

/** XML Helper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLUtil
{
    public static final String ENCODING = "UTF-8";

    /** Open XML document and locate root element
     *  @param stream XML stream
     *  @param expected_root Desired name of root element
     *  @return That root element
     *  @throws Exception on error, including document with wrong root
     */
    public static Element openXMLDocument(final InputStream stream,
            final String expected_root) throws Exception
    {
        // Parse XML
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Disable DTDs to prevent XML entity attacks
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        final Document doc = dbf.newDocumentBuilder().parse(stream);
        doc.getDocumentElement().normalize();

        // Check root element
        final Element root_node = doc.getDocumentElement();
        if (! expected_root.equals(root_node.getNodeName()))
            throw new Exception("Wrong document type. Expected <" +
                    expected_root + "> but found <" +
                    root_node.getNodeName() + ">");
        return root_node;
    }

    /** Iterator over all Elements (not just Nodes) of a parent */
    private static class ElementIterator implements Iterator<Element>
    {
        private Element next_node;

        ElementIterator(final Node parent)
        {
            next_node = findElement(parent.getFirstChild());
        }

        @Override
        public boolean hasNext()
        {
            return next_node != null;
        }

        @Override
        public Element next()
        {
            final Element current = next_node;
            next_node = findElement(current.getNextSibling());
            return current;
        }
    }

    /** Iterator over all Elements (not just Nodes) of a parent
     *  that have specific name.
     *
     *  This iterator allows appending new elements to the document,
     *  after the current iterator position, and the next() call will
     *  then find them.
     *  For that reason 'next' cannot already identify the following
     *  element, because it may not exist, yet.
     */
    private static class NamedElementIterator implements Iterator<Element>
    {
        private final String name;
        private Node first;
        private Element current;

        NamedElementIterator(final Node parent, final String name)
        {
            this.name = name;
            first = parent.getFirstChild();
            current = null;
        }

        @Override
        public boolean hasNext()
        {
            if (first != null)
            {
                current = findElementByName(first, name);
                first = null;
            }
            else if (current != null)
                current = findElementByName(current.getNextSibling(), name);
            return current != null;
        }

        @Override
        public Element next()
        {
            return current;
        }
    }

    /** Look for Element node.
     *
     *  <p>Checks the node and its siblings.
     *  Does not descent down the 'child' links.
     *
     *  @param node Node where to start.
     *  @return Returns node, next Element sibling or <code>null</code>.
     */
    private  static final Element findElement(Node node)
    {
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
                return (Element) node;
            node = node.getNextSibling();
        }
        return null;
    }

    /** Look for Element node of given name.
     *
     *  <p>Checks the node itself and its siblings for an {@link Element}.
     *  Does not descent down the 'child' links.
     *
     *  @param node Node where to start.
     *  @param name Name of the node to look for.
     *  @return Returns node, the next matching sibling, or <code>null</code>.
     */
    private static final Element findElementByName(Node node, final String name)
    {
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                node.getNodeName().equals(name))
                return (Element) node;
            node = node.getNextSibling();
        }
        return null;
    }

    /** Look for child node of given name.
     *
     *  @param parent Node where to start.
     *  @param name Name of the node to look for.
     *  @return Returns Element or <code>null</code>.
     */
    public static final Element getChildElement(final Node parent, final String name)
    {
        return findElementByName(parent.getFirstChild(), name);
    }

    /** Obtain all child elements.
     *  @param parent Parent node
     *  @return {@link Iterable} for child elements
     */
    public static Iterable<Element> getChildElements(final Node parent)
    {
        return () -> new ElementIterator(parent);
    }

    /** Obtain all child elements with given name.
     *  @param parent Parent node
     *  @param name Name of child elements
     *  @return {@link Iterable} for matching child elements
     */
    public static Iterable<Element> getChildElements(final Node parent, final String name)
    {
        return () -> new NamedElementIterator(parent, name);
    }


    /** Get string value of an element.
     *  @param element Element
     *  @return String of the node. Empty string if nothing found.
     */
    public static String getString(final Element element)
    {
        final Node text = element.getFirstChild();
        if (text == null) // <empty /> node
            return "";
        if ((text.getNodeType() == Node.TEXT_NODE  ||
             text.getNodeType() == Node.CDATA_SECTION_NODE))
            return text.getNodeValue();
        return "";
    }

    /** Given a parent element, locate string value of a child node.
     *  @param macros Macros
     *  @param parent Parent element
     *  @param name Name of child element
     *  @return Value of child element, or empty result
     */
    public static Optional<String> getChildString(final MacroProvider macros, final Element parent, final String name)
    {
        final Element child = getChildElement(parent, name);
        if (child != null)
            return Optional.of(MacroUtil.expand(macros, getString(child)));
        else
            return Optional.empty();
    }

    /** Given a parent element, locate integer value of a child node.
     *  @param parent Parent element
     *  @param name Name of child element
     *  @return Value of child element, or empty result
     *  @throws Exception on error parsing the number
     */
    public static Optional<Integer> getChildInteger(final Element parent, final String name) throws Exception
    {
        final Element child = getChildElement(parent, name);
        if (child == null)
            return Optional.empty();
        try
        {
            return Optional.of(Integer.valueOf(getString(child)));
        }
        catch (final NumberFormatException ex)
        {
            throw new Exception("Expected integer for <" + name +">", ex);
        }
    }

    /** Given a parent element, locate long value of a child node.
     *  @param parent Parent element
     *  @param name Name of child element
     *  @return Value of child element, or empty result
     *  @throws Exception on error parsing the number
     */
    public static Optional<Long> getChildLong(final Element parent, final String name) throws Exception
    {
        final Element child = getChildElement(parent, name);
        if (child == null)
            return Optional.empty();
        try
        {
            return Optional.of(Long.valueOf(getString(child)));
        }
        catch (final NumberFormatException ex)
        {
            throw new Exception("Expected long for <" + name +">", ex);
        }
    }

    /** Given a parent element, locate double value of a child node.
     *  @param parent Parent element
     *  @param name Name of child element
     *  @return Value of child element, or empty result
     *  @throws Exception on error parsing the number
     */
    public static Optional<Double> getChildDouble(final Element parent, final String name) throws Exception
    {
        final Element child = getChildElement(parent, name);
        if (child == null)
            return Optional.empty();
        try
        {
            return Optional.of(Double.valueOf(getString(child)));
        }
        catch (final NumberFormatException ex)
        {
            throw new Exception("Expected double for <" + name +">", ex);
        }
    }

    /** Given a parent element, locate boolean value of a child node.
     *  @param parent Parent element
     *  @param name Name of child element
     *  @return Value of child element, or empty result
     */
    public static Optional<Boolean> getChildBoolean(final Element parent, final String name)
    {
        final Element child = getChildElement(parent, name);
        if (child != null)
            return Optional.of(Boolean.parseBoolean(getString(child)));
        else
            return Optional.empty();
    }

    /** @param text Text that should contain true or false
     *  @param default_value Value to use when text is empty
     *  @return Boolean value of text
     */
    public static boolean parseBoolean(final String text, final boolean default_value)
    {
        if (text == null  ||  text.isEmpty())
            return default_value;
        return Boolean.parseBoolean(text);
    }

    public static Color getAWTColor(final Element xml, final String name)
    {
        final Element el = getChildElement(xml, name);
        if (el == null)
            return null;

        final Element col_xml = getChildElement(el, "color");
        if (col_xml == null)
        {
            logger.log(Level.WARNING, "Color element <" + name +"> is missing <color>");
            return null;
        }
        // Ignore legacy *.opi when it just contains <color name="ABC"/>
        if (col_xml.getAttribute("red").isEmpty())
            return null;
        // <color name="OK" red="0" green="255" blue="0"></color>
        final int red = Integer.parseInt(col_xml.getAttribute("red"));
        final int green = Integer.parseInt(col_xml.getAttribute("green"));
        final int blue = Integer.parseInt(col_xml.getAttribute("blue"));
        return new Color(red, green, blue);
    }

    public static String getWebColor(final Color awt_color)
    {
        if (awt_color == null)
            return null;
        return String.format("#%02X%02X%02X", awt_color.getRed(),
                                              awt_color.getGreen(),
                                              awt_color.getBlue());
    }

    public static Optional<String> getColor(final Element xml, final String name)
    {
        final Color color = getAWTColor(xml, name);
        if (color == null)
            return Optional.empty();
        return Optional.of(getWebColor(color));
    }

    public static Optional<FontInfo> getFont(final Element xml, final String name)
    {
        final Element el = getChildElement(xml, name);
        if (el == null)
            return Optional.empty();

        Element font_xml = getChildElement(el, "font");
        if (font_xml == null)
        {
            // Fall back to
            // <opifont.name fontName="Liberation Sans" height="14" style="0" pixels="true">Default</opifont.name>
            font_xml = getChildElement(el, "opifont.name");
            if (font_xml != null)
            {
                final String attr = font_xml.getAttribute("height");
                if (! attr.isEmpty())
                {
                    final int size = (int) Double.parseDouble(attr);
                    final boolean bold = false;
                    final boolean italic = false;
                    return Optional.of(new FontInfo(size, bold, italic));
                }
            }
            // Fall back to <fontdata fontName="Sans" height="11" style="0"/>
            font_xml = getChildElement(el, "fontdata");
            if (font_xml != null)
            {
                final String attr = font_xml.getAttribute("height");
                if (! attr.isEmpty())
                {
                    final int size = (int) Double.parseDouble(attr);
                    final boolean bold = false;
                    final boolean italic = false;
                    return Optional.of(new FontInfo(size, bold, italic));
                }
            }
            // Give up
            logger.log(Level.WARNING, "Font element <" + name +"> is missing <font>");
            return Optional.empty();
        }

        // <font name="Default Bold" family="Liberation Sans" style="BOLD" size="14.0"></font>
        final int size = (int) Double.parseDouble(font_xml.getAttribute("size"));
        final boolean bold = font_xml.getAttribute("style").equals("BOLD");
        final boolean italic = font_xml.getAttribute("style").equals("ITALIC");
        return Optional.of(new FontInfo(size, bold, italic));
    }

    /** Write DOM to stream
     *  @param node Node from which on to write. May be the complete {@link Document}
     *  @param stream Output stream
     *  @throws Exception on error
     */
    public static void writeDocument(final Node node, final OutputStream stream) throws Exception
    {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.transform(new DOMSource(node), new StreamResult(stream));
    }

    /** Write DOM to stream
     *  @param node Node from which on to write. May be the complete {@link Document}
     *  @return XML as text
     *  @throws Exception on error
     */
    public static String toString(final Node node) throws Exception
    {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeDocument(node, buf);
        buf.close();
        return buf.toString();
    }
}

