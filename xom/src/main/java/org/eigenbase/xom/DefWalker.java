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

import java.lang.reflect.Array;
import java.util.Vector;

/**
 * DefWalker is a class designed to help users of plugin elements and elements
 * with content type ANY.  It walks through an array of ElementDef, searching
 * for and returning portions as the correct types.
 */
public class DefWalker {

    private NodeDef[] defs;
    private int pos;

    /**
     * Construct a DefWalker, attaching it to a NodeDef array and
     * specifying a PrintWriter to display error messages for later
     * consumption.
     * @param defs a NodeDef array to walk.  All returned objects
     * come from this array.
     */
    public DefWalker(NodeDef[] defs)
    {
        this.defs = defs;
        pos = 0;
    }

    /**
     * Returns the next node in the defs array, but only if it matches
     * the provided class <i>elemType</i>.
     * @param elemType the Class of NodeDef to expect.  This class will
     * always be assignable from the returned object.
     * @throws XOMException if there are no more nodes in the defs
     * array or if the next node is of an incorrect type.
     */
    public NodeDef expect(Class elemType)
        throws XOMException
    {
        if(pos >= defs.length)
            throw new XOMException("Expecting a Node of type "
                                      + elemType.getName() + " but no "
                                      + "Nodes remain.");
        if(!(elemType.isAssignableFrom(defs[pos].getClass())))
            throw new XOMException("Expecting a Node of type "
                                      + elemType.getName() + " but "
                                      + "found a Node of type "
                                      + defs[pos].getClass().getName());
        return defs[pos++];
    }

    /**
     * Returns a portion of the remaining nodes in the defs array as an
     * array.  All nodes in the array will be of the specified class
     * <i>elemType</i>.  The nodes are returned as a generic NodeDef[]
     * array and may need to be explicitly converted to an array of the
     * appropriate type by the caller.
     * @param elemType the Class of NodeDef to expect and return.  This
     * class will always be assignable from each returned object in the
     * array.
     */
    public NodeDef[] expectArray(Class elemType)
    {
        Vector found = new Vector();
        while(pos < defs.length &&
              elemType.isAssignableFrom(defs[pos].getClass()))
            found.addElement(defs[pos++]);

        NodeDef[] ret = new NodeDef[found.size()];
        for(int i=0; i<found.size(); i++)
            ret[i] = (NodeDef)(found.elementAt(i));
        return ret;
    }

    /**
     * Returns a portion of the remaining nodes in the defs array as an
     * array.  All nodes in the array will be of the specified class
     * <i>elemType</i>.  The nodes are in an array of the specified type,
     * which will be returned as an object (which must be cast to the
     * appropriate array type by the caller when needed.
     * @param elemType the Class of NodeDef to expect and return.  This
     * class will always be assignable from each returned object in the
     * array.
     */
    public Object expectTypeArray(Class elemType)
    {
        Vector found = new Vector();
        while(pos < defs.length &&
              elemType.isAssignableFrom(defs[pos].getClass()))
            found.addElement(defs[pos++]);

        Object ret = Array.newInstance(elemType, found.size());
        for(int i=0; i<found.size(); i++)
            Array.set(ret, i, found.elementAt(i));
        return ret;
    }
}


// End DefWalker.java
