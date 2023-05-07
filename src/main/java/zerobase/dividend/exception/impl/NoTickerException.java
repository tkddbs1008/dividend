package zerobase.dividend.exception.impl;

import org.springframework.http.HttpStatus;

import zerobase.dividend.exception.AbstractException;

@SuppressWarnings("serial")
public class NoTickerException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "티커(Ticker)를 적어주세요";
    }
}
