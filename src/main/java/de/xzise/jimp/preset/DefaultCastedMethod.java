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

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public abstract class DefaultCastedMethod<V extends Variables, C extends V> extends DefaultMethod<V> {

    private final Class<? extends C> variableClass;

    public DefaultCastedMethod(final Class<? extends C> variableClass, final int paramCount, final int... paramCounts) {
        super(paramCount, paramCounts);
        this.variableClass = variableClass;
    }

    public abstract ParameterType innerCall(Parameter[] parameters, RuntimeOptions<C> runtime);

    @SuppressWarnings("unchecked")
    @Override
    public final ParameterType call(Parameter[] parameters, RuntimeOptions<? extends V> runtime) {
        if (this.variableClass.isInstance(runtime.variables)) {
            return this.innerCall(parameters, (RuntimeOptions<C>) runtime);
        } else {
            return null;
        }
    }

}
