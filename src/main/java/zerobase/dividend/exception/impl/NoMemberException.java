package zerobase.dividend.exception.impl;

import org.springframework.http.HttpStatus;

import zerobase.dividend.exception.AbstractException;

@SuppressWarnings("serial")
public class NoMemberException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 사용자입니다.";
    }
}
