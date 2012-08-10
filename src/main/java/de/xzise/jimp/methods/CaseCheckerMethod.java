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

import de.xzise.EqualCheck;
import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.preset.DefaultNamedMethod;
import de.xzise.jimp.variables.Variables;

public class CaseCheckerMethod extends DefaultNamedMethod<Variables> {

    private final EqualCheck<? super String> checker;

    public CaseCheckerMethod(final EqualCheck<? super String> checker, final String name) {
        super(name, -2);
        this.checker = checker;
    }

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<?> runtime) {
        //@formatter:off
	    /*
	     * case(
	     * 0    <value>,
	     * 1    <cond 1>,
	     * 2    <call 1>,
	     * 3    <cond 2>,
	     * 4    <call 2>,
	     *   …,
	     * 2n+1 <cond n>,
	     * 2n+2 <call n>,
	     * 2n+3 [default call]
	     * )
	     * 
	     * cond idx = section number * 2 + 1
	     * call idx = cond idx + 1
	     * default call idx = (cond idx + 1)
	     * section count = (parameter count - 2) / 2
	     */
		//@formatter:on
        if (parameters.length >= 2) {
            for (int i = 0; i < (parameters.length - 2) / 2; i++) {
                if (this.checker.equals(parameters[0].getValue(runtime).asString(), parameters[i * 2 + 1].getValue(runtime).asString())) {
                    return parameters[i * 2 + 2].getValue(runtime);
                }
            }
            if (parameters.length % 2 == 0) {
                return parameters[parameters.length - 1].getValue(runtime);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
