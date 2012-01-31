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

package de.xzise.jimp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.xzise.EqualCheck;
import de.xzise.MinecraftUtil;
import de.xzise.jimp.methods.ArrayMethod;
import de.xzise.jimp.methods.CaseCheckerMethod;
import de.xzise.jimp.methods.ConstantMethod;
import de.xzise.jimp.methods.CreateMethod;
import de.xzise.jimp.methods.IfArithmeticMethod;
import de.xzise.jimp.methods.IfCheckerMethod;
import de.xzise.jimp.methods.IfSetMethod;
import de.xzise.jimp.methods.IndefiniteArticleMethod;
import de.xzise.jimp.methods.MaximumMethod;
import de.xzise.jimp.methods.MinimumMethod;
import de.xzise.jimp.methods.PrintMethod;
import de.xzise.jimp.methods.PrintPrefixMethod;
import de.xzise.jimp.methods.RandomMethod;
import de.xzise.jimp.methods.RedirectMethod;
import de.xzise.jimp.methods.math.AddMethod;
import de.xzise.jimp.methods.math.SubtractMethod;
import de.xzise.jimp.parameter.OnceParsedParameter;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.ParameterTypeFactory;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.preset.DefaultMethod;
import de.xzise.jimp.preset.NamedMethod;
import de.xzise.jimp.variables.Variables;

public class MethodParser<V extends Variables> implements MethodRegistrator<V> {

    public static final int STOPPING_THRESHOLD = 100;
    public static final int WARNING_THRESHOLD = STOPPING_THRESHOLD * 9 / 10;

    private final class VariableEntry {
        public final boolean persistent;
        public final ParameterType value;
        
        public VariableEntry(final boolean persistent, final ParameterType value) {
            this.persistent = persistent;
            this.value = value;
        }
    }

    private static class MethodContainer<V extends Variables> {

        private final MethodParser<V> parser;
        protected final Map<String, Map<Integer, Method<? super V>>> methods = new HashMap<String, Map<Integer, Method<? super V>>>();

        public MethodContainer(final MethodParser<V> parser) {
            this.parser = parser;
        }

        /**
         * <p>
         * Returns a method with the specified name and parameter count. Negative
         * parameter counts means the minimum of allowed parameters. If there is no
         * method with the specified parameter count it will select the method which
         * can handle the parameter count but they may can handle more or less than
         * specified.
         * </p>
         * <p>
         * Example: The {@code paramCount} parameter is {@code 3} and there is no
         * method registered with 3 parameters it will try {@code -3}, {@code -2}
         * and {@code -1} parameter and selects the first found. Here does the
         * {@code -3} means that the method can handle 3 or more parameters.
         * </p>
         * 
         * @param name
         *            name of the method.
         * @param paramCount
         *            Number of parameters. Negative parameter count means that the
         *            method can also allow more than the specified value.
         * @return a method with the name and parameter count. If there wasn't a
         *         method found it will return null.
         */
        public Method<? super V> getMethod(final String name, final int paramCount) {
            // TODO: Case insensitive?
            if (name != null) {
                final int prefixLength;
                if (this.parser.getPrefix() == null) {
                    prefixLength = 0;
                } else {
                    prefixLength = this.parser.getPrefix().length();
                }
                Map<Integer, Method<? super V>> methods = this.methods.get(name.substring(prefixLength));
                if (methods != null) {
                    return getMethod(methods, paramCount);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        /**
         * Registers a new method.
         * 
         * @param name
         *            New name of the method. No spaces are allowed and without the
         *            prefix.
         * @param method
         *            New method.
         * @param paramCount
         *            The number of parameters the method is registered to. There
         *            could be negative values which allows more than the specified
         *            absolute value.
         * @return How many methods were already registered and overwritten.
         */
        protected int registerMethod(final String name, Method<? super V> method, int... paramCount) {
            paramCount = testParameters(name, paramCount);
            // TODO: Case insensitive?
            Map<Integer, Method<? super V>> methods = this.methods.get(name);
            if (methods == null) {
                methods = new HashMap<Integer, Method<? super V>>();
                this.methods.put(name, methods);
            }
            int failCount = 0;
            for (int i : paramCount) {
                if (methods.put(i, method) != null) {
                    failCount++;
                }
            }
            return failCount;
        }

        public static <V extends Variables> Method<? super V> getMethod(Map<Integer, Method<? super V>> methods, int paramCount) {
            if (methods.containsKey(paramCount)) {
                return methods.get(paramCount);
            } else {
                Method<? super V> method = null;
                paramCount = -Math.abs(paramCount);
                while (paramCount < 0 && method == null) {
                    method = methods.get(paramCount);
                    paramCount++;
                }
                return method;
            }
        }

        public static int[] testParameters(final String name, final int[] paramCount) {
            if (!MinecraftUtil.isSet(name)) {
                throw new IllegalArgumentException("Name has to be set (not null and not empty)!");
            } else if (name.contains(" ")) {
                throw new IllegalArgumentException("Name mustn't contain spaces.");
            } else if (name.contains("(") || name.contains(")")) {
                throw new IllegalArgumentException("Name mustn't contain brackets.");
            } else {
                if (paramCount.length == 0) {
                    return new int[] { -1, 0 };
                } else {
                    return paramCount;
                }
            }
        }
    }

    private static class MutableMethodContainer<V extends Variables> extends MethodContainer<V> {

        public MutableMethodContainer(final MethodParser<V> parser) {
            super(parser);
        }

        public void clear() {
            this.methods.clear();
        }

        /**
         * Registers a new method.
         * 
         * @param name
         *            New name of the method. No spaces are allowed and without the
         *            prefix.
         * @param method
         *            New method.
         * @param paramCount
         *            The number of parameters the method is registered to. There
         *            could be negative values which allows more than the specified
         *            absolute value.
         * @return How many methods were already registered and overwritten.
         */
        @Override
        public int registerMethod(final String name, Method<? super V> method, int... paramCount) {
            return super.registerMethod(name, method, paramCount);
        }

        public int unregisterMethod(final String name, int... paramCount) {
            paramCount = testParameters(name, paramCount);
            // TODO: Case insensitive?
            Map<Integer, Method<? super V>> methods = this.methods.get(name);
            int failCount = 0;
            if (methods != null) {
                for (int i : paramCount) {
                    if (methods.remove(i) != null) {
                        failCount++;
                    }
                }
                if (methods.isEmpty()) {
                    this.methods.remove(name);
                }
            }
            return failCount;
        }

        public void createRedirected(String name, String redirected, int... paramCounts) {
            final Collection<Integer> paramCountsSet;
            Map<Integer, Method<? super V>> methods = this.methods.get(redirected);
            if (paramCounts.length == 0) {
                if (methods != null) {
                    paramCountsSet = methods.keySet();
                } else {
                    paramCountsSet = new HashSet<Integer>(0);
                }
            } else {
                paramCountsSet = new HashSet<Integer>(paramCounts.length);
                for (int paramCount : paramCounts) {
                    paramCountsSet.add(paramCount);
                }
            }
            for (int paramCount : paramCounts) {
                Method<? super V> redirectedMethod = MethodContainer.getMethod(methods, paramCount);
                if (redirectedMethod != null) {
                    this.registerMethod(name, new RedirectMethod<V>(redirectedMethod), paramCount);
                }
            }
        }
    }

    private static final class EssentialMethodContainer<V extends Variables> extends MethodContainer<V> {

        public final CreateMethod createMethod;
        private final MethodRegistrator<V> registrator = new MethodRegistrator<V>() {
            @Override
            public int registerMethod(String name, Method<? super V> method, int... paramCount) {
                return EssentialMethodContainer.this.registerMethod(name, method, paramCount);
            }
        };

        public EssentialMethodContainer(final MethodParser<V> parser) {
            super(parser);
            this.createMethod = this.addNamedMethod(new CreateMethod());
        }

        private <M extends NamedMethod<? super V>> M addNamedMethod(M method) {
            method.register(this.registrator);
            return method;
        }

        @SuppressWarnings("unused") // Preparation
        private <M extends DefaultMethod<? super V>> M addDefaultMethod(final String name, M method) {
            method.register(name, this.registrator);
            return method;
        }

        @SuppressWarnings("unused") // Preparation
        private <M extends Method<? super V>> M addMethod(final String name, M method, int... paramCount) {
            this.registerMethod(name, method, paramCount);
            return method;
        }
    }

    private final MutableMethodContainer<V> methods;
    private final EssentialMethodContainer<V> essentialMethods;
    private final Map<String, VariableEntry> variables = new HashMap<String, VariableEntry>();
    private final Logger logger;
    private String prefix = "";

    public MethodParser(final Logger logger, final String prefix) {
        this.logger = logger;
        this.methods = new MutableMethodContainer<V>(this);
        this.essentialMethods = new EssentialMethodContainer<V>(this);
        this.setPrefix(prefix);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix has to be not null.");
        } else if (prefix.contains(" ")) {
            throw new IllegalArgumentException("Prefix mustn't contain spaces.");
        } else if (prefix.contains("(") || prefix.contains(")")) {
            throw new IllegalArgumentException("Prefix mustn't contain brackets.");
        }
        this.prefix = prefix;
    }

    public ParameterType getVariable(final String name) {
        VariableEntry entry = this.variables.get(name);
        return entry == null ? null : entry.value;
    }

    public void addParameterTypeFactory(final String name, final ParameterTypeFactory factory) {
        this.essentialMethods.createMethod.setFactory(name, factory);
    }

    public void setVariable(final String name, final ParameterType value, final boolean persistent) {
        this.variables.put(name, new VariableEntry(persistent, value));
    }

    public void unsetVariable(final String name) {
        this.variables.remove(name);
    }

    /**
     * Registers a new method.
     * 
     * @param name
     *            New name of the method. No spaces are allowed and without the
     *            prefix.
     * @param method
     *            New method.
     * @param paramCount
     *            The number of parameters the method is registered to. There
     *            could be negative values which allows more than the specified
     *            absolute value.
     * @return How many methods were already registered and overwritten.
     */
    public int registerMethod(final String name, Method<? super V> method, int... paramCount) {
        return this.methods.registerMethod(name, method, paramCount);
    }

    public int unregisterMethod(final String name, int... paramCount) {
        return this.methods.unregisterMethod(name, paramCount);
    }

    /**
     * <p>
     * Returns a method with the specified name and parameter count. Negative
     * parameter counts means the minimum of allowed parameters. If there is no
     * method with the specified parameter count it will select the method which
     * can handle the parameter count but they may can handle more or less than
     * specified.
     * </p>
     * <p>
     * Example: The {@code paramCount} parameter is {@code 3} and there is no
     * method registered with 3 parameters it will try {@code -3}, {@code -2}
     * and {@code -1} parameter and selects the first found. Here does the
     * {@code -3} means that the method can handle 3 or more parameters.
     * </p>
     * 
     * @param name
     *            name of the method.
     * @param paramCount
     *            Number of parameters. Negative parameter count means that the
     *            method can also allow more than the specified value.
     * @return a method with the name and parameter count. If there wasn't a
     *         method found it will return null.
     */
    public Method<? super V> getMethod(final String name, final int paramCount) {
        // TODO: Case insensitive?
        if (name != null) {
            Method<? super V> method = this.essentialMethods.getMethod(name, paramCount);
            if (method == null) {
                return this.methods.getMethod(name, paramCount);
            } else {
                return method;
            }
        } else {
            return null;
        }
    }

    public void clearMethods() {
        this.methods.clear();
    }

    private void cleanupVariables() {
        for (Entry<String, VariableEntry> entry : this.variables.entrySet()) {
            if (!entry.getValue().persistent) {
                this.variables.remove(entry.getKey());
            }
        }
    }

    public String parseLine(String line, V globalParameters) {
        String result = this.parseLine(line, globalParameters, 0).asString();
        this.cleanupVariables();
        return result;
    }

    public ParameterType parseLine(String line, V globalParameters, final int depth) {
        int index = 0;
        int start = -1;
        int delim = -1;
        int end = -1;
        int bracketLevel = 0;
        boolean quoted = false;
        char[] chars = new char[line.length()];
        int parameterLength = 0;
        List<String> parameters = new ArrayList<String>();
        while (index <= line.length()) {
            if (index < line.length()) {
                char c = line.charAt(index);
                if (bracketLevel > 0) {
                    if (bracketLevel == 1 && c == ',') {
                        int quotedStart = -1;
                        int quotedEnd = -1;
                        for (int i = 0; i < parameterLength; i++) {
                            if (chars[i] == '"') {
                                if (quotedStart < 0) {
                                    quotedStart = i;
                                } else {
                                    quotedEnd = i;
                                }
                            }
                        }
                        //@formatter:off
                        /*
                         *  0123456789
                         *    "hallo"
                         * 
                         * qs = 2 → 3
                         * qe = 8
                         */
                        //@formatter:on
                        quotedStart = quotedEnd < 0 || quotedStart < 0 ? 0 : quotedStart + 1;
                        quotedEnd = quotedEnd < 0 ? parameterLength : quotedEnd - quotedStart;
                        parameters.add(new String(chars, quotedStart, quotedEnd));
                        parameterLength = 0;
                    } else {
                        chars[parameterLength++] = c;
                    }
                }
                switch (c) {
                case '\\':
                    index++;
                    break;
                case '"':
                    quoted = !quoted;
                    break;
                case '(':
                    if (start >= 0) {
                        if (!quoted) {
                            if (bracketLevel == 0) {
                                delim = index;
                            }
                            bracketLevel++;
                        }
                    } else {
                        start = index;
                    }
                    break;
                case ')':
                    if (!quoted) {
                        if (bracketLevel > 0) {
                            bracketLevel--;
                            if (bracketLevel == 0) {
                                end = index;
                            }
                        }
                    }
                    break;
                case ' ':
                    if (bracketLevel == 0 && start >= 0) {
                        end = index - 1;
                    }
                    break;
                default:
                    if (start < 0) {
                        start = index;
                    }
                    break;
                }
            } else if (start >= 0) {
                bracketLevel = 0;
                end = index - 1;
            }

            if (start >= 0 && end >= start) {
                final int nameEnd;
                if (delim > start) {
                    nameEnd = delim - 1;
                    if (parameterLength > 0) {
                        parameters.add(new String(chars, 0, parameterLength));
                        parameterLength = 0;
                    }
                } else {
                    nameEnd = end;
                    parameters = new ArrayList<String>(0);
                }
                final String name = line.substring(start, nameEnd + 1);

                final Method<? super V> method;
                final String methodName;
                if (MinecraftUtil.isSet(this.prefix)) {
                    final int idx = name.indexOf(this.prefix);
                    if (idx > 0) {
                        start += idx;
                        methodName = name.substring(idx);
                    } else if (idx == 0) {
                        methodName = name;
                    } else {
                        methodName = null;
                    }
                } else {
                    methodName = name;
                }
                if (methodName != null) {
                    method = this.getMethod(methodName, parameters.size());
                } else {
                    method = null;
                }
                if (method != null) {
                    if (depth < STOPPING_THRESHOLD) {
                        if (depth >= WARNING_THRESHOLD) {
                            this.logger.warning("Deep method call of '" + name + "' at depth " + depth);
                        }
                        Parameter[] parameterObjects = new Parameter[parameters.size()];
                        for (int i = 0; i < parameters.size(); i++) {
                            parameterObjects[i] = OnceParsedParameter.create(this, parameters.get(i), globalParameters, depth + 1);
                        }
                        ParameterType replacement = null;
                        try {
                            replacement = method.call(parameterObjects, depth, globalParameters);
                        } catch (Exception e) {
                            this.logger.log(Level.WARNING, "Exception by calling '" + name + "'!", e);
                        }
                        if (replacement != null) {
                            final String prefix = line.substring(0, start);
                            final String suffix = substring(line, end + 1, line.length());
                            // This is the only statement
                            if (prefix.isEmpty() && suffix.isEmpty()) {
                                return replacement;
                            } else {
                                String replacementText = replacement.asString();
                                line = line.substring(0, start) + replacementText + substring(line, end + 1, line.length());
                                index += replacementText.length() - (end - start) - 1;
                            }
                        }
                    } else {
                        this.logger.severe("Didn't called method '" + name + "' at depth " + depth);
                    }
                }
                index++;
                end = -1;
                start = -1;
                delim = -1;
            } else {
                index++;
            }
        }

        return new StringParameterType(line);
    }

    public void loadDefaults() {
        PrintMethod.create(true, "call", this).register();
        PrintMethod.create(false, "print", this).register();

        // IfChecker
        this.registerMethod("ifequals", new IfCheckerMethod(EqualCheck.CLASSIC_EQUAL_CHECKER, false), 3, 4);
        this.registerMethod("ifnotequals", new IfCheckerMethod(EqualCheck.CLASSIC_EQUAL_CHECKER, true), 3, 4);
        this.registerMethod("ifequalsignorecase", new IfCheckerMethod(EqualCheck.STRING_IGNORE_CASE_EQUAL_CHECKER, false), 3, 4);
        this.registerMethod("ifnotequalsignorecase", new IfCheckerMethod(EqualCheck.STRING_IGNORE_CASE_EQUAL_CHECKER, true), 3, 4);
        this.registerMethod("ifset", new IfSetMethod(false), 2, 3);
        this.registerMethod("ifnotset", new IfSetMethod(true), 2, 3);

        new CaseCheckerMethod(EqualCheck.CLASSIC_EQUAL_CHECKER, "caseequals").register(this);

        this.registerMethod("ifgreaterequals", new IfArithmeticMethod(EqualCheck.GREATER_EQUAL_CHECKER), 3, 4);
        this.registerMethod("ifgreater", new IfArithmeticMethod(EqualCheck.GREATER_CHECKER), 3, 4);
        this.registerMethod("iflower", new IfArithmeticMethod(EqualCheck.LOWER_CHECKER), 3, 4);
        this.registerMethod("iflowerequals", new IfArithmeticMethod(EqualCheck.LOWER_EQUAL_CHECKER), 3, 4);

        this.registerMethod("random", new RandomMethod(), -1);

        ConstantMethod.NULL_METHOD.register(this);

        new ArrayMethod().register(this);
        new IndefiniteArticleMethod().register(this);
        new ConstantMethod("", "sp").register(this);
        new MaximumMethod(true).register(this);
        new MaximumMethod(false).register(this);
        new MinimumMethod(true).register(this);
        new MinimumMethod(false).register(this);
        new PrintPrefixMethod(this).register();

        new AddMethod().register(this);
        new SubtractMethod().register(this);
    }

    public void loadEssential() {
        new CreateMethod().register(this);
    }

    private static String substring(String input, int start, int end) {
        if (end < start) {
            return "";
        } else {
            return input.substring(start, end);
        }
    }

    public void createRedirected(String name, String redirected, int... paramCounts) {
        this.methods.createRedirected(name, redirected, paramCounts);
    }

    public boolean createRedirected(String name, String redirected, int paramCount) {
        Method<? super V> redirectedMethod = this.getMethod(redirected, paramCount);
        if (redirectedMethod != null) {
            this.registerMethod(name, new RedirectMethod<V>(redirectedMethod), paramCount);
            return true;
        } else {
            return false;
        }
    }

    public static class RedirectedElement {
        public final int paramCount;
        public final String name;
        public final String redirected;

        public RedirectedElement(String name, String redirected, int paramCount) {
            this.name = name;
            this.redirected = redirected;
            this.paramCount = paramCount;
        }
    }

    public void createRedirected(List<RedirectedElement> elements) {
        List<RedirectedElement> buffer = null;
        List<RedirectedElement> output = new ArrayList<RedirectedElement>(elements);
        int startSize;
        do {
            startSize = output.size();
            buffer = new ArrayList<RedirectedElement>(startSize);
            for (RedirectedElement redirectedElement : output) {
                if (!this.createRedirected(redirectedElement.name, redirectedElement.redirected, redirectedElement.paramCount)) {
                    buffer.add(redirectedElement);
                }
            }
            output = buffer;
        } while (startSize > buffer.size());
        elements.clear();
        elements.addAll(output);
    }
}