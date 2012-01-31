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

import java.util.Iterator;

import de.xzise.collections.ArrayIterator;
import de.xzise.jimp.MethodParser;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

/**
 * Returns all parameters.
 */
public class PrintMethod<V extends Variables> extends DefaultNamedMethod<V> {

    private final boolean isRecursive;
    private final MethodParser<V> parser;

    public PrintMethod(final boolean isRecursive, final String defaultName, final MethodParser<V> parser) {
        super(defaultName, -1);
        this.isRecursive = isRecursive;
        this.parser = parser;
    }

    public static <V extends Variables> PrintMethod<V> create(final boolean isRecursive, final String defaultName, final MethodParser<V> parser) {
        return new PrintMethod<V>(isRecursive, defaultName, parser);
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, V globalParameters) {
        StringBuilder builder = new StringBuilder();
        final ParameterType[] typeArray = new ParameterType[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (this.isRecursive) {
                typeArray[i] = parameters[i].parse();
            } else {
                typeArray[i] = new StringParameterType(parameters[i].getText());
            }
        }
        printArray(typeArray, builder);
        if (this.isRecursive) {
            return this.parser.parseLine(builder.toString(), globalParameters, depth + 1);
        } else {
            return new StringParameterType(builder.toString());
        }
    }

    private void printArray(final ParameterType[] array, final StringBuilder builder) {
        for (Iterator<ParameterType> parameterItr = new ArrayIterator<ParameterType>(array); parameterItr.hasNext();) {
            final ParameterType type = parameterItr.next();
            if (type != null && type.isArray()) {
                this.printArray(type.getArray(), builder);
            } else {
                final String next = type == null ? null : type.asString();
                if (next == null) {
                    builder.append("##null##");
                } else if (next.isEmpty()) {
                    builder.append("##empty##");
                } else {
                    builder.append(next);
                }
                if (parameterItr.hasNext()) {
                    builder.append(" ");
                }
            }
        }
    }

    public PrintMethod<V> register() {
        super.register(this.parser);
        return this;
    }

    public PrintMethod<V> unregister() {
        super.unregister(this.parser);
        return this;
    }
}
