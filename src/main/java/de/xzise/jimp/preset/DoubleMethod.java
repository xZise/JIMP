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

package de.xzise.jimp.preset;

import de.xzise.MinecraftUtil;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public abstract class DoubleMethod<V extends Variables, C extends V> extends DefaultCastedNamedMethod<V, C> {

    public DoubleMethod(final String defaultName, final Class<? extends C> variableClass) {
        super(defaultName, variableClass, 0, 1, 2);
    }

    @Override
    public final ParameterType innerCall(Parameter[] parameters, C globalParameters) {
        if (parameters.length > 2) {
            return null;
        } else {
            Double value = getValue(globalParameters);
            if (value != null) {
                Long minDecimals = 0L;
                Long maxDecimals = 0L;
                switch (parameters.length) {
                case 2:
                    minDecimals = NativeParameterType.asLong(parameters[1].parse());
                case 1:
                    maxDecimals = NativeParameterType.asLong(parameters[0].parse());
                    break;
                }
                if (minDecimals != null && maxDecimals != null) {
                    return new DoubleParameterType(value, MinecraftUtil.getFormatWithMinimumDecimals(minDecimals.intValue(), maxDecimals.intValue()));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public abstract Double getValue(C globalParameters);
}
