package org.tensin.sonos.control;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.model.types.CharacterDatatype;
import org.fourthline.cling.model.types.DoubleDatatype;
import org.fourthline.cling.model.types.FloatDatatype;
import org.fourthline.cling.model.types.IntegerDatatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.ShortDatatype;
import org.fourthline.cling.model.types.StringDatatype;
import org.fourthline.cling.model.types.URIDatatype;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytesDatatype;
import org.fourthline.cling.model.types.UnsignedIntegerOneByteDatatype;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytesDatatype;

/**
 * The Class SonosActionInvocation.
 */
public class SonosActionInvocation extends ActionInvocation {

    /**
     * Instantiates a new sonos action invocation.
     * 
     * @param action
     *            the action
     */
    public SonosActionInvocation(final Action action) {
        super(action);
    }

    /**
     * Instantiates a new sonos action invocation.
     * 
     * @param action
     *            the action
     * @param input
     *            the input
     */
    public SonosActionInvocation(final Action action, final ActionArgumentValue[] input) {
        super(action, input);
    }

    /**
     * Instantiates a new sonos action invocation.
     * 
     * @param action
     *            the action
     * @param input
     *            the input
     * @param output
     *            the output
     */
    public SonosActionInvocation(final Action action, final ActionArgumentValue[] input, final ActionArgumentValue[] output) {
        super(action, input, output);
    }

    /**
     * Instantiates a new sonos action invocation.
     * 
     * @param failure
     *            the failure
     */
    public SonosActionInvocation(final ActionException failure) {
        super(failure);
    }

    /**
     * Gets the output as integer.
     * 
     * @param argumentName
     *            the argument name
     * @return the output as integer
     */
    public int getOutputAsInteger(final String argumentName) {
        ActionArgumentValue s = super.getOutput(argumentName);
        if (s != null) {
            if (s.getValue() != null) {
                return Integer.valueOf(s.getValue().toString()).intValue();
            }
        }
        return 0;
    }

    /**
     * Gets the output as string.
     * 
     * @param argumentName
     *            the argument name
     * @return the output as string
     */
    public String getOutputAsString(final String argumentName) {
        ActionArgumentValue s = super.getOutput(argumentName);
        if (s != null) {
            if (s.getValue() != null) {
                return s.getValue().toString();
            }
        }
        return "";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teleal.cling.model.action.ActionInvocation#setInput(java.lang.String, java.lang.Object)
     */
    public void setInput(final String argumentName, final String value) throws InvalidValueException {
        // super.setInput(argumentName, value);
        final ActionArgument argument = getInputArgument(argumentName);
        Object convertedValue = value;
        if (argument.getDatatype() instanceof StringDatatype) {
            convertedValue = new StringDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof UnsignedIntegerFourBytesDatatype) {
            convertedValue = new UnsignedIntegerFourBytesDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof UnsignedIntegerOneByteDatatype) {
            convertedValue = new UnsignedIntegerOneByteDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof UnsignedIntegerTwoBytesDatatype) {
            convertedValue = new UnsignedIntegerTwoBytesDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof BooleanDatatype) {
            convertedValue = new BooleanDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof CharacterDatatype) {
            convertedValue = new CharacterDatatype().valueOf(value);
            // } else if (argument.getDatatype() instanceof DateTimeDatatype) {
            // convertedValue = new DateTimeDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof DoubleDatatype) {
            convertedValue = new DoubleDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof FloatDatatype) {
            convertedValue = new FloatDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof IntegerDatatype) {
            convertedValue = new IntegerDatatype(4).valueOf(value);
        } else if (argument.getDatatype() instanceof ShortDatatype) {
            convertedValue = new ShortDatatype().valueOf(value);
        } else if (argument.getDatatype() instanceof URIDatatype) {
            convertedValue = new URIDatatype().valueOf(value);
        }
        setInput(new ActionArgumentValue(getInputArgument(argumentName), convertedValue));
    }
}
