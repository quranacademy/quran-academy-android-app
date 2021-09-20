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
// Scanner handler
package ccil.cowan.tagsoup;

import org.xml.sax.SAXException;

/**
 * An interface that Scanners use to report events in the input stream.
 **/
public interface ScanHandler {
    /**
     * Reports an attribute name without a value.
     **/
    void adup(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports an attribute name; a value will follow.
     **/
    void aname(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports an attribute value.
     **/
    void aval(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the content of a CDATA section (not a CDATA element)
     */
    void cdsect(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports a <!....> declaration - typically a DOCTYPE
     */
    void decl(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports an entity reference or character reference.
     **/
    void entity(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports EOF.
     **/
    void eof(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports an end-tag.
     **/
    void etag(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the general identifier (element type name) of a start-tag.
     **/
    void gi(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports character content.
     **/
    void pcdata(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the data part of a processing instruction.
     **/
    void pi(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the target part of a processing instruction.
     **/
    void pitarget(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the close of a start-tag.
     **/
    void stagc(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports the close of an empty-tag.
     **/
    void stage(char[] buff, int offset, int length) throws SAXException;

    /**
     * Reports a comment.
     **/
    void cmnt(char[] buff, int offset, int length) throws SAXException;

    /**
     * Returns the value of the last entity or character reference reported.
     **/
    int getEntity();
}