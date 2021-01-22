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

package cc.jweb.boot.utils.lang.function;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, W> {
    void accept(T t, U u, W w);
    default TriConsumer<T, U, W> andThen(TriConsumer<? super T, ? super U, ? super W> after) {
        Objects.requireNonNull(after);
        return (l, r, w) -> {
            accept(l, r, w);
            after.accept(l, r, w);
        };
    }
}
