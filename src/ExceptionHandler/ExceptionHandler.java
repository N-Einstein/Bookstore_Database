package ExceptionHandler;

public class ExceptionHandler {

	private static ExceptionHandler instance;

	private ExceptionHandler() {

	}

	public static ExceptionHandler getHandler() {
		if (instance == null)
			instance = new ExceptionHandler();
		return instance;
	}

	public void handle(Exception e) throws Exception {
		String str = e.toString();
		String toFind;
		int start;
		if (isForeignKeyError(str)) {
			toFind = "Foreign Key Constraint";
			start = str.indexOf(toFind);
			throw new ForeignKeyException(str.substring(start));
		}
		if (isPrimaryKeyError(str)) {
			toFind = "Duplicate";
			start = str.indexOf(toFind);
			throw new PrimaryException(str.substring(start));
		}
		if (isTrigger(str)) {
			toFind = "trigger";
			start = str.indexOf(toFind);
			throw new TriggerException(str.substring(start));
		}
		if (isNull(str)) {
			toFind = "Field";
			start = str.indexOf(toFind);
			throw new NullException(str.substring(start));
		}
		throw (e);
	}

	private boolean isNull(String str) {
		return (str.matches(".*default.*"));
	}

	private boolean isTrigger(String str) {
		return (str.matches("(trigger).*"));
	}

	private boolean isPrimaryKeyError(String str) {
		return str.matches(".*PRIMARY.*");
	}

	private boolean isForeignKeyError(String str) {
		return str.matches(".*FOREIGN.*");
	}

}