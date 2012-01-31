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

import de.xzise.jimp.MethodParser;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class UnsetVarMethod<V extends Variables> extends DefaultNamedMethod<V> {

    private final MethodParser<? super V> parser;

    public UnsetVarMethod(final MethodParser<? super V> parser) {
        super("unsetvar", -1);
        this.parser = parser;
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, Variables globalParameters) {
        for (Parameter parameter : parameters) {
            this.parser.unsetVariable(parameter.parse().asString());
        }
        return NativeParameterType.EMPTY_PARAMETER_TYPE;
    }

}
