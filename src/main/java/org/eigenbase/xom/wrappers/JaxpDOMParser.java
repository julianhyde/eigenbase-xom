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

import org.eigenbase.xom.XOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * A <code>JaxpDOMParser</code> implements {@link org.eigenbase.xom.Parser} using
 * a {@link DocumentBuilder JAXP-compliant parser}.
 *
 * @author jhyde
 */
public class JaxpDOMParser extends GenericDOMParser {
    private DocumentBuilder builder;

    /** Creates a non-validating parser. */
    public JaxpDOMParser() throws XOMException {
        this(false);
    }

    /** Creates a parser. */
    public JaxpDOMParser(boolean validating) throws XOMException {
        try {
            DocumentBuilderFactory factory = createSecureDocBuilderFactory();
            factory.setValidating(validating);
            try {
                factory.setAttribute(VALIDATION_FEATURE, new Boolean(validating));
                factory.setAttribute(LOAD_EXTERNAL_DTD_FEATURE, new Boolean(validating));
            } catch (IllegalArgumentException e) {
                // Weblogic 6.1's parser complains 'No arguments are
                // implemented'
            }
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XOMException(e, "Error creating parser");
        } catch (FactoryConfigurationError e) {
            throw new XOMException(e, "Error creating parser");
        }
        builder.setErrorHandler(this);
        document = builder.newDocument();
    }

    protected Document parseInputSource(InputSource in) throws XOMException {
        prepareParse();
        try {
            Document document = builder.parse(in);
            handleErrors();
            return document;
        } catch (SAXException e) {
            // Display any pending errors
            handleErrors();
            throw new XOMException(e, "Document parse failed");
        } catch (IOException e) {
            // Display any pending errors
            handleErrors();
            throw new XOMException(e, "Document parse failed");
        }
    }

    /**
     * Creates an instance of {@link DocumentBuilderFactory} class
     * with enabled {@link XMLConstants#FEATURE_SECURE_PROCESSING} property.
     * Enabling this feature prevents from some XXE attacks (e.g. XML bomb)
     * See http://jira.pentaho.com/browse/PPP-3506 for more details.
     *
     * @throws ParserConfigurationException if feature can't be enabled
     *
     */
    private DocumentBuilderFactory createSecureDocBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        return docBuilderFactory;
    }
}

// End JaxpDOMParser.java
