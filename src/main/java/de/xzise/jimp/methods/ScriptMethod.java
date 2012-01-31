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

package de.xzise.jimp.methods;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.xzise.jimp.Method;

import de.xzise.MinecraftUtil;
import de.xzise.jimp.parameter.Parameter;
import de.xzise.jimp.parameter.types.BooleanParameterType;
import de.xzise.jimp.parameter.types.DoubleParameterType;
import de.xzise.jimp.parameter.types.LongParameterType;
import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;
import de.xzise.jimp.variables.Variables;

public class ScriptMethod implements Method<Variables> {

    private final Invocable invocable;
    private final String methodName;
    private final Logger logger;

    public ScriptMethod(final String methodName, final Invocable invocable, final Logger logger) {
        this.invocable = invocable;
        this.methodName = methodName;
        this.logger = logger;
    }

    public static ScriptMethod create(final String engineName, final String methodName, final Reader reader, final ScriptEngineManager engineManager, final Logger logger) {
        ScriptEngine engine = getEngine(engineName, reader, engineManager, logger);
        if (engine instanceof Invocable) {
            return new ScriptMethod(methodName, (Invocable) engine, logger);
        } else {
            return null;
        }
    }

    public static ScriptEngine getEngine(final String name, final Reader reader, final ScriptEngineManager engineManager, final Logger logger) {
        ScriptEngine engine = engineManager.getEngineByName(name);
        try {
            engine.eval(reader);
        } catch (ScriptException e) {
            engine = null;
            logger.log(Level.WARNING, "Unable to evaluate script.", e);
        }
        return engine;
    }

    @Override
    public ParameterType call(Parameter[] parameters, int depth, Variables globalParameters) {
        Object result = null;
        try {
            result = this.invocable.invokeFunction(this.methodName, parameters, globalParameters);
        } catch (ScriptException e) {
            this.logger.log(Level.WARNING, "Unable to call '" + this.methodName + "(Parameter[], Variables)'!", e);
        } catch (NoSuchMethodException e) {
            this.logger.log(Level.WARNING, "No such method named '" + this.methodName + "(Parameter[], Variables)'!", e);
        }
        // Test the result for the different types
        if (result instanceof Number) {
            Number number = (Number) result;
            if (number instanceof Double || number instanceof Float || number instanceof BigDecimal) {
                return new DoubleParameterType(number.doubleValue(), MinecraftUtil.MAX_TWO_DECIMALS_FORMAT);
            } else {
                return new LongParameterType(number.longValue());
            }
        } else if (result instanceof Boolean) {
            return new BooleanParameterType((Boolean) result);
        } else if (result != null) {
            return new StringParameterType(result.toString());
        } else {
            return null;
        }
    }

}
