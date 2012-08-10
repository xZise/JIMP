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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T>, ListIterator<T> {
	private final T[] array;
	private final int start;
	private final int end;
	private int pos;

	public ArrayIterator(final T[] array) {
		this(array, 0);
	}

	public ArrayIterator(final T[] array, final int start) {
		this(array, 0, array.length - 1);
	}

	public ArrayIterator(final T[] array, final int start, final int end) {
		this(array, start, start, end);
	}

	public ArrayIterator(final T[] array, final int startPos, final int start, final int end) {
		this.array = array;
		this.start = start;
		this.end = end;
		this.pos = startPos;
	}

	@Override
	public boolean hasNext() {
		return this.pos < this.end;
	}

	@Override
	public T next() throws NoSuchElementException {
		if (hasNext()) {
			return this.array[this.pos++];
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPrevious() {
		return this.pos > this.start;
	}

	@Override
	public T previous() {
		if (hasPrevious()) {
			return this.array[this.pos--];
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public int nextIndex() {
		return this.pos;
	}

	@Override
	public int previousIndex() {
		return this.pos - 1;
	}

	@Override
	public void set(T e) {
		this.array[this.pos] = e;
	}

	@Override
	public void add(T e) {
		throw new UnsupportedOperationException();
	}
}
