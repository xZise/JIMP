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

import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

/**
 * Simple class for all original methods (without any arguments). Redirect all
 * calls to {@link OriginalMethod#call(String, int, Variables)}.
 */
public abstract class OriginalMethod<V extends Variables> extends DefaultNamedMethod<V> {

    public OriginalMethod(final String defaultName) {
        super(defaultName, 0);
    }

    @Override
    public final ParameterType call(Parameter[] parameters, int depth, V globalParameters) {
        if (parameters.length == 0) {
            return this.call(globalParameters);
        } else {
            return null;
        }
    }

    protected abstract ParameterType call(V globalParameters);
}
