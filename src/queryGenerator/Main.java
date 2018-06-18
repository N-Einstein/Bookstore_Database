package queryGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		DeleteQuery q = new DeleteQuery();
		q.setTables(new ArrayList<String>(Arrays.asList("BOOK")));
		q.addConditions(new ArrayList<String>(Arrays.asList("title = \"aloha\"", "price = 50")));
		q.build();
		System.out.println(q.toString());
		
		
		UpdateQuery q6 = new UpdateQuery();
		q6.setTables(new ArrayList<String>(Arrays.asList("Author")));
		q6.addConditions(new ArrayList<String>(Arrays.asList("name = \"Steve\"")));
		Map<String, String> m = new HashMap<>();
		m.put("name", "nada");
		m.put("age", "22");
		q6.addUpdates(m);
		q6.build();
		System.out.println(q6.toString());
		
		
		InsertQuery q2 = new InsertQuery();
		q2.setTables(new ArrayList<String>(Arrays.asList("Publisher")));
		q2.addRecord(new ArrayList<String>(Arrays.asList("pinguin", "551236")));
		q2.build();
		System.out.println(q2.toString());
		
		InsertQuery q3 = new InsertQuery();
		q3.setTables(new ArrayList<String>(Arrays.asList("Publisher")));
		q3.addRecord(new ArrayList<String>(Arrays.asList("name", "phone")), 
				new ArrayList<String>(Arrays.asList("pinguin", "52369874")));
		q3.build();
		System.out.println(q3.toString());
		
		
		SelectQuery q4 = new SelectQuery();
		q4.setTables(new ArrayList<String>(Arrays.asList("Book")));
		q4.addConditions(new ArrayList<String>(Arrays.asList("title = \"aloha\"", "price = 50")));
		Map<String, Order> m2 = new HashMap<>();
		m2.put("price", Order.ASC);
		m2.put("year", Order.DESC);
		q4.setOrder(m2);
		q4.build();
		System.out.println(q4.toString());
	}
}
