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

import de.xzise.jimp.Method;
import de.xzise.jimp.MethodParser;
import de.xzise.jimp.MethodRegistrator;
import de.xzise.jimp.variables.Variables;

public interface NamedMethod<V extends Variables> extends Method<V> {

    NamedMethod<V> register(MethodRegistrator<? extends V> registrator);

    NamedMethod<V> unregister(MethodParser<? extends V> parser);
}
