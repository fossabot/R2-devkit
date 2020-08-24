package org.r2.devkit.exception.runtime;

import org.r2.devkit.exception.abs.AbstractRuntimeException;

/**
 * 方法入参异常，继承自{@code AbstractRuntimeException}
 *
 * @author ruan4261
 */
public class ArgRuntimeException extends AbstractRuntimeException {

    public ArgRuntimeException() {
        super();
    }

    public ArgRuntimeException(String message) {
        super(message);
    }

    public ArgRuntimeException(Throwable throwable) {
        super(throwable);
    }

}
