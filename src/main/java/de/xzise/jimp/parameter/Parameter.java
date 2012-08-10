package de.xzise.jimp.parameter;

import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.variables.Variables;

public class Parameter {

    public static final Parameter EMPTY_PARAMETER = new Parameter("", "", false);

    private final String entry;
    private final String full;
    public final boolean quoted;
    public final boolean finalValue;

    public Parameter(final String entry, final String full, final boolean quoted) {
        this(entry, full, quoted, true);
    }

    protected Parameter(final String entry, final String full, final boolean quoted, final boolean finalValue) {
        this.entry = entry;
        this.full = full;
        this.quoted = quoted;
        this.finalValue = finalValue;
    }

    protected <V extends Variables> ParameterType getInnerValue(final RuntimeOptions<V> runtime) {
        return new StringParameterType(this.entry);
    }

    public final <V extends Variables> ParameterType getValue(final RuntimeOptions<V> runtime) {
        final ParameterType result = this.getInnerValue(runtime);
        if (result == null) {
            return new StringParameterType(this.full);
        } else {
            return result;
        }
    }

    public String getText() {
        return this.entry;
    }

    public String getFullText() {
        return this.full;
    }
}