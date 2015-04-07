package ru.onyx.clipper.pdfgenerator.model;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * User: Alex
 * Date: 16.03.12
 * Time: 13:53
 */
 public class SkeletonNameSpaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if (prefix == null) throw new NullPointerException("Null prefix");
            else if ("".equals(prefix)) return "http://ru.pega/doc/generator";
            return XMLConstants.NULL_NS_URI;
        }

        // This method isn't necessary for XPath processing.
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        // This method isn't necessary for XPath processing either.
        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }

    }
