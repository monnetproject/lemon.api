/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************
 */
package eu.monnetproject.lemon.conversions.lmf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Static extensions to the collection API to give functional-programming-like
 * operations. If you use this class it is strongly recommended that you static
 * import it. <br/> <br/> Example usage:<br/>
 * <code>
 * if(exists(list, new Criterion<Integer>() {<br/>
 * &nbsp;&nbsp;&nbsp;public boolean f(Integer i) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return i > 10;<br/>
 * &nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;})) {<br/>
 * &nbsp;&nbsp;System.out.println("List contains an elements larger than 10");<br/>
 * }<br/>
 * </code>
 *
 * @author John McCrae
 */
public class CollectionFunctions {

    // do not instantiate
    private CollectionFunctions() {
    }

    /**
     * Returns true if the array contains a specific element. Uses {@code Object.equals()}
     *
     * @param array The array
     * @param element The element
     * @return true if the element is in the array
     */
    public static <E> boolean contains(E[] array, E element) {
        for (int i = 0; i < array.length; i++) {
            if ((element == null && array[i] == null)
                    || (element.equals(array[i]))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the array has an element satisfying the condition
     *
     * @param array The array
     * @param f The function
     */
    public static <E> boolean exists(E[] array, Criterion<E> f) {
        for (int i = 0; i < array.length; i++) {
            if (f.f(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the collection has an element satisfying the condition
     *
     * @param collection The collection
     * @param f The function
     */
    public static <E> boolean exists(Collection<E> collection, Criterion<E> f) {
        for (E e : collection) {
            if (f.f(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return an array containing all elements matching a specific criterion
     *
     * @param array The array
     * @param f The function
     */
    public static <E> E[] filter(E[] array, Criterion<E> f) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; i++) {
            if (f.f(array[i])) {
                list.add(array[i]);
            }
        }
        return (E[]) list.toArray();
    }

    /**
     * Return a collection containing all elements matching a specific criterion
     *
     * @param list The list
     * @param f The function
     */
    public static <E> List<E> filter(List<E> list, Criterion<E> f) {
        final ArrayList<E> list2 = new ArrayList<E>(list);
        final Iterator<E> iter = list2.iterator();
        while (iter.hasNext()) {
            if (!f.f(iter.next())) {
                iter.remove();
            }
        }
        return Collections.unmodifiableList(list2);
    }

    /**
     * Return a collection containing all elements matching a specific criterion
     *
     * @param set The list
     * @param f The function
     */
    public static <E> Set<E> filter(Set<E> set, Criterion<E> f) {
        final Set<E> set2 = new HashSet<E>(set);
        final Iterator<E> iter = set2.iterator();
        while (iter.hasNext()) {
            if (!f.f(iter.next())) {
                iter.remove();
            }
        }
        return Collections.unmodifiableSet(set2);
    }

    /**
     * Return a map containing all entries whose keys match a specific criterion
     *
     * @param map The map
     * @param f The function
     */
    public static <E, F> Map<E, F> filterByKey(Map<E, F> map, Criterion<E> f) {
        final Map<E, F> newMap = new HashMap<E, F>(map);
        final Iterator<Map.Entry<E, F>> iter = newMap.entrySet().iterator();
        while (iter.hasNext()) {
            if (!f.f(iter.next().getKey())) {
                iter.remove();
            }
        }
        return Collections.unmodifiableMap(newMap);
    }

    /**
     * Return a map containing all entries whose values match a specific
     * criterion
     *
     * @param map The map
     * @param f The function
     */
    public static <E, F> Map<E, F> filterByValue(Map<E, F> map, Criterion<F> f) {
        final Map<E, F> newMap = new HashMap<E, F>(map);
        final Iterator<Map.Entry<E, F>> iter = newMap.entrySet().iterator();
        while (iter.hasNext()) {
            if (!f.f(iter.next().getValue())) {
                iter.remove();
            }
        }
        return Collections.unmodifiableMap(newMap);
    }

    /**
     * Find the first element of the array satisfying the condition
     *
     * @param array The array
     * @param f The condition
     * @return The first element or null if no element satisfies the condition
     */
    public static <E> E find(E[] array, Criterion<E> f) {
        for (E e : array) {
            if (f.f(e)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Find the first element of the collection satisfying the condition
     *
     * @param collection The collection
     * @param f The condition
     * @return The first element or null if no element satisfies the condition
     */
    public static <E> E find(Collection<E> collection, Criterion<E> f) {
        for (E e : collection) {
            if (f.f(e)) {
                return e;
            }
        }
        return null;

    }

    /**
     * Fold the array to the left. That is apply f to each element in order
     *
     * @param array The array
     * @param initial The initial value
     * @param f The folding function
     * @return The result
     */
    public static <E, F> F foldLeft(E[] array, F initial, FoldFunction<F, E> f) {
        F value = initial;
        for (E e : array) {
            value = f.f(value, e);
        }
        return value;
    }

    /**
     * Fold the collection to the left. That is apply f to each element in
     * order, e.g., to sum a List&lt;Integer&gt; the following code can be used:
     * <br/>
     * <code>
     *   foldLeft(list, 0, new SAM2() { public Integer f(Integer v1, Integer v2) { return v1 + v2 } });
     * </code>
     *
     * @param array The array
     * @param initial The initial value
     * @param f The folding function
     * @return The result
     */
    public static <E, F> F foldLeft(Collection<E> collection, F initial, FoldFunction<F, E> f) {
        F value = initial;
        for (E e : collection) {
            value = f.f(value, e);
        }
        return value;
    }

    /**
     * Check if a condition holds for all elements in the array
     *
     * @param array The array
     * @param f The condition
     */
    public static <E> boolean forall(E[] array, Criterion<E> f) {
        for (E e : array) {
            if (!f.f(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a condition holds for all elements in the collection
     *
     * @param array The collection
     * @param f The condition
     */
    public static <E> boolean forall(Collection<E> collection, Criterion<E> f) {
        for (E e : collection) {
            if (!f.f(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert each element of an array
     *
     * @param array The array
     * @param f The conversion function
     */
    public static <E, F> F[] map(E[] array, Converter<E, F> f) {
        final ArrayList list = new ArrayList();
        for (E e : array) {
            list.add(f.f(e));
        }
        return (F[]) list.toArray();
    }

    /**
     * Convert each element of a list
     *
     * @param list The list
     * @param f The conversion function
     */
    public static <E, F> List<F> map(List<E> list, Converter<E, F> f) {
        final ArrayList<F> newList = new ArrayList<F>();
        for (E e : list) {
            newList.add(f.f(e));
        }
        return Collections.unmodifiableList(newList);
    }

    /**
     * Convert each element of a set
     *
     * @param set The set
     * @param f The conversion function
     */
    public static <E, F> Set<F> map(Set<E> set, Converter<E, F> f) {
        final HashSet<F> newSet = new HashSet<F>();
        for (E e : set) {
            newSet.add(f.f(e));
        }
        return Collections.unmodifiableSet(newSet);
    }

    /**
     * Convert each element of a map
     *
     * @param map The map
     * @param f1 The key conversion function
     * @param f2 The value conversion function
     */
    public static <E, F, G, H> Map<G, H> map(Map<E, F> map, Converter<E, G> f1, Converter<F, H> f2) {
        final HashMap<G, H> newMap = new HashMap<G, H>();
        for (Map.Entry<E, F> entry : map.entrySet()) {
            newMap.put(f1.f(entry.getKey()), f2.f(entry.getValue()));
        }
        return Collections.unmodifiableMap(newMap);
    }

    /**
     * Remove all elements matching a criterion from a list and return a new
     * list containing all elements matching the criterion
     *
     * @param list The list
     * @param f The criterion
     */
    public static <E> List<E> partition(List<E> list, Criterion<E> f) {
        final ArrayList<E> newList = new ArrayList<E>();
        final Iterator<E> iter = list.iterator();
        while (iter.hasNext()) {
            final E e = iter.next();
            if (f.f(e)) {
                newList.add(e);
                iter.remove();
            }
        }
        return Collections.unmodifiableList(newList);
    }

    /**
     * Remove all elements matching a criterion from a set and return a new set
     * containing all elements matching the criterion
     *
     * @param set The set
     * @param f The criterion
     */
    public static <E> Set<E> partition(Set<E> set, Criterion<E> f) {
        final HashSet<E> newSet = new HashSet<E>();
        final Iterator<E> iter = set.iterator();
        while (iter.hasNext()) {
            final E e = iter.next();
            if (f.f(e)) {
                newSet.add(e);
                iter.remove();
            }
        }
        return Collections.unmodifiableSet(newSet);
    }

    /**
     * Remove all entries whose keys match a criterion from a set and return a
     * new map containing all entries matching the criterion
     *
     * @param map The map
     * @param f The criterion
     */
    public static <E, F> Map<E, F> partitionByKey(Map<E, F> map, Criterion<E> f) {
        final HashMap<E, F> newMap = new HashMap<E, F>();
        final Iterator<Entry<E, F>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<E, F> e = iter.next();
            if (f.f(e.getKey())) {
                newMap.put(e.getKey(), e.getValue());
                iter.remove();
            }
        }
        return Collections.unmodifiableMap(newMap);
    }

    /**
     * Remove all entries whose values match a criterion from a set and return a
     * new map containing all entries matching the criterion
     *
     * @param map The map
     * @param f The criterion
     */
    public static <E, F> Map<E, F> partitionByValue(Map<E, F> map, Criterion<F> f) {
        final HashMap<E, F> newMap = new HashMap<E, F>();
        final Iterator<Entry<E, F>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<E, F> e = iter.next();
            if (f.f(e.getValue())) {
                newMap.put(e.getKey(), e.getValue());
                iter.remove();
            }
        }
        return Collections.unmodifiableMap(newMap);
    }

    /**
     * Create a string from a collection using a particular separator String.
     * e.g., builds Strings like "a,b,c" of "a|||b|||c"
     *
     * @param collection The collection
     * @param separator The separator
     * @return The string
     */
    public static <StrLike extends CharSequence> String joinString(Collection<StrLike> collection, String separator) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<StrLike> iterator = collection.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }
    /**
     * Convenience SAM function that returns the same value
     */
    public static final Converter IDENTITY = new Converter() {

        @Override
        public Object f(Object e) {
            return e;
        }
    };

    /**
     * Convenience equivalence function
     */
    public static <E> Criterion<E> eq(final E e) {
        return new Criterion<E>() {

            @Override
            public boolean f(E e2) {
                return (e2 != null && e2.equals(e))
                        || (e2 == null && e == null);
            }
        };
    }

    /**
     * Convenience equivalence function
     */
    public static <E> Criterion<E> neq(final E e) {
        return new Criterion<E>() {

            @Override
            public boolean f(E e2) {
                return (e2 != null && e2.equals(e))
                        || (e2 == null && e != null);
            }
        };
    }

    /**
     * A single abstract method interface for a function of arity 1:1
     */
    public static interface Converter<E, F> {

        F f(E e);
    }

    /**
     * A single abstract method interface for a function of arity 2:1
     */
    public static interface FoldFunction<F, E> {

        F f(F f, E e);
    }

    /**
     * A single abstract method interface returning a boolean
     */
    public static interface Criterion<E> {

        boolean f(E e);
    }
}
