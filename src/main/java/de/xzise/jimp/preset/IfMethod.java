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

import java.util.Arrays;

import de.xzise.jimp.Method;
import de.xzise.jimp.parameter.FinalParameter;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public abstract class IfMethod<V extends Variables> implements Method<V> {

    private final int preValueCount;
    private final boolean inverted;

    protected IfMethod(final int preValueCount, final boolean inverted) {
        this.preValueCount = Math.max(0, preValueCount);
        this.inverted = inverted;
    }

    @Override
    public final ParameterType call(Parameter[] parameters, int depth, V globalParameters) {
        Parameter match = FinalParameter.EMPTY_PARAMETER;
        Parameter noMatch = FinalParameter.EMPTY_PARAMETER;
        switch (parameters.length - this.preValueCount) {
        case 2:
            noMatch = parameters[this.preValueCount + 1];
        case 1:
            match = parameters[this.preValueCount];
            break;
        default:
            return null;
        }
        return this.match(Arrays.copyOf(parameters, this.preValueCount), globalParameters) != this.inverted ? match.parse() : noMatch.parse();
    }

    protected abstract Boolean match(Parameter[] preValues, V globalParameters);

}
