package queryGenerator;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DeleteQuery extends Query{
	
	private ArrayList<String> conditions;
	
	public DeleteQuery() {
		this.setCommand("DELETE FROM ");
	}
	
	public void addConditions(ArrayList<String> cond) {
		this.conditions = cond;
	}

	@Override
	public void build() {
		String tablesStr = tables.stream().collect(Collectors.joining(", "));
		String conditionStr = conditions.stream().collect(Collectors.joining(" AND "));
		sb.append(tablesStr).append(" WHERE ").append(conditionStr).append(";");
	}
}
