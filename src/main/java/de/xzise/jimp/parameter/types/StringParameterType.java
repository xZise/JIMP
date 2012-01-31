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

package de.xzise.jimp.parameter.types;

import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.variables.Variables;

public class StringParameterType extends NativeParameterType {

    public static final StringParameterType EMPTY_PARAMETER_TYPE = new StringParameterType("");

    private final String value;

    public StringParameterType(final String value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return this.value;
    }

    public static final StringParameterTypeFactory STRING_PARAMETER_TYPE_FACTORY = new StringParameterTypeFactory();

    public static class StringParameterTypeFactory implements ParameterTypeFactory {

        @Override
        public ParameterType create(Parameter[] parameters, Variables variables) {
            if (parameters.length == 1) {
                String value = parameters[0].parse().asString();
                if (value != null) {
                    return new StringParameterType(value);
                }
            }
            return null;
        }

    }
}
