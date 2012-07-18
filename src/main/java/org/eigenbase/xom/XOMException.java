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
 * XOMException extends Exception and provides detailed error messages for
 * xom-specific exceptions.
 */
public class XOMException extends Exception {

    /**
     * Constructs a XOM exception with no message.
     */
    public XOMException()
    {
        super(null,null);
    }

    /**
     * Constructs an exception with a detailed message.
     *
     *@param s - a detailed message describing the specific error
     */
    public XOMException(String s)
    {
        super(s,null);
    }

    /**
     * Constructs an exception based on another exception, so that
     * the exceptions may be chained.
     * @param cause the exception on which this one is based.
     * @param s a message for this portion of the exception.
     */
    public XOMException(Throwable cause, String s)
    {
        super(s,cause);
    }
}

// End XOMException.java
