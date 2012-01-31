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

import java.util.Arrays;
import java.util.Map;

import de.xzise.MinecraftUtil;
import de.xzise.bukkit.util.callback.Callback;
import de.xzise.collections.ArrayReferenceList;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.variables.Variables;

public abstract class CaseMethod<V extends Variables, C extends V> extends DefaultCastedNamedMethod<V, C> {

    public static enum CaseEnum {
        LOWER_CASE("lower"),
        UPPER_CASE("upper"),
        CAMEL_CASE("camel"),
        FIRST_UPPER("first"),
        CUSTOM("custom"),
        NONE("none");

        public final String name;

        private CaseEnum(final String name) {
            this.name = name;
        }

        public static final Map<String, CaseEnum> CASE_ENUMS = MinecraftUtil.createReverseMutableEnumMap(CaseEnum.class, new Callback<String, CaseEnum>() {
            @Override
            public String call(CaseEnum caseEnum) {
                return caseEnum.name.toLowerCase();
            }
        });
    }

    private final int preValueCount;

    public CaseMethod(final int preValueCount, final String defaultName, final Class<? extends C> variableClass) {
        super(defaultName, variableClass, 0, 1, 3);
        this.preValueCount = preValueCount;
    }

    @Override
    public ParameterType innerCall(Parameter[] parameters, C globalParameters) {
        CaseEnum caseEnum = null;
        if (parameters.length == 0) {
            caseEnum = CaseEnum.NONE;
        } else if (parameters.length == 1 || parameters.length == 3) {
            caseEnum = CaseEnum.CASE_ENUMS.get(parameters[0].parse().asString().toLowerCase());
        }
        if ((caseEnum == CaseEnum.CUSTOM && parameters.length == 3) || (caseEnum != CaseEnum.CUSTOM && caseEnum != null && parameters.length == 1)) {
            Parameter[] preValues = Arrays.copyOf(parameters, preValueCount);
            final String result = this.call(preValues, globalParameters);
            switch (caseEnum) {
            case CUSTOM:
                final char[] upperTrigger = parameters[1].parse().asString().toCharArray();
                final char[] upperReceiver = parameters[2].parse().asString().toCharArray();
                final char[] processedCustom = new char[result.length()];
                int indexCustom = 0;
                boolean makeUpperCustom = true;
                for (char c : result.toCharArray()) {
                    if (ArrayReferenceList.contains(c, upperTrigger)) {
                        makeUpperCustom = true;
                    } else if (makeUpperCustom && ArrayReferenceList.contains(c, upperReceiver)) {
                        c = Character.toUpperCase(c);
                        makeUpperCustom = false;
                    }
                    processedCustom[indexCustom++] = c;
                }
                return new StringParameterType(new String(processedCustom));
            case CAMEL_CASE:
                final char[] processed = new char[result.length()];
                int index = 0;
                boolean makeUpper = true;
                for (char c : result.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        makeUpper = true;
                    } else if (makeUpper && Character.isLetter(c)) {
                        c = Character.toUpperCase(c);
                        makeUpperCustom = false;
                    }
                    processed[index++] = c;
                }
                return new StringParameterType(new String(processed));
            case FIRST_UPPER:
                return new StringParameterType(CaseMethod.toCapitalCase(result, false));
            case UPPER_CASE:
                return new StringParameterType(result.toUpperCase());
            case LOWER_CASE:
                return new StringParameterType(result.toLowerCase());
            case NONE:
                return new StringParameterType(result);
            default:
                return null;
            }
        } else {
            return null;
        }
    }

    public static String toCapitalCase(final String string, final boolean restToLowercase) {
        final char[] chars = string.toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i]) && !found) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (restToLowercase) {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }
        return new String(chars);
    }

    protected abstract String call(Parameter[] preValues, C globalParameters);
}
