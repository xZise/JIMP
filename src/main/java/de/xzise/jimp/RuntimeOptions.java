package de.xzise.jimp;

import java.util.logging.Logger;

import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public class RuntimeOptions<V extends Variables> {

    public final V variables;
    public final MethodParser<V> parser;
    public final Logger logger;
    private int depth = 0;

    public RuntimeOptions(final V variables, final MethodParser<V> parser, final Logger logger) {
        this.variables = variables;
        this.parser = parser;
        this.logger = logger;
    }

    public ParameterType call(final Method<? super V> method, final Parameter[] parameters) {
        this.depth++;
        final ParameterType result = method.call(parameters, this);
        this.depth--;
        return result;
    }

    public int getDepth() {
        return this.depth;
    }
}
