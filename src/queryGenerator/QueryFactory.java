package queryGenerator;

public class QueryFactory {
	
	private static QueryFactory qf;
	
	private QueryFactory() {
		
	}
	
	public static QueryFactory getInstance() {
		if(qf == null) {
			qf = new QueryFactory();
		}
		return qf;
	}
	
	public Query makeQuery(QueryType type) {
		switch(type) {
		case INSERT:
			return new InsertQuery();
		case SELECT:
			return new SelectQuery();
		case UPDATE:
			return new UpdateQuery();
		case DELETE:
			return new DeleteQuery();
		default:
			return null;
		}
	}
}
