package org.knime.rankaggregation.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.rankaggregation.ILPWriterNodeModel;

import java.nio.file.Path;

/**
 * 
 * @author Randy Reyna Hernández
 * 
 */

public class GenerateGeneralLP extends AbstractGenerateILPFile {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);
	private BufferedDataTable tableSpecInput;
	private Path localPath;
	private URL url;
	private ExecutionContext exec;

	public GenerateGeneralLP(BufferedDataTable tableSpecInput, Path localPath, URL url, ExecutionContext exec) {
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
			writer.write("Minimize");
			writer.write("\n");
			int rowCont = 1;
			String objFunct = "";
			ArrayList<String> variables = new ArrayList<String>();
			for (DataRow row : this.tableSpecInput) {
				int numbCell = row.getNumCells();
				for (int columnCount = rowCont; columnCount < numbCell; columnCount++) {
					double valueCell = (((DoubleValue) row.getCell(columnCount)).getDoubleValue());
					double dataCell = (double) Math.round(valueCell * 100000000d) / 100000000d;
					String Xba = "X" + (columnCount + 1) + "_" + (rowCont);
					String Xab = "X" + (rowCont) + "_" + (columnCount + 1);
					String Qab = dataCell + " ";
					String Qba = Math.round((1 - dataCell) * 100000000d) / 100000000d + " ";
					String Sab = Qab + Xba + " + " + Qba + Xab;
					variables.add(Xba);
					variables.add(Xab);
					if (objFunct.compareTo("") == 0) {
						objFunct = " obj: " + Sab;
					} else {
						objFunct += " + " + Sab;
					}
				}
				rowCont++;
			}
			writer.write(" " + objFunct.trim());
			writer.write("\n");
			writer.write("Bounds");
			writer.write("\n");
			for (int i = 0; i < variables.size(); i++) {
				String variable = variables.get(i);
				String condicion = " " + variable + " <= 1";
				writer.write(condicion);
				writer.write("\n");
			}
			writer.write("Subject To");
			writer.write("\n");
			int rowId = 1;
			for (int i = 0; i < variables.size(); i = i + 2) {
				String Xba = variables.get(i);
				String Xab = variables.get(i + 1);
				String condicion = " c" + rowId++ + ": " + Xab + " + " + Xba + " = 1";
				writer.write(condicion);
				writer.write("\n");
				condicion = " c" + rowId++ + ": " + Xba + " + " + Xab + " = 1";
				writer.write(condicion);
				writer.write("\n");
			}
			for (int i = 0; i < variables.size(); i++) {
				String varIzq = variables.get(i);
				String[] arrVarIzq = varIzq.split("_");
				int noFin = Integer.parseInt(arrVarIzq[1]);
				for (int j = 1; j <= numColumns; j++) {
					String varCentro = "X" + noFin + "_" + j;
					String primerNumeroArr = arrVarIzq[0].substring(1, arrVarIzq[0].length());
					int ultimo = Integer.parseInt(primerNumeroArr);
					if (varIzq.compareTo(varCentro) != 0 && j != noFin && ultimo != j) {
						String varDer = "X" + j + "_" + ultimo;
						String condicion = " c" + rowId + ": " + varIzq + " + " + varCentro + " + " + varDer + " >= 1";
						writer.write(condicion);
						writer.write("\n");
						rowId++;
					}

				}
			}
			writer.write("Binaries");
			writer.write("\n");
			String condicion = " ";
			for (int i = 0; i < variables.size(); i++) {
				String variable;
				if (i == variables.size() - 1) {
					variable = variables.get(i);
				} else {
					variable = variables.get(i) + " ";
				}
				condicion += " " + variable;
			}
			writer.write(" " + condicion.trim()+ " End");
			exec.checkCanceled();
		} catch (CanceledExecutionException ex) {
			if (localPath != null) {
				Files.deleteIfExists(localPath);
				LOGGER.debug("File '" + localPath + "' deleted.");
			}
			throw ex;
		}

	}

}
