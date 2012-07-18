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

import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.XOMException;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * This private helper class presents a GenericDOMParser using Xerces, with
 * simple error handling appropriate for a testing environment.
 */

public class XercesDOMParser extends GenericDOMParser {
    private DOMParser parser;

    /**
     * Constructs a non-validating Xerces DOM Parser.
     */
    public XercesDOMParser() throws XOMException {
        this(false);
    }

    /**
     * Constructs a Xerces DOM Parser.
     * @param validate whether to enable validation
     */
    public XercesDOMParser(boolean validate) throws XOMException {
        parser = new DOMParser();
        try {
            if (!validate) {
                parser.setFeature(VALIDATION_FEATURE, false);
                parser.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
            }
        } catch (SAXException e) {
            throw new XOMException(e, "Error setting up validation");
        }

        parser.setErrorHandler(this);
        document = new DocumentImpl();
    }

    // implement GenericDOMParser
    protected Document parseInputSource(InputSource in) throws XOMException {
        prepareParse();
        try {
            parser.parse(in);
        } catch (SAXException ex) {
            // Display any pending errors
            handleErrors();
            throw new XOMException(ex, "Document parse failed");
        } catch (IOException ex) {
            // Display any pending errors
            handleErrors();
            throw new XOMException(ex, "Document parse failed");
        }

        handleErrors();
        return parser.getDocument();
    }

    // implement Parser
    public DOMWrapper create(String tagName) {
        Node node = document.createElement(tagName);
        return new W3CDOMWrapper(node, this);
    }
}

// End XercesDOMParser.java
