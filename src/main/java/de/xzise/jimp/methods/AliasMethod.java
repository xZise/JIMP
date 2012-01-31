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

import de.xzise.jimp.MethodParser;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class AliasMethod<V extends Variables> extends DefaultNamedMethod<V> {

    private final String result;
    private final MethodParser<V> parser;

    public AliasMethod(final String result, final int paramCount, final String name, final MethodParser<V> parser) {
        super(name, paramCount);
        this.result = result;
        this.parser = parser;
    }

    public static <V extends Variables> AliasMethod<V> create(final String result, final int paramCount, final String name, final MethodParser<V> parser) {
        return new AliasMethod<V>(result, paramCount, name, parser);
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, V globalParameters) {
        if (this.getParamCounts()[0] == parameters.length) {
            String result = this.result;
            for (int i = 0; i < parameters.length; i++) {
                result = result.replaceAll("\\$" + i + ";", parameters[i].parse().asParsableString(this.parser.getPrefix()));
            }
            return this.parser.parseLine(result, globalParameters, depth + 1);
        } else {
            return null;
        }
    }

    public AliasMethod<V> register() {
        super.register(this.parser);
        return this;
    }

    public AliasMethod<V> unregister() {
        super.unregister(this.parser);
        return this;
    }
}
