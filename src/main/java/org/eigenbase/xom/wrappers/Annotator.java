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

import org.eigenbase.xom.*;
import org.w3c.dom.Node;

import java.util.*;
import java.io.PrintWriter;

/**
 * Quick and dirty XML parser that finds the precise start and end
 * position of all nodes in a document. Also finds all line endings, so
 * that character offsets can be converted to line/column positions.
 *
 * @author jhyde
 */
public class Annotator {
    private final List/*<LocInfo>*/ locInfoList = new ArrayList();
    private int[] lineStartPositions;
    private final String xml;
    private final Map/*<DOMWrapper, LocInfo>*/ wrapperLocMap =
        new HashMap();
    private final Map/*<Node, LocInfo>*/ nodeLocMap = new HashMap();
    private int seq; // workspace for populateMap

    /**
     * Creates an Annotator.
     *
     * <p>For testing purposes, <code>wrapper</code> may be null. Parses the XML
     * but does not build the mapping from location information to DOM nodes.
     *
     * @param xml XML source string
     * @param def Wrapper around root DOM node
     */
    Annotator(String xml, DOMWrapper def) {
        this.xml = xml;
        parse(xml);
        if (def != null) {
            seq = 0;
            populateMap(def);
            assert this.nodeLocMap.size() == this.wrapperLocMap.size();
        }
    }

    public Location getLocation(DOMWrapper wrapper) {
        LocInfo location0 = (LocInfo) wrapperLocMap.get(wrapper);
        if (location0 == null) {
            location0 = (Annotator.LocInfo)
                nodeLocMap.get(((W3CDOMWrapper) wrapper).node);
            if (location0 == null) {
                return null;
            }
        }
        final LocInfo location = location0;
        return new Location() {
            public int getStartLine() {
                return getLine(getStartPos()) + 1;
            }

            public int getStartColumn() {
                return getCol(getStartPos()) + 1;
            }

            public int getStartPos() {
                return location.startTagStartPos;
            }

            public int getEndLine() {
                return getLine(getEndPos()) + 1;
            }

            public int getEndColumn() {
                return getCol(getEndPos()) + 1;
            }

            public int getEndPos() {
                return location.endTagEndPos >= 0
                    ? location.endTagEndPos
                    : location.startTagEndPos;
            }

            public String getText(boolean headOnly) {
                return location.getText(headOnly);
            }

            public String toString() {
                return location.toString(Annotator.this);
            }
        };
    }

    /**
     * Returns the list of LocInfo. For testing.
     *
     * @return list of LocInfo.
     */
    List getLocInfoList() {
        return locInfoList;
    }

    // enum State
    private static final int
        STATE_NORMAL = 0,
        STATE_TAG = 1,
        STATE_ENDTAG = 2,
        STATE_QUOT = 3,
        STATE_APOS = 4,
        STATE_COMMENT = 5,
        STATE_CDATA = 6;

    void parse(String s)
    {
        final ArrayStack/*<LocInfo>*/ lockInfoStack = new ArrayStack();
        final List lineStartPositions = new ArrayList();
        int state = STATE_NORMAL;
        final int count = s.length();
        int i = 0;
        int last = 0;
        lineStartPositions.add(new Integer(i));
        lockInfoStack.push(null);
        LocInfo location = null;
        loop:
        while (i < count) {
            final char c = s.charAt(i);
            switch (c) {
            case '<':
                stateSwitch:
                switch (state) {
                case STATE_NORMAL:
                    if (i > last) {
                        // Unlike other node types, we create the LocInfo
                        // at the end of the element. No need to add the node
                        // to the stack, because we'd just remove it again.
                        LocInfo loc2 =
                            new LocInfo(locInfoList.size(), TYPE_TEXT, last);
                        loc2.endTagEndPos = i;
                        locInfoList.add(loc2);
                    }
                    if (i + 1 < count) {
                        final char c1 = s.charAt(i + 1);
                        switch (c1) {
                        case '/':
                            // ^</Tag>
                            state = STATE_ENDTAG;
                            assert location != null;
                            break stateSwitch;
                        case '?':
                            // ^<?xml ... ?>
                            location =
                                new LocInfo(
                                    locInfoList.size(),
                                    TYPE_PROCESSING_INSTRUCTION, i);
                            locInfoList.add(location);
                            state = STATE_TAG;
                            i += "<?".length();
                            continue loop;
                        case '!':
                            if (s.startsWith("--", i + 2)) {
                                // ^<!--
                                location =
                                    new LocInfo(
                                        locInfoList.size(),
                                        TYPE_COMMENT, i);
                                locInfoList.add(location);
                                state = STATE_COMMENT;
                                i += "<!--".length();
                                continue loop;
                            }
                            if (s.startsWith("[CDATA[", i + 2)) {
                                // ^<![CDATA[
                                location =
                                    new LocInfo(
                                        locInfoList.size(),
                                        TYPE_CDATA_SECTION, i);
                                locInfoList.add(location);
                                state = STATE_CDATA;
                                i += "<![CDATA[".length();
                                continue loop;
                            }
                            break;
                        }
                    }
                    // Start of an element,
                    // ^<Tag a1=v a2=v>
                    // Don't push until we see end of the head tag <Tag ... ^>
                    state = STATE_TAG;
                    location = new LocInfo(locInfoList.size(), TYPE_ELEMENT, i);
                    locInfoList.add(location);
                    ++i;
                    continue loop;
                }
                break;

            case '>':
                switch (state) {
                case STATE_TAG:
                    ++i;
                    assert location != null;
                    switch (location.type) {
                    case TYPE_PROCESSING_INSTRUCTION:
                        // <? ... ?^>
                    case TYPE_CDATA_SECTION:
                        // <![CDATA[ ... ]]^>
                    case TYPE_COMMENT:
                        // <!-- ... --^>
                        location.endTagEndPos = i;
                        location = (LocInfo) lockInfoStack.peek();
                        break;
                    default:
                        // <Tag^>
                        location.startTagEndPos = i;
                        lockInfoStack.push(location);
                        break;
                    }
                    last = i;
                    state = STATE_NORMAL;
                    continue loop;

                case STATE_ENDTAG:
                    // </Tag^>
                    ++i;
                    assert location != null;
                    location.endTagEndPos = i;
                    try {
                        location = (LocInfo) lockInfoStack.pop();
                    } catch (IndexOutOfBoundsException e) {
                        throw new RuntimeException(
                            "i=" + i + ", xml=" + xml.substring(i)
                                + ", nodeList=" + locInfoList,
                            e);
                    }
                    last = i;
                    state = STATE_NORMAL;
                    continue loop;
                }
                break;

            case '/':
                switch (state) {
                case STATE_TAG:
                    ++i;
                    if (i < count && s.charAt(i) == '>') {
                        // <Tag a1=v1 a2=v2 ^/>
                        ++i;
                        location.endTagEndPos = i;
                        // no need to pop; we never pushed when we saw '<'
                        location = (LocInfo) lockInfoStack.peek();
                        last = i;
                        state = STATE_NORMAL;
                    }
                    continue loop;
                }
                break;

            case ']':
                switch (state) {
                case STATE_CDATA:
                    if (s.startsWith("]>", i + 1)) {
                         // <![CDATA[ ... ^]]>
                        state = STATE_NORMAL;
                        i += "]]>".length();
                        location.endTagEndPos = i;
                        location = (LocInfo) lockInfoStack.peek();
                        last = i;
                        continue loop;
                    }
                }
                break;

            case '-':
                switch (state) {
                case STATE_COMMENT:
                    if (s.startsWith("->", i + 1)) {
                        // <!-- xxxxx^-->
                        i += "-->".length();
                        location.endTagEndPos = i;
                        last = i;
                        location = (LocInfo) lockInfoStack.peek();
                        state = STATE_NORMAL;
                        continue loop;
                    }
                }
                break;

            case '\r':
                ++i;
                if (i < count && s.charAt(i) == '\n') {
                    // only count windows line ending CR LF as one line
                    ++i;
                }
                lineStartPositions.add(new Integer(i));
                continue loop;

            case '\n':
                ++i;
                lineStartPositions.add(new Integer(i));
                continue loop;

            case '\'':
                switch (state) {
                case STATE_APOS:
                    // a='xxx^'
                    state = STATE_TAG;
                    break;
                case STATE_TAG:
                    // a=^'xxx'
                    state = STATE_APOS;
                    break;
                case STATE_QUOT:
                    // a="doesn^'t matter"
                default:
                    break;
                }
                break;

            case '"':
                switch (state) {
                case STATE_QUOT:
                    // a="xxx^"
                    state = STATE_TAG;
                    break;
                case STATE_TAG:
                    // a=^"xxx"
                    state = STATE_QUOT;
                    break;
                case STATE_APOS:
                    // a='doesn^"t matter'
                default:
                    break;
                }
                break;
            }

            ++i;
        }
        this.lineStartPositions = new int[lineStartPositions.size()];
        for (int j = 0; j < lineStartPositions.size(); j++) {
            this.lineStartPositions[j] =
                ((Integer) lineStartPositions.get(j)).intValue();
        }
    }

    private void populateMap(DOMWrapper def)
    {
        final int defType = def.getType();
        LocInfo location;
        while (true) {
            location = (LocInfo) locInfoList.get(seq++);
            if (defType == DOMWrapper.ELEMENT
                && location.type == TYPE_ELEMENT)
            {
                break;
            }
            if (defType == DOMWrapper.CDATA
                && location.type == TYPE_TEXT)
            {
                break;
            }
            if (seq >= locInfoList.size()) {
                return;
            }
        }
        wrapperLocMap.put(def, location);
        nodeLocMap.put(((W3CDOMWrapper) def).node, location);
        final DOMWrapper[] elementChildren = def.getElementChildren();
        for (int i = 0; i < elementChildren.length; i++) {
            DOMWrapper domWrapper = elementChildren[i];
            populateMap(domWrapper);
        }
    }

    /**
     * Returns the line that a character position falls on. The first line in a
     * document is numbered 0.
     *
     * @param pos Character position
     * @return Line (starting from 0)
     */
    int getLine(int pos)
    {
        int index = Arrays.binarySearch(lineStartPositions, pos);
        if (index >= 0) {
            return index;
        } else {
            return -2 - index;
        }
    }

    /**
     * Returns the column that a character position falls on. The first column
     * in a line is numbered 0.
     *
     * @param pos Character position
     * @return column (starting from 0)
     */
    int getCol(int pos)
    {
        int index = Arrays.binarySearch(lineStartPositions, pos);
        if (index >= 0) {
            return 0;
        } else {
            index = -2 - index;
            return pos - lineStartPositions[index];
        }
    }

    void list(PrintWriter pw)
    {
        for (int i = 0; i < locInfoList.size(); i++) {
            LocInfo location = (LocInfo) locInfoList.get(i);
            pw.println(
                location.seq + ": " + location.toString(this) + " ["
                    + location.getText(xml) + "]");
        }
        pw.flush();
    }

    // enum Type
    private static final int
        TYPE_ELEMENT = Node.ELEMENT_NODE,
        TYPE_PROCESSING_INSTRUCTION = Node.PROCESSING_INSTRUCTION_NODE,
        TYPE_COMMENT = Node.COMMENT_NODE,
        TYPE_CDATA_SECTION = Node.CDATA_SECTION_NODE,
        TYPE_TEXT = Node.TEXT_NODE;

    class LocInfo {
        /** Sequence in document, ordered by start position (prefix order) */
        final int seq;
        /** Node type, typically {@link Node#ELEMENT_NODE}. */
        final int startTagStartPos;
        final int type;
        int startTagEndPos = -1; // -1 if entity is a single tag
        int endTagEndPos = -1;

        /**
         * Creates a LocInfo.
         *
         * @param seq Sequence number in document
         * @param nodeType Node type, typically {@link Node#ELEMENT_NODE}.
         * @param startTagStartPos Position of start of element
         */
        LocInfo(int seq, int nodeType, int startTagStartPos) {
            this.seq = seq;
            this.type = nodeType;
            this.startTagStartPos = startTagStartPos;
        }

        public String toString(Annotator annotator) {
            return "line " + annotator.getLine(startTagStartPos)
                + ", column " + annotator.getCol(startTagStartPos);
        }

        /**
         * Returns the fragment of source XML that this node encompasses.
         *
         * @param xml Whole source XML
         * @return fragment of source XML
         */
        public String getText(String xml) {
            return xml.substring(
                startTagStartPos,
                endTagEndPos >= 0 ? endTagEndPos
                    : xml.length());
        }

        /**
         * Returns the fragment of source XML corresponding to the head tag
         * of this element, if this is an element, otherwise the whole node.
         *
         * @param xml Whole source XML
         * @return fragment of source XML
         */
        public String getHeadText(String xml) {
            return xml.substring(
                startTagStartPos,
                startTagEndPos >= 0 ? startTagEndPos
                    : endTagEndPos >= 0 ? endTagEndPos
                        : xml.length());
        }

        public String toString() {
            return getHeadText(xml);
        }

        /**
         * Returns the text of this location. Specification as for
         * {@link org.eigenbase.xom.Location#getText(boolean)}.
         *
         * @param headOnly Whether to return only the head of elements
         * @return Source text underlying a location
         */
        public String getText(boolean headOnly) {
            return xml.substring(
                startTagStartPos,
                headOnly && startTagEndPos >= 0
                    ? startTagEndPos
                    : endTagEndPos >= 0
                    ? endTagEndPos
                    : xml.length());
        }
    }

    /**
     * Similar to {@link Stack} but based on {@link ArrayList} instead of
     * {@link Vector}, and therefore more efficient.
     */
    private static class ArrayStack extends ArrayList {
        public final void push(Object t)
        {
            if (false) System.out.println(size() + " push [" + t + "]");
            add(t);
        }

        public final Object peek()
        {
            return get(size() - 1);
        }

        public final Object pop()
        {
            final int index = size() - 1;
            Object t = remove(index);
            if (false) System.out.println(size() + " pop  [" + t + "]");
            return get(index - 1);
        }
    }
}

// End Annotator.java
