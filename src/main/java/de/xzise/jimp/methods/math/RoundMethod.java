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

package de.xzise.jimp.methods.math;

import java.text.DecimalFormat;

import de.xzise.jimp.MethodParser;
import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.DoubleParameter;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class RoundMethod extends DefaultNamedMethod<Variables> {

    private final MethodParser<?> parser;

    public RoundMethod(final MethodParser<? extends Variables> parser) {
        super("round", 1);
        this.parser = parser;
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        final ParameterType type = parameters[0].getValue(runtime);
        final double d;
        final DecimalFormat format;
        if (type instanceof DoubleParameter) {
            d = Math.round(((DoubleParameter) type).asDouble());
        } else {
            return null;
        }

        if (type instanceof DoubleParameterType) {
            format = ((DoubleParameterType) type).getFormat();
        } else {
            format = this.parser.getDefaultFormat();
        }
        return new DoubleParameterType(d, format);
    }
}
