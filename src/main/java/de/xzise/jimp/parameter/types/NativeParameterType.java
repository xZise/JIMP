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

public abstract class NativeParameterType extends NonArrayParameterType {

    public static final NativeParameterType NULL_PARAMETER_TYPE = new NullParameterType();

    public static final NativeParameterType EMPTY_PARAMETER_TYPE = new NullParameterType() {
        @Override
        public String asString() {
            return "";
        };
    };

    public static class NullParameterType extends NativeParameterType implements DoubleParameter, LongParameter, BooleanParameter {

        @Override
        public Number asNumber() {
            return null;
        }

        @Override
        public Double asDouble() {
            return null;
        }

        @Override
        public Long asLong() {
            return null;
        }

        @Override
        public String asString() {
            return null;
        }

        @Override
        public Boolean asBoolean() {
            return null;
        }
    }

    @Override
    public String asParsableString(String prefix) {
        return this.asString();
    }

    public static Long asLong(final ParameterType parameter) {
        if (parameter instanceof LongParameter) {
            return ((LongParameter) parameter).asLong();
        } else {
            return null;
        }
    }

    public static Number asNumber(final ParameterType parameter) {
        if (parameter instanceof NumberParameter) {
            return ((NumberParameter) parameter).asNumber();
        } else {
            return null;
        }
    }

    public static Double asDouble(final ParameterType parameter) {
        if (parameter instanceof DoubleParameter) {
            return ((DoubleParameter) parameter).asDouble();
        } else {
            return null;
        }
    }

    public static Boolean asBoolean(final ParameterType parameter) {
        if (parameter instanceof BooleanParameter) {
            return ((BooleanParameter) parameter).asBoolean();
        } else {
            return null;
        }
    }
}
