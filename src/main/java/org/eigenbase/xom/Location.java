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
 * Represents the location of a node within its document.
 *
 * <p>Location is a span from a starting line and column to an ending line
 * and column; or alternatively, from a starting character position to an
 * ending character position.
 *
 * @author jhyde
 */
public interface Location {
    /**
     * Returns the line where this node starts.
     * The first line in the document is 1.
     *
     * @return Line of the start of this node
     */
    int getStartLine();

    /**
     * Returns the column where this node starts.
     * The first column in the document is 1.
     *
     * @return column of the start of this node
     */
    int getStartColumn();

    /**
     * Returns the character position where this node starts.
     * The first character in the document is 0.
     *
     * @return Character position of the start of this node
     */
    int getStartPos();

    /**
     * Returns the line where this node ends.
     * The first line in the document is 1.
     *
     * @return Line of the end of this node
     */
    int getEndLine();

    /**
     * Returns the column where this node ends.
     * The first column in the document is 1.
     *
     * @return column of the end of this node
     */
    int getEndColumn();

    /**
     * Returns the character position where this node ends.
     * The first character in the document is 0.
     *
     * @return Character position of the end of this node
     */
    int getEndPos();

    /**
     * Returns the text of this location.
     *
     * <p>If this location is an element
     * and <code>headOnly</code> is true, returns only the text of the head
     * of the element. For example,
     *
     * <blockquote><pre>
     * &lt;Foo a="1" b="2"&gt;
     *   &lt;Bar c="3"&gt;
     * &lt;/Foo&gt;
     * </pre></blockquote>
     *
     * returns "&lt;Foo a='1' b='2'&gt;&lt;Bar c='3'&gt;&lt;/Foo&gt;"
     * if <code>headOnly</code> is false, "&lt;Foo a='1' b='2'&gt;" if it is
     * true.
     *
     * @param headOnly Whether to return only the head of elements
     * @return Source text underlying a location
     */
    String getText(boolean headOnly);
}

// End Location.java
