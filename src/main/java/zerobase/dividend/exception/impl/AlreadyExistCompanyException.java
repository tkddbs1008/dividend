package zerobase.dividend.exception.impl;

import org.springframework.http.HttpStatus;

import zerobase.dividend.exception.AbstractException;

@SuppressWarnings("serial")
public class AlreadyExistCompanyException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 회사명 입니다.";
    }

}
