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

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility functions for the <code>org.eigenbase.xom</code> and
 * <code>org.eigenbase.xom.wrappers</code> packages.
 *
 * @author jhyde
 */
public abstract class XOMUtil extends XMLUtil {

    static final NodeDef[] emptyNodeArray = new NodeDef[0];

    /**
     * When the compiler is complaining that you are not using a variable, just
     * call one of these routines with it.
     */
    public static void discard(boolean b) {
    }

    public static void discard(byte b) {
    }

    public static void discard(char c) {
    }

    public static void discard(double d) {
    }

    public static void discard(float d) {
    }

    public static void discard(int i) {
    }

    public static void discard(long l) {
    }

    public static void discard(Object o) {
    }

    public static void discard(short s) {
    }

    /**
     * Converts the first letter of <code>name</code> to upper-case.
     */
    static String capitalize(String name) {
        if (name == null || name.length() < 1) {
            return name;
        }
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    /**
     * Adds an object to the end of an array.  The resulting array is of the
     * same type (e.g. <code>String[]</code>) as the input array.
     */
    public static Object[] addElement(Object[] a, Object o)
    {
        Class clazz = a.getClass().getComponentType();
        Object[] a2 = (Object[]) Array.newInstance(clazz, a.length + 1);
        System.arraycopy(a, 0, a2, 0, a.length);
        a2[a.length] = o;
        return a2;
    }

    /**
     * Concatenates two arrays.  The resulting array is of the
     * same type (e.g. <code>String[]</code>) as the first array.
     */
    public static Object[] concatenate(Object[] a0, Object[] a1)
    {
        Class clazz = a0.getClass().getComponentType();
        Object[] a2 = (Object[]) Array.newInstance(
            clazz, a0.length + a1.length);
        System.arraycopy(a0, 0, a2, 0, a0.length);
        System.arraycopy(a1, 0, a2, a0.length, a1.length);
        return a2;
    }

    /**
     * Adds a set of children to an object, using its best guess as to where to
     * put them.
     */
    public static void addChildren(ElementDef parent, NodeDef[] children)
        throws XOMException
    {
        if (parent instanceof GenericDef) {
            GenericDef xmlGeneric = (GenericDef) parent;
            for (int i = 0; i < children.length; i++) {
                xmlGeneric.addChild(children[i]);
            }
        } else if (parent instanceof Any) {
            Any any = (Any) parent;
            NodeDef[] currentChildren = any.getChildren();
            if (currentChildren == null) {
                if (children instanceof ElementDef[]) {
                    currentChildren = new ElementDef[0];
                } else {
                    currentChildren = new NodeDef[0];
                }
            }
            NodeDef[] newChildren = (NodeDef[]) concatenate(
                currentChildren, children);
            any.setChildren(newChildren);
        } else {
            // Use reflection. We presume that the children are stored in the
            // first array field.
            Field field = null;
            Field[] fields = parent.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType().isArray()) {
                    field = fields[i];
                    break;
                }
            }
            if (field == null) {
                throw new XOMException(
                    "cannot add field to " + parent.getClass() +
                    ": it has no array field");
            }
            try {
                Object[] a = (Object[]) field.get(parent);
                Object[] b = concatenate(a, children);
                field.set(parent, b);
            } catch (IllegalAccessException e) {
                throw new XOMException(e, "in XOMUtil.getChildren");
            }
        }
    }

    public static void addChild(ElementDef parent, ElementDef child)
        throws XOMException
    {
        addChildren(parent, new ElementDef[] {child});
    }

    public static void addChild(ElementDef parent, NodeDef child)
        throws XOMException
    {
        addChildren(parent, new NodeDef[] {child});
    }

    /**
     * Creates a {@link Parser} of the default parser type.
     */
    public static Parser createDefaultParser() throws XOMException
    {
        String className = "org.eigenbase.xom.wrappers.JaxpDOMParser";
        try {
            Class clazz = Class.forName(className);
            return (Parser) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new XOMException(e, "Error while creating xml parser '" + className + "'");
        } catch (IllegalAccessException e) {
            throw new XOMException(e, "Error while creating xml parser '" + className + "'");
        } catch (InstantiationException e) {
            throw new XOMException(e, "Error while creating xml parser '" + className + "'");
        } catch (VerifyError e) {
            throw new XOMException(
                    e, "Error while creating xml parser '" + className + "' " +
                    "(If you are running Weblogic 6.1, try putting " +
                    "xml-apis.jar and xercesImpl.jar BEFORE weblogic.jar " +
                    "on CLASSPATH)");
        }
    }

    /** * @see #makeParser */
    static final int MSXML = 1;
    /** * @see #makeParser */
    static final int XERCES = 2;

    /**
     * Creates a parser of given type.
     *
     * @param parserType valid values are {@link #MSXML} and {@link #XERCES}.
     */
    static Parser makeParser(
        int parserType, boolean usesPlugins, String fileDirectory,
        String dtdName, String docType) throws XOMException
    {
        try {
            switch (parserType) {
            case MSXML:
                if (usesPlugins) {
                    // Use reflection to call
                    //   MSXMLWrapper.createParser();
                    Class clazz = Class.forName(
                        "org.eigenbase.xom.wrappers.MSXMLWrapper");
                    Method method = clazz.getDeclaredMethod(
                        "createParser", new Class[] {});
                    return (Parser) method.invoke(null, new Object[] {});
                } else {
                    // Use reflection to call
                    //   MSXMLWrapper.createParser(docType, dtdPath);
                    File dtdPath = new File(fileDirectory, dtdName);
                    Class clazz = Class.forName(
                        "org.eigenbase.xom.wrappers.MSXMLWrapper");
                    Method method = clazz.getDeclaredMethod(
                        "createParser", new Class[] {
                            String.class, String.class});
                    return (Parser) method.invoke(null, new Object[] {
                        docType, dtdPath});
                }
            case XERCES:
                return new org.eigenbase.xom.wrappers.XercesDOMParser(
                    !usesPlugins);
            default:
                throw new XOMException("Unknown parser type: " + parserType);
            }
        } catch (ClassNotFoundException e) {
            throw new XOMException(e, "while creating xml parser");
        } catch (IllegalAccessException e) {
            throw new XOMException(e, "while creating xml parser");
        } catch (NoSuchMethodException e) {
            throw new XOMException(e, "while creating xml parser");
        } catch (InvocationTargetException e) {
            throw new XOMException(e, "while creating xml parser");
        }
    }

    /**
     * Returns the first member of an array of objects which is an instance of
     * a given class, or null if there is no such.
     */
    public static Object getFirstInstance(Object[] a, Class clazz)
    {
        for (int i = 0; i < a.length; i++) {
            if (clazz.isInstance(a[i])) {
                return a[i];
            }
        }
        return null;
    }

    public static String wrapperToXml(DOMWrapper wrapper, boolean ignorePcdata)
    {
        try {
            NodeDef node;
            switch (wrapper.getType()) {
            case DOMWrapper.ELEMENT:
                node = new WrapperElementDef(wrapper,null,null);
                break;
            case DOMWrapper.CDATA:
                node = new CdataDef(wrapper);
                break;
            case DOMWrapper.FREETEXT:
                node = new TextDef(wrapper);
                break;
            case DOMWrapper.COMMENT:
                node = new CommentDef(wrapper);
                break;
            default:
                throw new Error(
                "unknown node type " + wrapper.getType() +
                " while converting node to xml");
            }
            StringWriter sw = new StringWriter();
            XMLOutput out = new XMLOutput(sw);
            out.setIgnorePcdata(ignorePcdata);
            out.setGlob(true);
            node.displayXML(out, 0);
            return sw.toString();
        } catch (XOMException e) {
            throw new Error(
                "[" + e.toString() + "] while converting node to xml");
        }
    }
}


// End XOMUtil.java
