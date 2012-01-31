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

package de.xzise.jimp.methods;

import java.text.DecimalFormat;

import de.xzise.jimp.MethodParser;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.CreateableParameterTypes;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.ParameterTypeFactory;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class DecimalFormatMethod extends DefaultNamedMethod<Variables> {

    private static final String PARAMETER_TYPE_NAME = "decimalformat";

    public DecimalFormatMethod(final MethodParser<Variables> parser) {
        super("decimalformat", 2);
        parser.addParameterTypeFactory(PARAMETER_TYPE_NAME, DecimalFormatParameterType.DECIMAL_FORMAT_PARAMETER_TYPE_FACTORY);
    }

    public static class DecimalFormatParameterType extends CreateableParameterTypes {

        public final String format;
        private final DecimalFormat decFormat;

        public DecimalFormatParameterType(final String format) {
            this(format, new DecimalFormat(format));
        }

        public DecimalFormatParameterType(final String format, final DecimalFormat decFormat) {
            super(PARAMETER_TYPE_NAME);
            this.format = format;
            this.decFormat = decFormat;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public ParameterType[] getArray() {
            return new ParameterType[] { this };
        }

        @Override
        public String asString() {
            return this.format;
        }

        @Override
        protected void getContent(StringBuilder builder) {
            builder.append(this.format);
        }

        public String format(final Number number) {
            return this.decFormat.format(number);
        }

        public static final DecimalFormatParameterTypeFactory DECIMAL_FORMAT_PARAMETER_TYPE_FACTORY = new DecimalFormatParameterTypeFactory();

        public static class DecimalFormatParameterTypeFactory implements ParameterTypeFactory {

            @Override
            public ParameterType create(Parameter[] parameters, Variables variables) {
                if (parameters.length == 1) {
                    String value = parameters[0].parse().asString();
                    if (value != null) {
                        return new DecimalFormatParameterType(value);
                    }
                }
                return null;
            }

        }
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, Variables globalParameters) {
        if (parameters.length == 2) {
            Number n = NativeParameterType.asNumber(parameters[1].parse());
            if (n != null) {
                final ParameterType parameter = parameters[0].parse();
                if (parameter instanceof DecimalFormatParameterType) {
                    return new StringParameterType(((DecimalFormatParameterType) parameter).format(n));
                } else {
                    return new StringParameterType(new DecimalFormat(parameter.asString()).format(n));
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
