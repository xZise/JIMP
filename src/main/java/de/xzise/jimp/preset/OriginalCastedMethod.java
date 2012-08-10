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

public abstract class OriginalCastedMethod<V extends Variables, C extends V> extends DefaultCastedNamedMethod<V, C> {

    public OriginalCastedMethod(final String defaultName, final Class<? extends C> variableClass) {
        super(defaultName, variableClass, 0);
    }

    @Override
    public final ParameterType innerCall(final Parameter[] parameters, final RuntimeOptions<C> runtime) {
        if (parameters.length == 0) {
            return this.call(runtime);
        } else {
            return null;
        }
    }

    protected abstract ParameterType call(final RuntimeOptions<C> runtime);

}
