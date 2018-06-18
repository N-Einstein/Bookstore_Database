package queryGenerator;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQuery extends Query {

	private ArrayList<String> conditions;
	private Map<String, String> updates;

	public UpdateQuery() {
		this.setCommand("UPDATE ");
	}

	public void addUpdates(Map<String, String> updates) {
		this.updates = updates;
	}

	public void addConditions(ArrayList<String> cond) {
		conditions = cond;
	}

	@Override
	public void build() {
		String tablesStr = tables.stream().collect(Collectors.joining(", "));
		String conditionStr = conditions.stream().collect(Collectors.joining(" AND "));
		
		String res = updates.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
				.collect(Collectors.joining(", "));
		sb.append(tablesStr).append(" SET ").append(res).append(" WHERE ").append(conditionStr).append(";");
	}
}
