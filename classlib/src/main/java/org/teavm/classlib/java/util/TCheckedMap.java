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
import org.teavm.classlib.java.lang.TClass;
import org.teavm.classlib.java.lang.TObject;

/**
 *
 * @author Alexey Andreev
 */
class TCheckedMap<K, V> implements TMap<K, V> {
    private TMap<K, V> innerMap;
    private TClass<K> keyType;
    private TClass<V> valueType;

    public TCheckedMap(TMap<K, V> innerMap, TClass<K> keyType, TClass<V> valueType) {
        this.innerMap = innerMap;
        this.keyType = keyType;
        this.valueType = valueType;
    }
    

    @Override
    public boolean replace(K key, V value, V newValue) {
        if (containsKey(key) && TObjects.equals(get(key), value)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V replace(K key, V value) {
        if (containsKey(key)) {
            return put(key, value);
        } else {
            return null;
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V v = get(key);
        if (v == null) {
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                put(key, newValue);
            }
            return newValue;
        }
        return v;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V v = get(key);
        if (v != null) {
            V oldValue = v;
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                return put(key, newValue);
            } else {
                return remove(key);
            }
        }
        return v;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V oldValue = get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        if (oldValue != null) {
            if (newValue != null) {
                return put(key, newValue);
            } else {
                return remove(key);
            }
        } else if (newValue != null) {
            return put(key, newValue);
        } else {
            return null;
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value
                : remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            return remove(key);
        } else {
            return put(key, newValue);
        }
    }

    @Override
    public int size() {
        return innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return innerMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return innerMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return innerMap.put(keyType.cast(TObject.wrap(key)), valueType.cast(TObject.wrap(value)));
    }

    @Override
    public V remove(Object key) {
        return innerMap.remove(key);
    }

    @Override
    public void putAll(TMap<? extends K, ? extends V> m) {
        m = new THashMap<>(m);
        for (TIterator<? extends Entry<? extends K, ? extends V>> iter = m.entrySet().iterator(); iter.hasNext();) {
            Entry<? extends K, ? extends V> entry = iter.next();
            keyType.cast(TObject.wrap(entry.getKey()));
            valueType.cast(TObject.wrap(entry.getValue()));
        }
        innerMap.putAll(m);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public TSet<K> keySet() {
        return new TCheckedSet<>(innerMap.keySet(), keyType);
    }

    @Override
    public TCollection<V> values() {
        return new TCheckedCollection<>(innerMap.values(), valueType);
    }

    @Override
    public TSet<TMap.Entry<K, V>> entrySet() {
        return innerMap.entrySet();
    }
}
