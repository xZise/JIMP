package de.xzise.jimp.parameter;

import de.xzise.jimp.parameter.types.ParameterType;
import de.xzise.jimp.parameter.types.StringParameterType;

public class FinalParameter implements Parameter {

	public static final FinalParameter EMPTY_PARAMETER = new FinalParameter("");
	public static final FinalParameter NULL_PARAMETER = new FinalParameter(null);

	public final String parameterValue;

	public FinalParameter(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public ParameterType parse() {
		return new StringParameterType(this.parameterValue);
	}

	@Override
	public String getText() {
		return this.parameterValue;
	}

}
