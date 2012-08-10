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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import de.xzise.jimp.methods.PrintMethod;
import de.xzise.jimp.methods.PrintPrefixMethod;
import de.xzise.jimp.methods.RandomMethod;
import de.xzise.jimp.methods.RedirectMethod;
import de.xzise.jimp.methods.math.AddMethod;
import de.xzise.jimp.methods.math.MaximumMethod;
import de.xzise.jimp.methods.math.MinimumMethod;
import de.xzise.jimp.methods.math.RoundMethod;
import de.xzise.jimp.methods.math.SubtractMethod;
import de.xzise.jimp.methods.var.IsVarPersistent;
import de.xzise.jimp.methods.var.IsVarSet;
import de.xzise.jimp.methods.var.ReturnVarMethod;
import de.xzise.jimp.methods.var.SetVarMethod;
import de.xzise.jimp.methods.var.SetVarPersistency;
import de.xzise.jimp.methods.var.UnsetVarMethod;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.ParameterizedParameter;
import de.xzise.jimp.parameter.types.BooleanParameterType;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.LongParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.ParameterTypeFactory;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.util.ArrayUtil;
import de.xzise.jimp.util.FillingArray;
import de.xzise.jimp.util.Util;
import de.xzise.jimp.variables.Variables;

public class MethodParser<V extends Variables> {

    public static final int STOPPING_THRESHOLD = 100;
    public static final int WARNING_THRESHOLD = STOPPING_THRESHOLD * 9 / 10;

    private final static Map<String, ParameterTypeFactory> DEFAULT_FACTORIES = new HashMap<String, ParameterTypeFactory>(5);

    static {
        DEFAULT_FACTORIES.put("long", LongParameterType.LONG_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("string", StringParameterType.STRING_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("double", DoubleParameterType.DOUBLE_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("boolean", BooleanParameterType.BOOLEAN_PARAMETER_TYPE_FACTORY);
        DEFAULT_FACTORIES.put("array", ArrayMethod.INSTANCE);
    }

    private static final class PersistableEntry<T> {
        public boolean persistent;
        public final T value;
        
        public PersistableEntry(final boolean persistent, final T value) {
            this.persistent = persistent;
            this.value = value;
        }

        public static <K, V> void clear(final Map<K, PersistableEntry<V>> map, final boolean persistentOnly) {
            for (Entry<K, PersistableEntry<V>> entry : map.entrySet()) {
                if (entry.getValue().persistent == persistentOnly) {
                    map.remove(entry.getKey());
                }
            }
        }
    }

    private static final PersistableEntry<?> NULL_ENTRY = new PersistableEntry<Object>(false, null);

    private static <V extends Variables> Method<? super V> getMethod(Map<Integer, PersistableEntry<Method<? super V>>> methods, int paramCount) {
        if (methods.containsKey(paramCount)) {
            return methods.get(paramCount).value;
        } else {
            Method<? super V> method = null;
            paramCount = -Math.abs(paramCount);
            while (paramCount < 0 && method == null) {
                method = methods.get(paramCount).value;
                paramCount++;
            }
            return method;
        }
    }

    public void clear() {
        for (Entry<String, Map<Integer, PersistableEntry<Method<? super V>>>> entry : this.methods.entrySet()) {
            PersistableEntry.clear(entry.getValue(), false);
            if (entry.getValue().isEmpty()) {
                this.methods.remove(entry.getKey());
            }
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

    private final Map<String, Map<Integer, PersistableEntry<Method<? super V>>>> methods = new HashMap<String, Map<Integer, PersistableEntry<Method<? super V>>>>();
    private final Map<String, PersistableEntry<ParameterType>> variables = new HashMap<String, PersistableEntry<ParameterType>>();
    private final Map<String, ParameterTypeFactory> factories = new HashMap<String, ParameterTypeFactory>();
    private final Logger logger;

    private String prefix = "";
    private DecimalFormat defaultFormat = MinecraftUtil.MAX_TWO_DECIMALS_FORMAT;

    public MethodParser(final Logger logger, final String prefix) {
        this.logger = logger;
        this.setPrefix(prefix);
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

    public DecimalFormat getDefaultFormat() {
        return this.defaultFormat;
    }

    public void setDefaultFormat(final DecimalFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Default format has to be not null.");
        }
        this.defaultFormat = format;
    }

    @SuppressWarnings("unchecked")
    private PersistableEntry<ParameterType> getVariableEntry(final String name) {
        final PersistableEntry<ParameterType> entry = this.variables.get(name);
        return entry == null ? ((PersistableEntry<ParameterType>) NULL_ENTRY) : entry;
    }

    public ParameterType getVariable(final String name) {
        return this.getVariableEntry(name).value;
    }

    public void addParameterTypeFactory(final String name, final ParameterTypeFactory factory) {
        this.setFactory(name, factory);
    }

    public void setVariable(final String name, final ParameterType value, final boolean persistent) {
        this.variables.put(name, new PersistableEntry<ParameterType>(persistent, value));
    }

    public void unsetVariable(final String name) {
        this.variables.remove(name);
    }

    public void setPersistency(final String name, final boolean persistent) {
        final PersistableEntry<ParameterType> entry = this.variables.get(name);
        if (entry != null) {
            entry.persistent = persistent;
        }
    }

    public boolean isPersistent(final String name) {
        return this.getVariableEntry(name).persistent;
    }

    public boolean isVariableSet(final String name) {
        return this.variables.containsKey(name);
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
        paramCount = testParameters(name, paramCount);
        // TODO: Case insensitive?
        Map<Integer, PersistableEntry<Method<? super V>>> methods = this.methods.get(name);
        if (methods == null) {
            methods = new HashMap<Integer, PersistableEntry<Method<? super V>>>();
            this.methods.put(name, methods);
        }
        int failCount = 0;
        for (int i : paramCount) {
            final PersistableEntry<Method<? super V>> entry = methods.get(name);
            if (!entry.persistent) {
                if (methods.put(i, new PersistableEntry<Method<? super V>>(false, method)) != null) {
                    failCount++;
                }
            }
        }
        return failCount;
    }

    public int unregisterMethod(final String name, int... paramCount) {
        paramCount = testParameters(name, paramCount);
        // TODO: Case insensitive?
        final Map<Integer, PersistableEntry<Method<? super V>>> methods = this.methods.get(name);
        int failCount = 0;
        if (methods != null) {
            for (int i : paramCount) {
                final PersistableEntry<Method<? super V>> entry = methods.get(i);
                if (!entry.persistent) {
                    if (methods.remove(i) != null) {
                        failCount++;
                    }
                }
            }
            if (methods.isEmpty()) {
                this.methods.remove(name);
            }
        }
        return failCount;
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
            if (this.getPrefix() == null) {
                prefixLength = 0;
            } else {
                prefixLength = this.getPrefix().length();
            }
            Map<Integer, PersistableEntry<Method<? super V>>> methods = this.methods.get(name.substring(prefixLength));
            if (methods != null) {
                return getMethod(methods, paramCount);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void clearMethods() {
        this.methods.clear();
    }

    private void cleanupVariables() {
        PersistableEntry.clear(this.variables, false);
    }

    public static class Compiled {

        private final Parameter[] entries;

        public Compiled(final Parameter[] entries) {
            this.entries = entries;
        }

        public String execute(final RuntimeOptions<?> runtime) {
            final ParameterType result = this.executeOnly(runtime);
            runtime.parser.cleanupVariables();
            return result.asString();
        }

        public ParameterType executeOnly(final RuntimeOptions<?> runtime) {
            if (this.entries.length == 1) {
                return this.entries[0].getValue(runtime);
            } else {
                final StringBuilder builder = new StringBuilder();
                for (int i = 0; i < this.entries.length; i++) {
                    builder.append(this.entries[i].getValue(runtime).asString());
                }
                return new StringParameterType(builder.toString());
            }
        }
    }

    public String execute(final Compiled compiled, final V globalParameters) {
        return compiled.execute(new RuntimeOptions<V>(globalParameters, this, this.logger));
    }

    public String execute(final String line, final V globalParameters) {
        return this.execute(MethodParser.compile(line), globalParameters);
    }

    public static Compiled compile(final String line) {
        final Parameter[] parsed = MethodParser.parseLine(line, new char[] { ',' }, new char[] { ' ' }, '"', '\\', '(', ')', null, true);
        return new Compiled(parsed);
    }

    private static Parameter[] parseLine(final String line, final char[] paramDelimiter, final char[] delimiter, final char quote, final char escape, final Character bracketStart, final Character bracketEnd, final Character commentary, final boolean trimQuotes) {
        final List<Parameter> values = new ArrayList<Parameter>();
        MethodParser.getParameters(values, line, 0, paramDelimiter, delimiter, quote, escape, bracketStart, bracketEnd, commentary, trimQuotes);
        return values.toArray(new Parameter[0]);
    }

    private static int getParameters(final List<Parameter> parameters, final String line, final int start, final char[] paramDelimiter, final char[] delimiter, final char quote, final char escape, final Character bracketStart, final Character bracketEnd, final Character commentary, final boolean trimQuotes) {
        boolean quoted = false;
        boolean quotedEntry = false;
        boolean escaped = false;
        int bracketLevel = 0;
        int quotedWordLength = -1;
        int quotedLastStart = -1;
        final FillingArray<Character> word = new FillingArray<Character>(new Character[line.length()]);
        int entryStart = 0;
        for (int i = start; i <= line.length(); i++) {
            final char c;
            if (i < line.length()) {
                c = line.charAt(i);
            } else {
                c = delimiter[0];
            }
            if (escaped) {
                word.add(c);
                escaped = false;
            } else {
                if (c == quote && bracketLevel <= 0) {
                    if (trimQuotes) {
                        if (quoted) {
                            quotedWordLength = word.getFilledLength();
                        } else if (quotedLastStart < 0) {
                            quotedLastStart = word.getFilledLength();
                        }
                    }
                    quoted = !quoted;
                    if (quoted) {
                        quotedEntry = true;
                    }
                } else if (c == escape) {
                    escaped = true;
                } else if ((ArrayUtil.indexOf(delimiter, c) >= 0 || Util.equals(bracketStart, c) || Util.equals(bracketEnd, c) || Util.equals(commentary, c)) && !quoted) {
                    if (word.getFilledLength() > 0) {
                        final int first = quotedLastStart < 0 ? 0 : quotedLastStart;
                        final int last = quotedWordLength < 0 ? word.getFilledLength() : (quotedWordLength - first);
                        final String string = ArrayUtil.getString(word.getFilledArray(), first, last);
                        if (Util.equals(bracketStart, c)) {
                            final List<Parameter> subParameters = new ArrayList<Parameter>();
                            i = getParameters(subParameters, line, i + 1, paramDelimiter, paramDelimiter, quote, escape, bracketStart, bracketEnd, commentary, trimQuotes);
                            final String full = line.substring(entryStart, i);
                            parameters.add(new ParameterizedParameter(string, full, quotedEntry, subParameters.toArray(new Parameter[0])));
                        } else {
                            final String full = line.substring(entryStart, i);
                            parameters.add(new Parameter(string, full, quotedEntry));
                        }
                        entryStart = i;
                        word.clearFill();
                        quotedWordLength = -1;
                        quotedEntry = false;
                    }
                    if (Util.equals(commentary, c)) {
                        return line.length();
                    } else if (Util.equals(bracketEnd, c)) {
                        return i;
                    }
                    quotedLastStart = -1;
                } else if (Util.equals(commentary, c) && !quoted) {
                    break;
                } else {
                    word.add(c);
                }
            }
        }
        return line.length();
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

        // Var methods
        new IsVarPersistent().register(this);
        new IsVarSet().register(this);
        new ReturnVarMethod<V>(true).register(this);
        new ReturnVarMethod<V>(false).register(this);
        new SetVarMethod<V>(true).register(this);
        new SetVarMethod<V>(false).register(this);
        new SetVarPersistency(true).register(this);
        new SetVarPersistency(false).register(this);
        new UnsetVarMethod<V>().register(this);

        // Math methods
        new AddMethod(this).register(this);
        new SubtractMethod(this).register(this);
        new RoundMethod(this).register(this);
    }

    public void loadEssential() {
        new CreateMethod().register(this);
    }

    public void createRedirected(String name, String redirected, int... paramCounts) {
        final Collection<Integer> paramCountsSet;
        Map<Integer, PersistableEntry<Method<? super V>>> methods = this.methods.get(redirected);
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
            Method<? super V> redirectedMethod = MethodParser.getMethod(methods, paramCount);
            if (redirectedMethod != null) {
                this.registerMethod(name, new RedirectMethod<V>(redirectedMethod), paramCount);
            }
        }
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