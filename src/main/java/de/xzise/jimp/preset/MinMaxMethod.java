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

package de.xzise.jimp.preset;

import java.util.ArrayList;

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.LongParameterType;
import de.xzise.jimp.parameter.types.NativeParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public abstract class MinMaxMethod extends DefaultNamedMethod<Variables> {

    private final boolean first;

    protected MinMaxMethod(final boolean first, final String defaultName) {
        super(first ? defaultName : "n" + defaultName, first ? -1 : -2);
        this.first = first;
    }

    protected abstract boolean compare(final long nHighLowest, final long tested);

    @Override
    public ParameterType call(final Parameter[] parameters, final RuntimeOptions<? extends Variables> runtime) {
        if (parameters.length > 0) {
            final Long count = this.first ? 1 : NativeParameterType.asLong(parameters[0].getValue(runtime));
            if (count != null) {
                final int intCount = count.intValue();
                ArrayList<Long> longs = new ArrayList<Long>(parameters.length);
                for (int i = (this.first ? 0 : 1); i < parameters.length; i++) {
                    Long longBuffer = NativeParameterType.asLong(parameters[i].getValue(runtime));
                    if (longBuffer != null) {
                        longs.add(longBuffer);
                    }
                }
                if (longs.size() >= intCount) {
                    final long[] highest = new long[intCount];
                    int filled = 0;
                    for (Long longBuffer : longs) {
                        int i;
                        for (i = 0; i < filled && this.compare(highest[i], longBuffer); i++) {
                        }
                        if (i < highest.length) {
                            for (int j = Math.min(filled, highest.length - 1); i < j; j--) {
                                highest[j] = highest[j - 1];
                            }
                            highest[i] = longBuffer;
                            filled = Math.min(highest.length, filled + 1);
                        }
                    }
                    return new LongParameterType(highest[intCount - 1]);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
