package queryGenerator;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectQuery extends Query {

	private Map<String, Order> order;
	private ArrayList<String> conditions;
	private String limit = "", offset = "";

	public SelectQuery() {
		this.setCommand("SELECT * FROM ");
	}

	public void setOrder(Map<String, Order> map) {
		order = map;
	}

	public void addConditions(ArrayList<String> cond) {
		conditions = cond;
		for (String t : tables) {
			conditions.add(t + ".valid = 1");
		}
	}

	public void setLimit(String val) {
		limit = val;
	}

	public void setOffset(String val) {
		offset = val;
	}

	public Map<String, Order> getOrder() {
		return order;
	}

	public ArrayList<String> getConditions() {
		return conditions;
	}

	@Override
	public void build() {
		String tablesStr = tables.stream().collect(Collectors.joining(", "));
		String conditionStr = conditions.stream().collect(Collectors.joining(" AND "));
		sb.append(tablesStr).append(" WHERE ").append(conditionStr);
		if (order != null) {
			String res = order.entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue())
					.collect(Collectors.joining(", "));
			sb.append(" ORDER BY ").append(res);
		}
		if (!limit.equals("")) {
			sb.append(" LIMIT ").append(limit);
		}
		if (!offset.equals("")) {
			sb.append(" Offset ").append(offset);
		}
		sb.append(";");
	}
}
