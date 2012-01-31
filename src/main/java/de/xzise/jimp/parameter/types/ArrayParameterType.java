/*
 * This file is part of Java Inline Method Parser.
 * 
 * Java Inline Method Parser is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Java Inline Method Parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Inline Method Parser.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.xzise.jimp.parameter.types;

public class ArrayParameterType extends CreateableParameterTypes implements DoubleParameter, LongParameter, BooleanParameter {

    private final ParameterType[] array;
    private final ParameterType value;

    public ArrayParameterType(final ParameterType[] array) {
        super("array");
        this.array = array;
        ParameterType first = null;
        if (this.array.length == 1) {
            first = this.array[0];
        }
        if (first == null) {
            this.value = NativeParameterType.NULL_PARAMETER_TYPE;
        } else {
            this.value = first;
        }
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public ParameterType[] getArray() {
        return this.array.clone();
    }

    @Override
    public Number asNumber() {
        return NativeParameterType.asNumber(this.value);
    }

    @Override
    public Double asDouble() {
        return NativeParameterType.asDouble(this.value);
    }

    @Override
    public Long asLong() {
        return NativeParameterType.asLong(this.value);
    }

    @Override
    protected void getContent(StringBuilder builder) {
        for (int i = 0; i < this.array.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(" \"").append(this.array[i]).append("\" ");
        }
    }

    /**
     * Returns all values with a starting "<code>{</code>" and ending "
     * <code>}</code>". For example if the array contains
     * {@code foo, bar, snafu} it returns <code>{"foo", "bar", "snafu"}</code>.
     * 
     * @return the contents and a paar of curved brackets.
     */
    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder("{");
        this.getContent(builder);
        return builder.append("}").toString();
    }

    @Override
    public Boolean asBoolean() {
        return NativeParameterType.asBoolean(this.value);
    }
}
