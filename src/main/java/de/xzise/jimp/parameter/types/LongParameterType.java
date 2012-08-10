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

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.preset.DefaultMethod;

public class LongParameterType extends NativeParameterType implements LongParameter, NumberParameter, DoubleParameter {

    private final Long value;

    public LongParameterType(final long value) {
        this.value = value;
    }

    @Override
    public Double asDouble() {
        return new Double(this.value);
    }

    @Override
    public Number asNumber() {
        return this.value;
    }

    @Override
    public Long asLong() {
        return this.value;
    }

    @Override
    public String asString() {
        return Long.toString(this.value);
    }

    public static final LongParameterTypeFactory LONG_PARAMETER_TYPE_FACTORY = new LongParameterTypeFactory();

    public static class LongParameterTypeFactory implements ParameterTypeFactory {

        @Override
        public ParameterType create(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
            if (parameters.length == 1) {
                Long l = DefaultMethod.parseAsLong(parameters[0].getValue(runtime).asString());
                if (l != null) {
                    return new LongParameterType(l);
                }
            }
            return null;
        }

    }
}
