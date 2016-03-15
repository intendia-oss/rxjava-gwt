package java.util.concurrent;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    private final Map<K, V> backingMap;

    public ConcurrentHashMap() {
        this.backingMap = new HashMap<K, V>();
    }

    public ConcurrentHashMap(int initialCapacity) {
        this.backingMap = new HashMap<K, V>(initialCapacity);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this.backingMap = new HashMap<K, V>(initialCapacity, loadFactor);
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> t) {
        this.backingMap = new HashMap<K, V>(t);
    }

    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            return put(key, value);
        } else {
            return get(key);
        }
    }

    public boolean remove(Object key, Object value) {
        if (containsKey(key) && get(key).equals(value)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) throw new NullPointerException();
        else if (containsKey(key) && get(key).equals(oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    public V replace(K key, V value) {
        if (value == null) throw new NullPointerException();
        else if (containsKey(key)) return put(key, value);
        else return null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) throw new NullPointerException();
        return backingMap.containsKey(key);
    }

    @Override
    public V get(Object key) {
        if (key == null) throw new NullPointerException();
        return backingMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) throw new NullPointerException();
        return backingMap.put(key, value);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) throw new NullPointerException();
        return backingMap.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        if (key == null) throw new NullPointerException();
        return backingMap.remove(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return backingMap.entrySet();
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public Enumeration<V> elements() {
        return Collections.enumeration(values());
    }

    public Enumeration<K> keys() {
        return Collections.enumeration(keySet());
    }
}
