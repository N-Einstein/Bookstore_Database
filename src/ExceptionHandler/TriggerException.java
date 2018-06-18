package ExceptionHandler;

public class TriggerException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public TriggerException(String msg) {
		super(msg.replaceAll("trigger ", ""));
	}
}
