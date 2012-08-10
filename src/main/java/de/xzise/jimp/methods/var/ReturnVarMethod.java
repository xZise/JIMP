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

package de.xzise.jimp.methods.var;

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ArrayParameterType;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class ReturnVarMethod<V extends Variables> extends DefaultNamedMethod<V> {

    private final boolean persistent;

    public ReturnVarMethod(final boolean persistent) {
        this(persistent ? "returnpvar" : "returnvar", persistent);
    }

    protected ReturnVarMethod(final String name, final boolean persistent) {
        super(name, -1);
        this.persistent = persistent;
    }

    protected ParameterType getValue(final Parameter[] parameters, final RuntimeOptions<? extends V> runtime) {
        if (parameters.length == 2) {
            return parameters[0].getValue(runtime);
        } else if (parameters.length > 2) {
            final ParameterType[] array = new ParameterType[parameters.length - 1];
            for (int i = 1; i < parameters.length; i++) {
                array[i - 1] = parameters[i].getValue(runtime);
            }
            return new ArrayParameterType(array);
        } else {
            return null;
        }
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<? extends V> runtime) {
        final ParameterType value;
        if (parameters.length > 0) {
            final String name = parameters[0].getValue(runtime).asString();
            value = this.getValue(parameters, runtime);
            if (value == null) {
                return runtime.parser.getVariable(name);
            } else {
                runtime.parser.setVariable(name, value, this.persistent);
            }
        } else {
            value = NativeParameterType.NULL_PARAMETER_TYPE;
        }
        return value;
    }

}
