package ExceptionHandler;

public class ForeignKeyException extends Exception {

	private static final long serialVersionUID = 1L;

	public ForeignKeyException(String msg) {				
		super(msg);
	}
}
