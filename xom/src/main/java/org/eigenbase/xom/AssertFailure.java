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
 * Exception indicating that an assertion has failed.
 *
 * @author jhyde
 * @since 3 December, 2001
 */
public class AssertFailure extends RuntimeException {
    /** Construct an AssertFailure with no message */
    public AssertFailure() {
        super();
    }

    /** Construct an AssertFailure with a simple detail message. */
    public AssertFailure(String s) {
        super(s);
    }

    /** Construct an AssertFailure from an exception.  This indicates an
     * unexpected exception of another type.  We'll fill in the stack trace
     * when printing the message. */
    public AssertFailure(Throwable th) {
        super("unexpected exception:\n" +
              th.fillInStackTrace().toString());
    }

    /** Similar to the previous constructor, except allows a custom message on
     * top of the exception */
    public AssertFailure(Throwable th, String s) {
        super(s + ":\n" +
              th.fillInStackTrace().toString());
    }
}

// End AssertFailure.java
