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

import java.io.PrintWriter;

/**
 * <code>NodeDef</code> represents a node in a parse tree. It is a base class
 * for {@link ElementDef}, {@link TextDef}, etc.
 *
 * @author jhyde
 */
public interface NodeDef {

    /**
     * Returns the name of this node's tag.
     */
    String getName();

    /**
     * Returns the type of this element.
     * Values are as for {@link DOMWrapper#getType}.
     */
    int getType();

    /**
     * Returns the text inside this node.
     */
    String getText();

    /**
     * Returns the children of this node.
     */
    NodeDef[] getChildren();

    /**
     * Outputs this element definition in XML to any XMLOutput.
     * @param out the XMLOutput class to display the XML
     */
    void displayXML(XMLOutput out, int indent);

    /**
     * Outputs this node to any PrintWriter,
     * in a formatted fashion with automatic indenting.
     * @param out the PrintWriter to which to write this NodeDef.
     * @param indent the indentation level for the printout.
     */
    void display(PrintWriter out, int indent);

    /**
     * Retrieves the {@link DOMWrapper} which was used to create this
     * node. Only works if this nodes's {@link MetaDef.Element#keepDef} was
     * true (or, if it is not set, if the default
     * {@link MetaDef.Model#defaultKeepDef} is true);
     * otherwise, returns <code>null</code>.
     *
     * @return wrapper underlying this node
     */
    DOMWrapper getWrapper();

    /**
     * Returns the location of this element in its document.
     *
     * @return location of this element, or null if location is not available
     */
    Location getLocation();
}

// End NodeDef.java
