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

import java.util.HashMap;
import java.util.Map;

import de.xzise.MinecraftUtil;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.BooleanParameterType;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.LongParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.ParameterTypeFactory;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class CreateMethod extends DefaultNamedMethod<Variables> {

    private final static Map<String, ParameterTypeFactory> DEFAULT_FACTORIES = new HashMap<String, ParameterTypeFactory>(5);

    static {
        DEFAULT_FACTORIES.put("long", LongParameterType.LONG_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("string", StringParameterType.STRING_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("double", DoubleParameterType.DOUBLE_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("boolean", BooleanParameterType.BOOLEAN_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("array", ArrayMethod.INSTANCE);
    }

    private final Map<String, ParameterTypeFactory> factories = new HashMap<String, ParameterTypeFactory>();

    public CreateMethod() {
        super("create", -1);
    }

    public static ParameterTypeFactory getDefaultFactory(final String name) {
        return DEFAULT_FACTORIES.get(name.toLowerCase());
    }

    public ParameterTypeFactory getFactory(final String name) {
        final ParameterTypeFactory factory = getDefaultFactory(name);
        if (factory == null) {
            return this.factories.get(name.toLowerCase());
        } else {
            return factory;
        }
    }

    public boolean setFactory(final String name, final ParameterTypeFactory factory) {
        return this.factories.put(name.toLowerCase(), factory) != null;
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, Variables globalParameters) {
        if (parameters.length >= 1) {
            ParameterTypeFactory factory = getFactory(parameters[0].parse().asString());
            if (factory != null) {
                return factory.create(MinecraftUtil.subArray(parameters, 1), globalParameters);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
