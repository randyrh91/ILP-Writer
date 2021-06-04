package org.knime.rankaggregation.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.rankaggregation.ILPWriterNodeModel;

/**
 * 
 * @author Randy Reyna Hernández
 * 
 */

public class GenerateReductedVariablesLPX extends AbstractGenerateILPFile {
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);
	private BufferedDataTable tableSpecInput;
	private Path localPath;
	private URL url;
	private ExecutionContext exec;

	public GenerateReductedVariablesLPX(BufferedDataTable tableSpecInput, Path localPath, URL url,
			ExecutionContext exec) {
		this.tableSpecInput = tableSpecInput;
		this.localPath = localPath;
		this.url = url;
		this.exec = exec;
	}

	@Override
	public void generate() throws IOException, CanceledExecutionException {
		DataTableSpec columns = this.tableSpecInput.getDataTableSpec();
		int numColumns = columns.getNumColumns();
		try (BufferedWriter writer = openWriter(localPath, url)) {
			writer.write("\n");
			for (int c = 1; c <= numColumns; c++) {
				String columnName = columns.getColumnSpec(c - 1).getName();
				writer.write("// " + c + " => " + columnName);
				writer.write("\n");
			}
			writer.write("\n");
			writer.write("// Objective Function");
			writer.write("\n");
			int rowCont = 1;
			String objFunct = "";
			ArrayList<String> realVariables = new ArrayList<String>();
			ArrayList<String> todasVariables = new ArrayList<String>();
			double total = 0;
			for (DataRow row : this.tableSpecInput) {
				int numbCell = row.getNumCells();
				for (int columnCount = rowCont; columnCount < numbCell; columnCount++) {
					double dataCell = (double) Math
							.round((((DoubleValue) row.getCell(columnCount)).getDoubleValue()) * 100000000d)
							/ 100000000d;
					String Xab = "X" + (rowCont) + "_" + (columnCount + 1);
					String Xba = "X" + (columnCount + 1) + "_" + (rowCont);
					double Qab = (Math.round((1 - (2 * dataCell)) * 100000000d) / 100000000d);
					total += dataCell;
					String Sab = Qab + "*" + Xab;
					realVariables.add(Xab);
					todasVariables.add(Xab);
					todasVariables.add(Xba);
					if (objFunct.compareTo("") == 0) {
						objFunct = "min: " + Sab;
					} else {
						if (Qab < 0) {
							objFunct += Sab;
						} else {
							objFunct += "+" + Sab;
						}
					}
				}
				rowCont++;
			}
			writer.write(objFunct + "+" + total + "*F;");
			writer.write("\n");
			writer.write("\n");
			writer.write("// Variable bounds");
			writer.write("\n");
			writer.write("row1: F = 1;");
			writer.write("\n");
			int rowId = 2;
			for (int i = 0; i < realVariables.size(); i++) {
				String variable = realVariables.get(i);
				String condicion = "row" + rowId + ": " + variable + " <= 1;";
				writer.write(condicion);
				writer.write("\n");
				rowId++;
			}
			writer.write("\n");
			writer.write("// Second Constraints");
			writer.write("\n");
			for (int i = 0; i < todasVariables.size(); i++) {
				String varIzq = todasVariables.get(i);
				String[] arrVarIzq = varIzq.split("_");
				int noFin = Integer.parseInt(arrVarIzq[1]);
				for (int j = 1; j <= numColumns; j++) {
					String varCentro = "X" + noFin + "_" + j;
					String varCentroInversa = "X" + j + "_" + noFin;
					String primerNumeroArr = arrVarIzq[0].substring(1, arrVarIzq[0].length());
					int ultimo = Integer.parseInt(primerNumeroArr);
					String varIzqInversa = "X" + noFin + "_" + ultimo;
					if (varIzq.compareTo(varCentro) != 0 && j != noFin && ultimo != j) {
						String varDer = "X" + j + "_" + ultimo;
						String varDerInversa = "X" + ultimo + "_" + j;
						int miembroDerecho = 1;
						String condicion = "row" + rowId + ": ";
						if (noFin < ultimo) {
							miembroDerecho--;
							condicion += "- " + varIzqInversa + " ";
						} else {
							condicion += varIzq + " ";
						}
						if (noFin > j) {
							miembroDerecho--;
							condicion += "- " + varCentroInversa + " ";
						} else {
							condicion += "+ " + varCentro + " ";
						}
						if (j > ultimo) {
							miembroDerecho--;
							condicion += "- " + varDerInversa + " ";
						} else {
							condicion += "+ " + varDer + " ";
						}
						condicion += ">= " + miembroDerecho + ";";
						writer.write(condicion);
						writer.write("\n");
						rowId++;
					}
				}
			}
			writer.write("\n");
			writer.write("// Integer Definitions");
			writer.write("\n");
			String condicion = "int ";
			for (int i = 0; i < realVariables.size(); i++) {
				String variable;
				if (i == realVariables.size() - 1) {
					variable = realVariables.get(i);
				} else {
					variable = realVariables.get(i) + ",";
				}

				condicion += " " + variable;
			}
			writer.write(condicion.trim() + ";");
			writer.write("\n");
			exec.checkCanceled();
		} catch (

		CanceledExecutionException ex) {
			if (localPath != null) {
				Files.deleteIfExists(localPath);
				LOGGER.debug("File '" + localPath + "' deleted.");
			}
			throw ex;
		}
	}
}
