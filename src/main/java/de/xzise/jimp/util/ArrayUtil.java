package de.xzise.jimp.util;

import java.util.Arrays;
import java.util.List;

public class ArrayUtil {

	private ArrayUtil() {
	}

	public static char[] getCharArray(final List<Character> chars) {
		final char[] c = new char[chars.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = chars.get(i);
		}
		return c;
	}

	public static int[] getIntArray(final List<? extends Number> ints) {
		final int[] intArray = new int[ints.size()];
		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = ints.get(i).intValue();
		}
		return intArray;
	}

	public static short[] getShortArray(final List<? extends Number> ints) {
		final short[] shortArray = new short[ints.size()];
		for (int i = 0; i < shortArray.length; i++) {
			shortArray[i] = ints.get(i).shortValue();
		}
		return shortArray;
	}

	public static byte[] getByteArray(final List<? extends Number> ints) {
		final byte[] byteArray = new byte[ints.size()];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = ints.get(i).byteValue();
		}
		return byteArray;
	}

	public static void copy(final List<? extends Number> list, final int listOffset, final short[] target, final int targetOffset, final int length) {
		for (int i = 0; i < length; i++) {
			target[targetOffset + i] = list.get(listOffset + i).shortValue();
		}
	}

	public static short[] fill(final int length, final short s) {
		final short[] shorts = new short[length];
		Arrays.fill(shorts, s);
		return shorts;
	}

	public static char[] fill(final int length, final char c) {
		final char[] chars = new char[length];
		Arrays.fill(chars, c);
		return chars;
	}

	public static <T> T[] fill(final int length, final T t) {
		final T[] array = createArray(t.getClass(), length);
		Arrays.fill(array, t);
		return array;
	}

	public static String getString(final Array<Character> chars) {
		return new String(ArrayUtil.getCharArray(chars));
	}

	public static String getString(final Array<Character> chars, int offset, int count) {
		return String.copyValueOf(ArrayUtil.getCharArray(chars), offset, count);
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(Class<?> clazz, int newLength) {
		return ((Object) clazz == (Object) Object[].class) ? (T[]) new Object[newLength] : (T[]) java.lang.reflect.Array.newInstance(clazz.getComponentType(), newLength);
	}

	public static <T> T[] concat(final T first, final T... array) {
		final T[] completed = createArray(array.getClass(), array.length + 1);
		completed[0] = first;
		System.arraycopy(array, 0, completed, 1, array.length);
		return completed;
	}

	public static int indexOf(final char[] chars, final char ch) {
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(final int[] ints, final int i) {
		for (int j = 0; j < ints.length; j++) {
			if (ints[j] == i) {
				return j;
			}
		}
		return -1;
	}

	public static short[] convertIntArray(final int[] ints) {
		if (ints != null) {
			final short[] words = new short[ints.length >> 1];
			for (int i = 0; i < words.length; i++) {
				words[i] = (short) ints[i];
			}
			return words;
		} else {
			return null;
		}
	}
}
