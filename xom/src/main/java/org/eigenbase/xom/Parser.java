/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.eigenbase.xom;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * The <code>Parser</code> interface abstracts the behavior which the
 * <code>org.eigenbase.xom</code> package needs from an XML parser.
 *
 * <p>If you don't care which implementation you get, call {@link
 * XOMUtil#createDefaultParser} to create a parser.</p>
 *
 * @author jhyde
 */
public interface Parser {
    /**
     * Sets whether to retain position information.
     * @param keepPositions Whether to keep position information.
     */
    void setKeepPositions(boolean keepPositions);

    /**
     * Returns whether the parser is retaining position information.
     *
     * @return Whether to keep position information.
     */
    boolean isKeepPositions();

    /**
     * Parses a string and returns a wrapped element.
     *
     * @param sXml XML string
     * @return Wrapped element
     * @throws XOMException on error
     */
    DOMWrapper parse(String sXml) throws XOMException;

    /**
     * Parses an input stream and returns a wrapped element.
     *
     * @param is Input stream
     * @return Wrapped element
     * @throws XOMException on error
     */
    DOMWrapper parse(InputStream is) throws XOMException;

    /**
     * Parses the contents of a URL and returns a wrapped element.
     *
     * @param url URL
     * @return Wrapped element
     * @throws XOMException on error
     */
    DOMWrapper parse(URL url) throws XOMException;

    /**
     * Parses the contents of a reader and returns a wrapped element.
     *
     * @param reader Reader
     * @return Wrapped element
     * @throws XOMException on error
     */
    DOMWrapper parse(Reader reader) throws XOMException;

    /**
     * Creates a wrapper representing an XML element.
     *
     * @param tagName Name of element
     * @return Wrapper element
     */
    DOMWrapper create(String tagName);
}

// End Parser.java
