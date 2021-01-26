/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.security.exception;


public class UnauthenticatedException extends AuthorizationException {

    /**
     * Creates a new UnauthenticatedException.
     */
    public UnauthenticatedException() {
        super();
    }

    /**
     * Constructs a new UnauthenticatedException.
     *
     * @param message the reason for the exception
     */
    public UnauthenticatedException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthenticatedException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public UnauthenticatedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnauthenticatedException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
