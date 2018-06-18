package queryGenerator;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class InsertQuery extends Query {

	private ArrayList<String> cols, vals;

	public InsertQuery() {
		this.setCommand("INSERT INTO ");
	}

	public void addRecord(ArrayList<String> keys, ArrayList<String> values) {
		cols = keys;
		vals = values;
	}

	public void addRecord(ArrayList<String> values) {
		vals = values;
	}

	@Override
	public void build() {
		String tablesStr = tables.stream().collect(Collectors.joining(", "));
		String colStr = (cols == null) ? "" : " (" + cols.stream().collect(Collectors.joining(", ")) + ")";
		String valStr = "(" + vals.stream().collect(Collectors.joining(", ")) + ")";
		sb.append(tablesStr).append(colStr).append(" VALUES ").append(valStr).append(";");
	}
}
