package queryGenerator;

import java.util.ArrayList;

public abstract class Query {
	
	protected StringBuilder sb;
	protected ArrayList<String> tables;
	
	public Query() {
		sb = new StringBuilder();
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

	public void setCommand(String command) {
		sb.append(command);
	}

	public ArrayList<String> getTables() {
		return tables;
	}

	public void setTables(ArrayList<String> tables) {
		this.tables = tables;
	}
	
	public abstract void build();
}
