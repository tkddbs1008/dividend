package zerobase.dividend.exception;

@SuppressWarnings("serial")
public abstract class AbstractException extends RuntimeException {
	
	abstract public int getStatusCode();
	abstract public String getMessage();
}
