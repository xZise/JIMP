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

public class ParsedParameter<V extends Variables> implements Parameter {

    private final MethodParser<? super V> parser;
    private final String parameterValue;
    private final V variable;
    private final int depth;

    public ParsedParameter(final MethodParser<? super V> parser, final String parameterValue, final V variable, final int depth) {
        this.parser = parser;
        this.parameterValue = parameterValue;
        this.variable = variable;
        this.depth = depth;
    }

    public static <V extends Variables> ParsedParameter<V> create(final MethodParser<? super V> parser, final String parameterValue, final V variable, final int depth) {
        return new ParsedParameter<V>(parser, parameterValue, variable, depth);
    }

    @Override
    public ParameterType parse() {
        return this.parser.parseLine(this.parameterValue, this.variable, this.depth);
    }

    @Override
    public String getText() {
        return this.parameterValue;
    }

}
