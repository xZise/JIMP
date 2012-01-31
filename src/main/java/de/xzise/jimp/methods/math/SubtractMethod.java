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

import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.LongParameterType;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class SubtractMethod extends DefaultNamedMethod<Variables> {

    public SubtractMethod() {
        super("subtract", -1);
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, Variables globalParameters) {
        double difference = 0D;
        Double buffer;
        for (Parameter parameter : parameters) {
            buffer = NativeParameterType.asDouble(parameter.parse());
            if (buffer != null) {
                difference -= buffer;
            }
        }
        return new DoubleParameterType(difference);
    }

}
