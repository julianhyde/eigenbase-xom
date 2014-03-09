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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for XOM module.
 */
public class XomTest {
    @Test public void testFoo() throws XOMException {
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
        assertThat(def, notNullValue());
        final MetaDef.Model model = new MetaDef.Model(def);
        assertThat(model, notNullValue());

        Location location = model.getLocation();
        assertThat(model.getName(), equalTo("Model"));
        assertThat(location.getStartLine(), equalTo(1));
        assertThat(location.getStartColumn(), equalTo(1));
        assertThat(location.getEndLine(), equalTo(25));
        assertThat(location.getEndColumn(), equalTo(9));

        // Model only has one child, Element. Doc is Cdata, so becomes an
        // attribute.
        NodeDef[] children = model.getChildren();
        assertThat(children.length, equalTo(1));
        final NodeDef element = children[0];
        assertThat(element.getName(), equalTo("Element"));
        location = element.getLocation();
        assertThat(location.getStartLine(), equalTo(17));
        assertThat(location.getStartColumn(), equalTo(1));
        assertThat(location.getEndLine(), equalTo(24));
        assertThat(location.getEndColumn(), equalTo(11));

        children = element.getChildren();
        assertThat(children.length, equalTo(4));
        NodeDef attribute = children[1];
        assertThat(attribute.getName(), equalTo("Attribute"));
        location = attribute.getLocation();
        assertThat(location.getStartLine(), equalTo(23));
        assertThat(location.getStartColumn(), equalTo(5));
        assertThat(location.getEndLine(), equalTo(23));
        assertThat(location.getEndColumn(), equalTo(32));
    }
}

// End XomTest.java
