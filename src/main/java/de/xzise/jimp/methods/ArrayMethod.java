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

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ArrayParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.ParameterTypeFactory;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class ArrayMethod extends DefaultNamedMethod<Variables> implements ParameterTypeFactory {

    public static final ArrayMethod INSTANCE = new ArrayMethod();

    public ArrayMethod() {
        super("array", 0, -1);
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        return this.create(parameters, runtime);
    }

    @Override
    public ParameterType create(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        final ParameterType[] types = new ParameterType[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            types[i] = parameters[i].getValue(runtime);
        }
        return new ArrayParameterType(types);
    }

}
