/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.google.jstestdriver.directoryscanner;

/**
 * Signals an error condition during a build
 */
public class DirectoryScannerException extends RuntimeException {

    public DirectoryScannerException() {
        super();
    }

    public DirectoryScannerException(String message) {
        super(message);
    }

    public DirectoryScannerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an exception with the given exception as a root cause.
     *
     * @param cause The exception that might have caused this one.
     *              Should not be <code>null</code>.
     */
    public DirectoryScannerException(Throwable cause) {
        super(cause.toString());
    }
}
