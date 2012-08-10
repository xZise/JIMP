/*
 * This file is part of Bukkit Plugin Utilities.
 * 
 * Bukkit Plugin Utilities is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Bukkit Plugin Utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bukkit Plugin Utilities.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.xzise.jimp.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.RandomAccess;

public class Array<E> extends AbstractList<E> implements RandomAccess, java.io.Serializable {
	private static final long serialVersionUID = 8108838603745893478L;
	private final E[] array;
	private final int start;
	private final int end;
	public final int length;

	public Array() {
		this(0);
	}

	@SuppressWarnings("unchecked")
	public Array(final int size) {
		this.array = (E[]) new Object[size];
		this.start = 0;
		this.end = size - 1;
		this.length = size;
	}

	public Array(final E... array) {
		this(0, array.length - 1, array);
	}

	public Array(final Collection<E> collection, final E[] array) {
		this(collection.toArray(array));
	}

	public Array(final E[] array, final int start, final int end) {
		this(start, end, array);
	}

	public Array(final int start, final int end, final E... array) {
		if (array == null)
			throw new NullPointerException();
		if (start - 1 > end)
			throw new IllegalArgumentException("End index before start index!");
		this.array = array;
		this.start = start;
		this.end = end;
		this.length = end - start + 1;
	}

	public static <E> Array<E> create(final E... array) {
		return new Array<E>(array);
	}

	public static <E> Array<E> create(final E[] array, final int start, final int end) {
		return create(start, end, array);
	}

	public static <E> Array<E> create(final int start, final int end, final E... array) {
		return new Array<E>(array, start, end);
	}

	public static Array<Short> create(final short... array) {
		final Short[] shorts = new Short[array.length];
		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = array[i];
		}
		return new Array<Short>(shorts);
	}

	@Override
	public int size() {
		return this.length;
	}

	@Override
	public E[] toArray() {
		if (this.length == this.array.length) {
			return this.array.clone();
		} else {
			return Arrays.copyOfRange(this.array, this.start, this.end);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		final int size = size();
		if (a.length < size)
			return Arrays.copyOfRange(this.array, this.start, this.end, (Class<? extends T[]>) a.getClass());
		System.arraycopy(this.array, this.start, a, 0, this.length);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	@Override
	public E get(final int index) {
		return this.array[this.start + index];
	}

	@Override
	public E set(final int index, final E element) {
		E oldValue = this.array[this.start + index];
		this.array[this.start + index] = element;
		return oldValue;
	}

	/**
	 * Tests where the first object is inside the array.
	 * 
	 * @param o
	 *            Searched object.
	 * @param a
	 *            Searched array.
	 * @return the first position found.
	 * @since 1.3
	 */
	public static <T> int indexOf(T o, T... a) {
		return Array.indexOf(o, a, 0, a.length - 1);
	}

	private static <T> int indexOf(final T o, final T[] a, final int start, final int end) {
		for (int i = start; i <= end; i++) {
			if (a[i] == null ? o == null : a[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int indexOf(Object o) {
		return Array.indexOf(o, this.array, this.start, this.end);
	}

	/**
	 * Returns if the tested object <code>o</code> is in the array
	 * <code>a</code>.
	 * 
	 * @param o
	 *            Searched object.
	 * @param a
	 *            Searched array.
	 * @return if the object is in the array.
	 * @since 1.3
	 */
	public static <T> boolean contains(T o, T[] a) {
		return Array.indexOf(o, a) >= 0;
	}

	private static <T> boolean contains(T o, T[] a, final int start, final int end) {
		return Array.indexOf(o, a, start, end) >= 0;
	}

	/**
	 * Returns if the tested character is in the characters array.
	 * 
	 * @param character
	 *            Searched character.
	 * @param characters
	 *            Searched characters.
	 * @return if the character is in the array.
	 * @since 1.3
	 */
	public static boolean contains(char character, char[] characters) {
		return Array.indexOf(character, characters) >= 0;
	}

	public boolean contains(Object o) {
		return Array.contains(o, this.array, this.start, this.end);
	}

	public Array<E> subarray(final int start) {
		return new Array<E>(this.array, this.start + start, this.end);
	}

	public Array<E> subarray(final int start, final int end) {
		return new Array<E>(this.array, this.start + start, this.start + end);
	}

	@Override
	public Iterator<E> iterator() {
		return new ArrayIterator<E>(this.array, this.start, this.end);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ArrayIterator<E>(this.array, index, this.start, this.end);
	}
}
