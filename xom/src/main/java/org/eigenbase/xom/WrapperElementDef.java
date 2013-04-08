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
 * <code>WrapperElementDef</code> is an {@link ElementDef} which retains the
 * underlying XML {@link DOMWrapper}. It is used when there is no specific
 * class for this tag.
 *
 * @author jhyde
 */
public class WrapperElementDef extends ElementDef
{
    DOMWrapper _def;
    Class enclosure;
    String prefix;

    public WrapperElementDef(
        DOMWrapper def, Class enclosure, String prefix)
    {
        this._def = def;
        this.enclosure = enclosure;
        this.prefix = prefix;
    }

    // implement NodeDef
    public void display(PrintWriter out, int indent)
    {
        out.print("<");
        out.print(_def.getTagName());
        String[] attributeKeys = _def.getAttributeNames();
        for (int i = 0; i < attributeKeys.length; i++) {
            String key = attributeKeys[i];
            Object value = _def.getAttribute(key);
            XOMUtil.printAtt(out, key, value.toString());
        }
        NodeDef[] children = getChildren();
        if (children.length == 0) {
            out.print("/>");
        } else {
            for (int i = 0, count = children.length; i < count; i++) {
                children[i].display(out, indent + 1);
            }
            out.print("</");
            out.print(_def.getTagName());
            out.print(">");
        }
    }

    // implement NodeDef
    public void displayXML(XMLOutput out, int indent)
    {
        out.beginNode();
        String tagName = _def.getTagName();
        out.beginBeginTag(tagName);
        String[] attributeKeys = _def.getAttributeNames();
        for (int i = 0; i < attributeKeys.length; i++) {
            String key = attributeKeys[i];
            Object value = _def.getAttribute(key);
            out.attribute(key, value.toString());
        }
        out.endBeginTag(tagName);
        NodeDef[] children = getChildren();
        for (int i = 0, count = children.length; i < count; i++) {
            NodeDef child = children[i];
            child.displayXML(out, indent + 1);
        }
        out.endTag(tagName);
    }

    // implement NodeDef
    public int getType()
    {
        return DOMWrapper.ELEMENT;
    }

    // implement NodeDef
    public String getName()
    {
        return _def.getTagName();
    }

    // implement NodeDef
    public NodeDef[] getChildren()
    {
        try {
            DOMWrapper[] children = _def.getChildren();
            NodeDef[] a = new NodeDef[children.length];
            for (int i = 0; i < a.length; i++) {
                a[i] = ElementDef.constructElement(
                    children[i], enclosure, prefix);
            }
            return a;
        } catch (XOMException e) {
            throw new AssertFailure(e, "in WrapperElementDef.getChildren");
        }
    }

    // implement NodeDef
    public DOMWrapper getWrapper()
    {
        return _def;
    }
}

// End WrapperElementDef.java
