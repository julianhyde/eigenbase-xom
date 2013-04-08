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


/**
 * A <code>CdataDef</code> represents a CDATA element.  It allows an
 * <code>&lt;Any&gt;</code> element to have mixed children.
 *
 * @author jhyde
 */
public class CdataDef extends TextDef
{
    public CdataDef()
    {
        super();
    }

    public CdataDef(String s)
    {
        super(s);
    }

    public CdataDef(DOMWrapper _def)
        throws org.eigenbase.xom.XOMException
    {
        super(_def);
    }

    // implement NodeDef
    public int getType()
    {
        return DOMWrapper.CDATA;
    }

    // override NodeDef
    public void displayXML(XMLOutput out, int indent)
    {
        out.beginNode();
        out.cdata(s, true);
    }
}


// End CdataDef.java
