package de.xzise.jimp.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.Files;

public class Util {

    private Util() {
    }

    public static <E extends Enum<E>> ImmutableBiMap<String, E> createNameBiMap(final Class<E> enumClass) {
        final ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
        for (E enumElement : enumClass.getEnumConstants()) {
            builder.put(enumElement.name(), enumElement);
        }
        return builder.build();
    }

    public static <T> T getLast(final Map<T, T> map, T value) {
        T result = null;
        do {
            result = value;
            value = map.get(value);
        } while (value != null);
        return result;
    }

    public static <T> T getLast(final List<T> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static boolean equals(final Object a, final Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public static int max(int a, final int... b) {
        for (int i : b) {
            if (i > a) {
                a = i;
            }
        }
        return a;
    }

    private static final int BYTE_BITCOUNT = Byte.SIZE;

    private static int[] readInts(final byte[] bytes, final int bitCount) {
        if (bytes != null) {
            final int byteCount = bitCount / BYTE_BITCOUNT;
            final int[] words = new int[Util.divUpper(bytes.length, byteCount)];
            for (int i = 0; i < words.length; i++) {
                words[i] = 0;
                int shift = bitCount;
                for (int j = i * byteCount; j < Math.min((i + 1) * byteCount, bytes.length); j++) {
                    shift -= BYTE_BITCOUNT;
                    words[i] |= (bytes[j] & 0xFF) << shift;
                }
            }
            return words;
        } else {
            return null;
        }
    }

    public static int[] readInts(final File file) throws IOException {
        return Util.readInts(Files.toByteArray(file), Integer.SIZE);
    }

    public static int[] readShorts(final File file) throws IOException {
        return Util.readInts(Files.toByteArray(file), Short.SIZE);
    }

    public static int ceil(int dividend, final int divisor) {
        final int mod = dividend % divisor;
        if (mod > 0) {
            dividend += divisor - mod;
        }
        return dividend;
    }

    public static int divUpper(int dividend, final int divisor) {
        int div = dividend / divisor;
        if (dividend % divisor > 0) {
            div++;
        }
        return div;
    }

    public static byte reverse(final byte b) {
        return (byte) reverse(b, 1);
    }

    public static short reverse(final short s) {
        return (short) reverse(s, 2);

    }

    public static int reverse(final int i) {
        return (int) reverse(i, 3);
    }

    public static long reverse(final long l) {
        return reverse(l, 4);
    }

    private static long reverse(long l, final int binaryExponent) {
        l = (((l & 0xaaaaaaaaaaaaaaaaL) >>> 1) | ((l & 0x5555555555555555L) << 1));
        l = (((l & 0xccccccccccccccccL) >>> 2) | ((l & 0x3333333333333333L) << 2));
        l = (((l & 0xf0f0f0f0f0f0f0f0L) >>> 4) | ((l & 0x0f0f0f0f0f0f0f0fL) << 4));
        if (binaryExponent > 1) {
            l = (((l & 0xff00ff00ff00ff00L) >>> 8) | ((l & 0x00ff00ff00ff00ffL) << 8));
            if (binaryExponent > 2) {
                l = (((l & 0xffff0000ffff0000L) >>> 16) | ((l & 0x0000ffff0000ffffL) << 16));
                if (binaryExponent > 3) {
                    l = ((l >>> 32) | (l << 32));
                }
            }
        }
        return l;
    }

    public static int getUnsignedShort(final short s) {
        return s & 0xFFFF;
    }

    public static int getUnsignedByte(final byte b) {
        return b & 0xFF;
    }

    public static long createMask(final int bitcount) {
        return (~0L) << bitcount;
    }

    public static class ValidTest {
        public final long mask;
        public final int bitCount;

        public ValidTest(final int bitCount) {
            this.bitCount = bitCount;
            this.mask = createMask(bitCount);
        }

        public boolean isValid(final long l) {
            return Util.isValidMasked(l, this.mask);
        }
    }

    public static final ValidTest INTEGER_TEST = new ValidTest(Integer.SIZE);
    public static final ValidTest SHORT_TEST = new ValidTest(Short.SIZE);
    public static final ValidTest BYTE_TEST = new ValidTest(Byte.SIZE);

    public static boolean isInteger(final long l) {
        return INTEGER_TEST.isValid(l);
    }

    public static boolean isShort(final long l) {
        return SHORT_TEST.isValid(l);
    }

    public static boolean isByte(final long l) {
        return BYTE_TEST.isValid(l);
    }

    public static boolean isValid(final long l, final int bitcount) {
        return isValidMasked(l, createMask(bitcount));
    }

    public static boolean isValidMasked(final long l, final long mask) {
        return (l & mask) == 0;
    }

    public static String fixedWidthHex(final int i) {
        return Util.fixedWidthHex(i, 4);
    }

    public static String fixedWidthHex(final long i, final int width) {
        return Util.fixedWidthInt(i, '0', width, 16);
    }

    public static String fixedWidthDec(final long i, final char prefix, final int width) {
        return Util.fixedWidthInt(i, prefix, width, 10);
    }

    public static String fixedWidthInt(final long i, final char prefix, final int width, final int base) {
        char[] c = Long.toString(i, base).toCharArray();
        char[] out = new char[width];
        for (int j = 0; j < out.length; j++) {
            if (j < out.length - c.length) {
                out[j] = prefix;
            } else {
                out[j] = c[j - out.length + c.length];
            }
        }
        return new String(out);
    }

    /**
     * Try to read the parameter as an integer. This methods uses all features
     * from {@link #parseAsIntegerFixed(String)} but also allows flexible
     * radixes by using a underscore sign. For example the string
     * <code>20_3</code> returns the decimal value 6. The radix is
     * <code>3</code> and the value is <code>20</code>. The radix will be parsed
     * with {@link #parseAsIntegerFixed(String)} and the value with
     * {@link Integer#parseInt(String)}.
     * 
     * @param parameter
     *            parameter value.
     * @return The parameter parsed as an integer. If it isn't a valid value it
     *         returns <code>null</code>.
     * @see DefaultMethod#parseAsLongFixed(String)
     */
    public static Long parseAsLong(final String parameter) {
        if (Util.isSet(parameter)) {
            Long fixedParse = Util.parseAsLongFixed(parameter);
            if (fixedParse != null) {
                return fixedParse;
            } else {
                final String[] s = parameter.split("_");
                if (s.length == 2) {
                    Long radix = Util.parseAsLongFixed(s[1]);
                    return radix != null ? Util.tryAndGetLong(s[0], radix.intValue()) : null;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Try to read the parameter as a long. There are several configurations
     * possible:
     * <ul>
     * <li>If the parameter's length is one it try to read it as a hexadecimal
     * value.</li>
     * <li>If the number is formated like <code>0xF</code> it is the hexadecimal
     * value F and the decimal value 15.</li>
     * <li>If the number is formated like <code>0b10</code> it is the binary
     * value 10 and the decimal value 2.</li>
     * <li>If the number is formated like <code>010</code> it is the octal value
     * 10 and the decimal value 8.</li>
     * <li>In all other cases it interprets it as a decimal value.</li>
     * </ul>
     * 
     * @param parameter
     *            parameter value.
     * @return The parameter parsed as a long. If it isn't a valid value it
     *         returns <code>null</code>.
     * @see DefaultMethod#parseAsLong(String)
     */
    public static Long parseAsLongFixed(final String parameter) {
        if (Util.isSet(parameter)) {
            if (parameter.length() == 1) {
                return Util.tryAndGetLong(parameter, 16);
            } else {
                final int radix;
                boolean positive = true;
                int start = 0;
                switch (parameter.charAt(start)) {
                case '-':
                    positive = false;
                case '+':
                    start++;
                    break;
                }
                if (parameter.charAt(start) == '0') {
                    start++; // Skip '0'
                    if (parameter.charAt(start) == 'x') {
                        radix = 16;
                        start++; // Skip 'x'
                    } else if (parameter.charAt(start) == 'b') {
                        radix = 2;
                        start++; // Skip 'b'
                    } else {
                        radix = 8;
                    }
                } else {
                    radix = 10;
                }
                final Long value = Util.tryAndGetLong(parameter.substring(start), radix);
                return value == null ? null : positive ? value : -value;
            }
        } else {
            return null;
        }
    }

    /**
     * Checks if an object is set. Set mean at least “not null”. Following
     * objects will be checked separate:
     * 
     * <blockquote>
     * <table>
     * <tr>
     * <th>Type</th>
     * <th>Tests</th>
     * </tr>
     * <tr>
     * <td>CharSequence</td>
     * <td>{@link CharSequence#length()} is positive</td>
     * </tr>
     * <tr>
     * <td>Collection&lt;?&gt;</td>
     * <td>not <tt>{@link Collection#isEmpty()}</tt></td>
     * </tr>
     * <tr>
     * <td>Map&lt;?, ?&gt;</td>
     * <td>not <tt>{@link Map#isEmpty()}</tt></td>
     * </tr>
     * <tr>
     * <td>Any array</td>
     * <td>Arraylength is positive</td>
     * </tr>
     * </table>
     * </blockquote>
     * 
     * @param o
     *            The tested object.
     * @return If the object is not empty.
     * @since 1.0
     */
    public static boolean isSet(Object o) {
        if (o == null) {
            return false;
        }
        try {
            if (o instanceof CharSequence) {
                return ((CharSequence) o).length() > 0;
            } else if (o instanceof Collection<?>) {
                return !((Collection<?>) o).isEmpty();
            } else if (o instanceof Map<?, ?>) {
                return !((Map<?, ?>) o).isEmpty();
            } else if (o.getClass().isArray()) {
                return java.lang.reflect.Array.getLength(o) > 0;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static final String DIGITS = "0123456789";
    private static final String UPPER_DIGIT_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_DIGIT_MAP = "abcdefghijklmnopqrstuvwxyz";

    public static Long speedLongParse(final String string, final int radix) {
        final int len = string.length();
        if (len == 0) {
            return null;
        }
        int i = string.charAt(0) == '-' ? 1 : 0;
        if (len == i) {
            return null;
        }
        do {
            final char c = string.charAt(i);
            final int digitIdx = DIGITS.indexOf(c);
            if (digitIdx >= radix) {
                return null;
            }
            final int upperIdx = UPPER_DIGIT_MAP.indexOf(c);
            if (upperIdx >= radix - 10) {
                return null;
            }
            final int lowerIdx = LOWER_DIGIT_MAP.indexOf(c);
            if (lowerIdx >= radix - 10) {
                return null;
            }
            if (digitIdx < 0 && upperIdx < 0 && lowerIdx < 0) {
                return null;
            }
        } while (++i < len);
        // The string should be valid
        return Long.parseLong(string, radix);
    }

    /**
     * Tries to convert a string into an long. If the string is invalid it
     * returns <code>null</code>.
     * 
     * @param string
     *            The string to be parsed.
     * @param radix
     *            The radix of the long.
     * @return The value if the string is valid, otherwise <code>null</code>.
     */
    public static Long tryAndGetLong(String string, int radix) {
        return speedLongParse(string, radix);
    }

    /**
     * Tries to convert a string into an long. If the string is invalid it
     * returns <code>null</code>.
     * 
     * @param string
     *            The string to be parsed.
     * @return The value if the string is valid, otherwise <code>null</code>.
     */
    public static Long tryAndGetLong(String string) {
        return tryAndGetLong(string, 10);
    }
}
