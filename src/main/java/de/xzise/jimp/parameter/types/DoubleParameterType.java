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

import java.text.DecimalFormat;

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;

import de.xzise.MinecraftUtil;

public class DoubleParameterType extends NativeParameterType implements DoubleParameter, NumberParameter {

    private final Double value;
    private final DecimalFormat format;

    public DoubleParameterType(final double value, final DecimalFormat format) {
        this.value = value;
        this.format = format;
    }

    public DoubleParameterType(final double value, final int minimumDecimals, final int maximumDecimals) {
        this(value, MinecraftUtil.getFormatWithMinimumDecimals(minimumDecimals, maximumDecimals));
    }

    @Override
    public Double asDouble() {
        return this.value;
    }

    @Override
    public Number asNumber() {
        return this.value;
    }

    @Override
    public String asString() {
        return this.format.format(this.value);
    }

    public DecimalFormat getFormat() {
        return this.format;
    }

    public static final DoubleParameterTypeFactory DOUBLE_PARAMETER_TYPE_FACTORY = new DoubleParameterTypeFactory();

    public static class DoubleParameterTypeFactory implements ParameterTypeFactory {

        @Override
        public ParameterType create(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
            Long minDecimals = 0L;
            Long maxDecimals = 0L;
            Double d = null;
            switch (parameters.length) {
            case 3:
                minDecimals = NativeParameterType.asLong(parameters[2].getValue(runtime));
            case 2:
                maxDecimals = NativeParameterType.asLong(parameters[1].getValue(runtime));
            case 1:
                d = NativeParameterType.asDouble(parameters[0].getValue(runtime));
                if (d == null) {
                    final String value = parameters[0].getValue(runtime).asString();
                    if (value != null) {
                        d = MinecraftUtil.tryAndGetDouble(value);
                    }
                }
                break;
            }
            if (minDecimals != null && maxDecimals != null && d != null) {
                return new DoubleParameterType(d, minDecimals.intValue(), maxDecimals.intValue());
            } else {
                return null;
            }
        }

    }
}
