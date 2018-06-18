package utilities;

import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JFrame;

import dataTransfer.DBConnector;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;

public class JavaCallJasperReport extends JFrame {

	private static final long serialVersionUID = 1L;

	public void showReport(String name) throws JRException, ClassNotFoundException, SQLException {

		String reportSrcFile = name + ".jrxml";

		DBConnector con = new DBConnector();

		// First, compile jrxml file.
		JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
		// Fields for report
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, con.getConnection());
		JRViewer viewer = new JRViewer(print);
		viewer.setOpaque(true);
		viewer.setVisible(true);
		this.add(viewer);
		this.setSize(700, 500);
		this.setVisible(true);

		con.close();
	}
}