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
import de.xzise.jimp.variables.Variables;

/**
 * Extended default implementation which adds support for a default method name.
 */
public abstract class DefaultNamedMethod<V extends Variables> extends DefaultMethod<V> implements NamedMethod<V> {

    private final String defaultName;

    public DefaultNamedMethod(final String defaultName, final int paramCount, final int... paramCounts) {
        super(paramCount, paramCounts);
        this.defaultName = defaultName;
    }

    public final DefaultNamedMethod<V> register(MethodParser<? extends V> parser) {
        super.register(this.defaultName, parser);
        return this;
    }

    public final DefaultNamedMethod<V> unregister(MethodParser<? extends V> parser) {
        super.unregister(this.defaultName, parser);
        return this;
    }
}
