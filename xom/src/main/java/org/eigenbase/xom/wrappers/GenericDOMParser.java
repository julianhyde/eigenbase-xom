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
package org.eigenbase.xom.wrappers;

import org.eigenbase.xom.*;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.net.URL;

/**
 * A <code>GenericDOMParser</code> is an abstract base class for {@link
 * XercesDOMParser} and {@link JaxpDOMParser}.
 *
 * @author jhyde
 */
abstract class GenericDOMParser
    implements ErrorHandler, org.eigenbase.xom.Parser, Locator
{

    // Used for capturing error messages as they occur.
    StringWriter errorBuffer = null;
    PrintWriter errorOut = null;

    /** The document which spawns elements. The constructor of the derived
     * class must set this. */
    protected Document document;

    static final String LOAD_EXTERNAL_DTD_FEATURE =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    static final String VALIDATION_FEATURE =
            "http://xml.org/sax/features/validation";
    private boolean keepPositions;
    private Annotator annotator;

    public DOMWrapper create(String tagName) {
        Element element = document.createElement(tagName);
        return new W3CDOMWrapper(element, this);
    }

    public DOMWrapper parse(InputStream is) throws XOMException {
        TeeInputStream tis = new TeeInputStream(is);
        InputSource source = new InputSource(tis);
        Document document = parseInputSource(source);
        final W3CDOMWrapper wrapper =
            new W3CDOMWrapper(document.getDocumentElement(), this);
        if (keepPositions) {
            String xmlString = new String(tis.getBytes());
            annotator = new Annotator(xmlString, wrapper);
        }
        return wrapper;
    }

    public void setKeepPositions(boolean keepPositions) {
        this.keepPositions = keepPositions;
    }

    public boolean isKeepPositions() {
        return keepPositions;
    }

    public DOMWrapper parse(String xmlString) throws XOMException {
        final DOMWrapper wrapper = parse(new StringReader(xmlString));
        if (keepPositions) {
            annotator = new Annotator(xmlString, wrapper);
        }
        return wrapper;
    }

    public DOMWrapper parse(Reader reader) throws XOMException {
        Document document = parseInputSource(new InputSource(reader));
        return new W3CDOMWrapper(document.getDocumentElement(), this);
    }

    /**
     * Parses the specified URI and returns the document.
     * @param in Input source
     * @return Document
     * @throws org.eigenbase.xom.XOMException on error
     */
    protected abstract Document parseInputSource(InputSource in)
            throws XOMException;

    /** Warning. */
    public void warning(SAXParseException ex) {
        errorOut.println("[Warning] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {
        errorOut.println("[Error] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex)
            throws SAXException {
        errorOut.println("[Fatal Error] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
        throw ex;
    }

    /** Returns a string of the location.
     * @param ex Exception
     * @return Location string, e.g. "file.xml:4:72"
     */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());
        return str.toString();
    }

    // implement Parser
    public DOMWrapper parse(URL url)
            throws XOMException {
        try {
            return parse(new BufferedInputStream(url.openStream()));
        } catch (IOException ex) {
            throw new XOMException(ex, "Document parse failed");
        }
    }

    // Helper: reset the error buffer to prepare for a new parse.
    protected void prepareParse() {
        errorBuffer = new StringWriter();
        errorOut = new PrintWriter(errorBuffer);
    }

    // Helper: throw an exception with messages of any errors
    // accumulated during the parse.
    protected void handleErrors() throws XOMException {
        errorOut.flush();
        String errorStr = errorBuffer.toString();
        if (errorStr.length() > 0) {
            throw new XOMException("Document parse failed: " + errorStr);
        }
    }

    // implement Locator
    public Location getLocation(DOMWrapper wrapper) {
        return annotator.getLocation(wrapper);
    }

    /**
     * Input stream that keeps a copy of every byte that flows through it.
     */
    private static class TeeInputStream extends FilterInputStream {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        TeeInputStream(InputStream in) {
            super(in);
        }

        public int read() throws IOException {
            int x = super.read();
            baos.write(x);
            return x;
        }

        /**
         * Returns the bytes that have been read from this stream.
         *
         * @return Array of bytes that have been read from this stream
         */
        public byte[] getBytes() {
            return baos.toByteArray();
        }
    }
}

// End GenericDOMParser.java
