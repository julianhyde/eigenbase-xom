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

import junit.framework.TestCase;

/**
 * Unit tests for XOM module.
 */
public class XomTest extends TestCase {
    public void testFoo() throws XOMException {
        final Parser xmlParser = XOMUtil.createDefaultParser();
        xmlParser.setKeepPositions(true);
        final String lineSep = System.getProperty("line.separator");
        final String xml = "<Model" + lineSep
            + "  name=\"meta\"" + lineSep
            + "  dtdName=\"meta.dtd\"" + lineSep
            + "  className=\"MetaDef\"" + lineSep
            + "  packageName=\"org.eigenbase.xom\"" + lineSep
            + "  root=\"Model\"" + lineSep
            + "  version=\"1.0\"" + lineSep
            + ">" + lineSep
            + "  <!-- a comment" + lineSep
            + "       spread across multiple lines -->" + lineSep
            + "<Doc>" + lineSep
            + "  This model is the XOM Meta Model.  It is the specification of the model used" + lineSep
            + "  to define new XML-based models.  It is also an instance of itself." + lineSep
            + "" + lineSep
            + "</Doc>" + lineSep
            + "" + lineSep
            + "<Element type=\"Model\">" + lineSep
            + "    <Doc>" + lineSep
            + "       Contains a \"single\" apostrope '." + lineSep
            + "    </Doc>" + lineSep
            + "" + lineSep
            + "    <Attribute name=\"name\" required=\"true\"/>" + lineSep
            + "    <Attribute name=\"dtdName\"/>" + lineSep
            + "</Element>" + lineSep
            + "</Model>";
        DOMWrapper def = xmlParser.parse(xml);
        assertNotNull(def);
        final MetaDef.Model model = new MetaDef.Model(def);
        assertNotNull(model);

        Location location = model.getLocation();
        assertEquals("Model", model.getName());
        assertEquals(1, location.getStartLine());
        assertEquals(1, location.getStartColumn());
        assertEquals(25, location.getEndLine());
        assertEquals(9, location.getEndColumn());

        // Model only has one child, Element. Doc is Cdata, so becomes an
        // attribute.
        NodeDef[] children = model.getChildren();
        assertEquals(1, children.length);
        final NodeDef element = children[0];
        assertEquals("Element", element.getName());
        location = element.getLocation();
        assertEquals(17, location.getStartLine());
        assertEquals(1, location.getStartColumn());
        assertEquals(24, location.getEndLine());
        assertEquals(11, location.getEndColumn());

        children = element.getChildren();
        assertEquals(4, children.length);
        NodeDef attribute = children[1];
        assertEquals("Attribute", attribute.getName());
        location = attribute.getLocation();
        assertEquals(23, location.getStartLine());
        assertEquals(5, location.getStartColumn());
        assertEquals(23, location.getEndLine());
        assertEquals(32, location.getEndColumn());
    }
}

// End XomTest.java
