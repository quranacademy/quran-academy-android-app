// This file is part of TagSoup and is Copyright 2002-2008 by John Cowan.
//
// TagSoup is licensed under the Apache License,
// Version 2.0.  You may obtain a copy of this license at
// http://www.apache.org/licenses/LICENSE-2.0 .  You may also have
// additional legal rights not granted by this license.
//
// TagSoup is distributed in the hope that it will be useful, but
// unless required by applicable law or agreed to in writing, TagSoup
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied; not even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// 
// 
// PYX Writer
// FIXME: does not do escapes in attribute values
// FIXME: outputs entities as bare '&' character
package ccil.cowan.tagsoup;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * A ContentHandler that generates PYX format instead of XML.
 * Primarily useful for debugging.
 **/
public class PYXWriter
        implements ScanHandler, ContentHandler, LexicalHandler {
    private PrintWriter theWriter;        // where we write to
    private static char[] dummy = new char[1];
    private String attrName;        // saved attribute name

    // ScanHandler implementation
    public void adup(char[] buff, int offset, int length) {
        theWriter.println(attrName);
        attrName = null;
    }

    public void aname(char[] buff, int offset, int length) {
        theWriter.print('A');
        theWriter.write(buff, offset, length);
        theWriter.print(' ');
        attrName = new String(buff, offset, length);
    }

    public void aval(char[] buff, int offset, int length) {
        theWriter.write(buff, offset, length);
        theWriter.println();
        attrName = null;
    }

    public void cmnt(char[] buff, int offset, int length) {
//		theWriter.print('!');
//		theWriter.write(buff, offset, length);
//		theWriter.println();
    }

    public void entity(char[] buff, int offset, int length) {
    }

    public int getEntity() {
        return 0;
    }

    public void eof(char[] buff, int offset, int length) {
        theWriter.close();
    }

    public void etag(char[] buff, int offset, int length) {
        theWriter.print(')');
        theWriter.write(buff, offset, length);
        theWriter.println();
    }

    public void decl(char[] buff, int offset, int length) {
    }

    public void gi(char[] buff, int offset, int length) {
        theWriter.print('(');
        theWriter.write(buff, offset, length);
        theWriter.println();
    }

    public void cdsect(char[] buff, int offset, int length) throws SAXException {
        pcdata(buff, offset, length);
    }

    public void pcdata(char[] buff, int offset, int length) {
        if (length == 0) return;    // nothing to do
        boolean inProgress = false;
        length += offset;
        for (int i = offset; i < length; i++) {
            if (buff[i] == '\n') {
                if (inProgress) {
                    theWriter.println();
                }
                theWriter.println("-\\n");
                inProgress = false;
            } else {
                if (!inProgress) {
                    theWriter.print('-');
                }
                switch (buff[i]) {
                    case '\t':
                        theWriter.print("\\t");
                        break;
                    case '\\':
                        theWriter.print("\\\\");
                        break;
                    default:
                        theWriter.print(buff[i]);
                }
                inProgress = true;
            }
        }
        if (inProgress) {
            theWriter.println();
        }
    }

    public void pitarget(char[] buff, int offset, int length) {
        theWriter.print('?');
        theWriter.write(buff, offset, length);
        theWriter.write(' ');
    }

    public void pi(char[] buff, int offset, int length) {
        theWriter.write(buff, offset, length);
        theWriter.println();
    }

    public void stagc(char[] buff, int offset, int length) {
//		theWriter.println("!");			// FIXME
    }

    public void stage(char[] buff, int offset, int length) {
        theWriter.println("!");            // FIXME
    }

    // SAX ContentHandler implementation
    public void characters(char[] buff, int offset, int length) throws SAXException {
        pcdata(buff, offset, length);
    }

    public void endDocument() {
        theWriter.close();
    }

    public void endElement(String uri, String localname, String qname) {
        if (qname.length() == 0) qname = localname;
        theWriter.print(')');
        theWriter.println(qname);
    }

    public void endPrefixMapping(String prefix) {
    }

    public void ignorableWhitespace(char[] buff, int offset, int length) throws SAXException {
        characters(buff, offset, length);
    }

    public void processingInstruction(String target, String data) {
        theWriter.print('?');
        theWriter.print(target);
        theWriter.print(' ');
        theWriter.println(data);
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) {
    }

    public void startDocument() {
    }

    public void startElement(String uri, String localname, String qname,
                             Attributes atts) {
        if (qname.length() == 0) qname = localname;
        theWriter.print('(');
        theWriter.println(qname);
        int length = atts.getLength();
        for (int i = 0; i < length; i++) {
            qname = atts.getQName(i);
            if (qname.length() == 0) qname = atts.getLocalName(i);
            theWriter.print('A');
//			theWriter.print(atts.getType(i));	// DEBUG
            theWriter.print(qname);
            theWriter.print(' ');
            theWriter.println(atts.getValue(i));
        }
    }

    public void startPrefixMapping(String prefix, String uri) {
    }

    // Default LexicalHandler implementation
    public void comment(char[] ch, int start, int length) throws SAXException {
        cmnt(ch, start, length);
    }

    public void endCDATA() {
    }

    public void endDTD() {
    }

    public void endEntity(String name) {
    }

    public void startCDATA() {
    }

    public void startDTD(String name, String publicId, String systemId) {
    }

    public void startEntity(String name) {
    }

    // Constructor
    public PYXWriter(Writer w) {
        if (w instanceof PrintWriter) {
            theWriter = (PrintWriter) w;
        } else {
            theWriter = new PrintWriter(w);
        }
    }
}