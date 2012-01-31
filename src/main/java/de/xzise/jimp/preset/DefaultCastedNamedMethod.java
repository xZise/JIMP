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

import de.xzise.jimp.MethodParser;
import de.xzise.jimp.MethodRegistrator;
import de.xzise.jimp.variables.Variables;

public abstract class DefaultCastedNamedMethod<V extends Variables, C extends V> extends DefaultCastedMethod<V, C> implements NamedMethod<V> {

    private final String defaultName;

    public DefaultCastedNamedMethod(final String defaultName, final Class<? extends C> variableClass, final int paramCount, final int... paramCounts) {
        super(variableClass, paramCount, paramCounts);
        this.defaultName = defaultName;
    }

    public final DefaultCastedNamedMethod<V, C> register(MethodRegistrator<? extends V> registrator) {
        super.register(this.defaultName, registrator);
        return this;
    }

    public final DefaultCastedNamedMethod<V, C> unregister(MethodParser<? extends V> parser) {
        super.unregister(this.defaultName, parser);
        return this;
    }

}
