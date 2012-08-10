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

import java.util.Comparator;

import de.xzise.EqualCheck;
import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.preset.IfMethod;
import de.xzise.jimp.variables.Variables;

public class IfCheckerMethod extends IfMethod<Variables> {

    private final EqualCheck<? super String> checker;

    public IfCheckerMethod(EqualCheck<? super String> checker, boolean inverted) {
        super(2, inverted);
        this.checker = checker;
    }

    public IfCheckerMethod(Comparator<String> checker, boolean inverted) {
        this((EqualCheck<String>) new EqualCheck.ComparatorEqualChecker<String>(checker), inverted);
    }

    @Override
    protected Boolean match(final Parameter[] preValues, final RuntimeOptions<?> runtime) {
        return this.checker.equals(preValues[0].getValue(runtime).asString(), preValues[1].getValue(runtime).asString());
    }
}
