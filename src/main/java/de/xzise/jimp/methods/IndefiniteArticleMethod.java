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

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class IndefiniteArticleMethod extends DefaultNamedMethod<Variables> {

    public IndefiniteArticleMethod() {
        super("an", 1);
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        if (parameters.length == 1) {
            char letter = parameters[0].getValue(runtime).asString().trim().charAt(0);
            final boolean vowel = (letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u');
            return new StringParameterType((vowel ? "an " : "a ") + parameters[0]);
        } else {
            return null;
        }
    }

}
