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

import de.xzise.MinecraftUtil;
import de.xzise.jimp.MethodParser;
import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultMethod;
import de.xzise.jimp.variables.Variables;

public class PrintPrefixMethod extends DefaultMethod<Variables> {

    private final MethodParser<?> parser;

    public PrintPrefixMethod(final MethodParser<?> parser) {
        super(0);
        this.parser = parser;
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        if (parameters.length == 0) {
            return new StringParameterType(this.parser.getPrefix());
        } else {
            return null;
        }
    }

    public PrintPrefixMethod register() {
        if (MinecraftUtil.isSet(this.parser.getPrefix())) {
            super.register(this.parser.getPrefix(), this.parser);
        }
        return this;
    }

    public PrintPrefixMethod unregister() {
        if (MinecraftUtil.isSet(this.parser.getPrefix())) {
            super.unregister(this.parser.getPrefix(), this.parser);
        }
        return this;
    }
}
