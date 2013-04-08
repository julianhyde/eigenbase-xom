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
 * A <code>TextDef</code> represents piece of textual data in an XML document.
 * Free text (such as <code>Some text</code>) is represented by an actual
 * <code>TextDef</code>; comments (such as <code>&lt;-- a comment --&gt;</code>)
 * by derived class {@link CommentDef}; and CDATA sections (such as
 * <code>&lt;![CDATA[Some text]]&gt;</code>) by derived class {@link CdataDef}.
 *
 * @author jhyde
 */
public class TextDef implements NodeDef {

    public String s;

    /**
     * Whether to print the data as is -- never quote as a CDATA
     * section. Useful if the fragment contains a valid XML string.
     */
    boolean asIs;

    private Location location;

    public TextDef()
    {
        this(null, false, null);
    }

    public TextDef(String s)
    {
        this(s, false, null);
    }

    public TextDef(String s, boolean asIs)
    {
        this(s, asIs, null);
    }

    public TextDef(String s, boolean asIs, Location location)
    {
        this.s = s;
        this.asIs = asIs;
        this.location = location;
    }

    public TextDef(org.eigenbase.xom.DOMWrapper _def)
        throws org.eigenbase.xom.XOMException
    {
        switch (_def.getType()) {
        case DOMWrapper.FREETEXT:
        case DOMWrapper.CDATA:
        case DOMWrapper.COMMENT:
            break;
        default:
            throw new XOMException(
                "cannot make CDATA/PCDATA element from a " + _def.getType());
        }
        this.s = _def.getText();
        this.location = _def.getLocation();
    }

    // override ElementDef
    public String getName()
    {
        return null;
    }

    // override ElementDef
    public String getText()
    {
        return s;
    }

    // implement NodeDef
    public NodeDef[] getChildren()
    {
        return XOMUtil.emptyNodeArray;
    }

    // implement NodeDef
    public DOMWrapper getWrapper()
    {
        return null;
    }

    // implement NodeDef
    public int getType()
    {
        return DOMWrapper.FREETEXT;
    }

    // implement NodeDef
    public void display(PrintWriter pw, int indent)
    {
        pw.print(s);
    }

    // override NodeDef
    public void displayXML(XMLOutput out, int indent)
    {
        if (out.getIgnorePcdata()) {
            return;
        }
        out.beginNode();
        if (asIs) {
            out.print(s);
        } else {
            boolean quote = true;
            out.cdata(s, quote);
        }
    }

    // implement NodeDef
    public Location getLocation()
    {
        return location;
    }
}

// End TextDef.java
