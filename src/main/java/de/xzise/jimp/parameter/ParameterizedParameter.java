package de.xzise.jimp.parameter;

import de.xzise.jimp.Method;
import de.xzise.jimp.RuntimeOptions;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.variables.Variables;

public class ParameterizedParameter extends Parameter {
        public final String methodName;
        private final Parameter[] parameters;

        public ParameterizedParameter(final String methodName, final String entry, final boolean quoted, final Parameter[] parameters) {
            super(entry, entry, quoted, false);
            this.methodName = methodName;
            this.parameters = parameters;
        }

        @Override
        protected <V extends Variables> ParameterType getInnerValue(final RuntimeOptions<V> runtime) {
            final Method<? super V> method = runtime.parser.getMethod(this.methodName, this.parameters.length);
            if (method != null) {
                return runtime.call(method, this.parameters);
            } else {
                return null;
//                throw AssemblingException.createMethodNotFound("method", this.entry, this.parameters.length);
            }
        }
    }