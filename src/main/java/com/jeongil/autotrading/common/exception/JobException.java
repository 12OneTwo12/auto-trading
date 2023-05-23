package com.jeongil.autotrading.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class JobException extends RuntimeException {

	private static final long serialVersionUID = -1314871354637859546L;

	public JobException(BindingResult bindingResult) {
		super(bindingResult.getAllErrors().get(0).getDefaultMessage());
	}

	public JobException(String message) {
		super(message);
	}

	public static JobException ofError(String error) {
		return new JobException(error);
	}
}
