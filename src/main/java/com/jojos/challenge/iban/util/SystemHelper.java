package com.jojos.challenge.iban.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * todo: create javadoc
 * <p>
 * Created by karanikasg@gmail.com.
 */
public class SystemHelper {

    /**
     * For a map containing a map for the specified {@code outerMapKey}, adds the provided {@code value} to an
     * inner map for key {@code innerMapKey}. If no inner map is found, one will be created first. The put into
     * the inner map will be a normal {@link ConcurrentMap#put(Object, Object)}.
     *
     * @param map the outer map
     * @param outerMapKey the key to the outer map
     * @param innerMapKey the key for which to add the value in the inner map
     * @param value the value
     * @param <OK> the type of the outer key
     * @param <IK> the type of the inner key
     * @param <V> the type of the value
     * @return the existing value from the inner map.
     * @see Map#put(Object, Object)
     */
    public static <OK, IK, V> V addToContainedMap(ConcurrentMap<OK, Map<IK, V>> map, OK outerMapKey, IK innerMapKey, V value) {
        Map<IK, V> existingMap = map.get(outerMapKey);
        if (existingMap == null) {
            // It might look like checking for null and then creating something means that we need a lock.
            // this isn't the case, as the ACTUAL point of synchronization is the map.putIfAbsent() below.
            // it's perfectly possible to have multiple threads enter this block at the same time.
            // this is fine, as the only "true" value added is added by the putIfAbsent() call.
            // this race will only be an issue in the beginning. Once putIfAbsent() has succeeded,
            // the outer if-statement will always be false, which means we can avoid creating the
            // inner container and calling putIfAbsent() again.
            // This replaces this more legible but slower pattern:
            // map.putIfAbsent(outerMapKey, new ConcurrentHashMap<IK, V>()); // ensure that we have something
            // map.get(outerMapKey).put(innerMapKey, value);
            // See slides 54 and 55 of this presentation regarding the speed of this: http://www.slideshare.net/marakana/effective-java-still-effective-after-all-these-years
            ConcurrentHashMap<IK, V> newMap = new ConcurrentHashMap<>();
            existingMap = map.putIfAbsent(outerMapKey, newMap);
            if (existingMap == null) {
                // we've added a new set
                existingMap = newMap;
            }
        }
        return existingMap.put(innerMapKey, value);
    }

    public static String toString(int[] ints) {
        StringBuilder sb = new StringBuilder();
        for (int value : ints) {
            sb.append(value);
        }
        return sb.toString();
    }

    public static String toString(char[] chars) {
        StringBuilder sb = new StringBuilder();
        for (char value : chars) {
            sb.append(value);
        }
        return sb.toString();
    }

}
