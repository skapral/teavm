/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author Alexey Andreev
 * @param <K>
 * @param <V>
 */
public interface TMap<K, V> {
    interface Entry<K1, V1> {
        K1 getKey();

        V1 getValue();

        V1 setValue(V1 value);
    }

    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);

    V put(K key, V value);

    V remove(Object key);
    
    
    boolean replace(K k, V v, V v1);

    V replace(K k, V v);

    V computeIfAbsent(K k, Function<? super K, ? extends V> fnctn);

    V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> bf);

    V compute(K k, BiFunction<? super K, ? super V, ? extends V> bf);

    V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> bf);

    void putAll(TMap<? extends K, ? extends V> m);

    void clear();

    TSet<K> keySet();

    TCollection<V> values();

    TSet<Entry<K, V>> entrySet();
}
