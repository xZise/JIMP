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

package de.xzise.jimp.parameter;

import de.xzise.jimp.MethodParser;

import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public class OnceParsedParameter<V extends Variables> extends ParsedParameter<V> {

    private ParameterType parsedParameter = null;
    private boolean parsed = false;

    public OnceParsedParameter(final MethodParser<? super V> parser, final String parameterValue, final V variable, final int depth) {
        super(parser, parameterValue, variable, depth);
    }

    public static <V extends Variables> OnceParsedParameter<V> create(final MethodParser<? super V> parser, final String parameterValue, final V variable, final int depth) {
        return new OnceParsedParameter<V>(parser, parameterValue, variable, depth);
    }

    @Override
    public ParameterType parse() {
        if (!this.parsed) {
            this.parsedParameter = super.parse();
            this.parsed = true;
        }
        return this.parsedParameter;
    }
}
