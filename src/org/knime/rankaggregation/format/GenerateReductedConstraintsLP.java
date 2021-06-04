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

public class GenerateReductedConstraintsLP extends AbstractGenerateILPFile {
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);
	private BufferedDataTable tableSpecInput;
	private Path localPath;
	private URL url;
	private ExecutionContext exec;

	public GenerateReductedConstraintsLP(BufferedDataTable tableSpecInput, Path localPath, URL url,
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
			writer.write("Minimize");
			writer.write("\n");
			int rowCont = 1;
			String objFunct = "";
			ArrayList<String> realVariables = new ArrayList<String>();
			double total = 0;
			for (DataRow row : this.tableSpecInput) {
				int numbCell = row.getNumCells();
				for (int columnCount = rowCont; columnCount < numbCell; columnCount++) {
					double dataCell = (double) Math
							.round((((DoubleValue) row.getCell(columnCount)).getDoubleValue()) * 100000000d)
							/ 100000000d;
					String Xab = "X" + (rowCont) + "_" + (columnCount + 1);
					double Qab = (Math.round((1 - (2 * dataCell)) * 100000000d) / 100000000d);
					total += dataCell;
					String Sab = Qab + " " + Xab;
					realVariables.add(Xab);

					if (objFunct.compareTo("") == 0) {
						objFunct = "obj: " + Sab;
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
			writer.write(" " + objFunct + "+" + total + " F");
			writer.write("\n");
			writer.write("Bounds");
			writer.write("\n");
			writer.write(" F = 1");
			writer.write("\n");
			for (int i = 0; i < realVariables.size(); i++) {
				String variable = realVariables.get(i);
				String condicion = " " + variable + " <= 1";
				writer.write(condicion);
				writer.write("\n");
			}
			writer.write("Subject To");
			writer.write("\n");
			int rowId = 1;
			for (int i = 0; i < realVariables.size(); i++) {
				String varIzq = realVariables.get(i);
				String[] arrVarIzq = varIzq.split("_");
				String primerNumeroArr = arrVarIzq[0].substring(1, arrVarIzq[0].length());
				int noIniVarIzq = Integer.parseInt(primerNumeroArr);
				int noFinVarIzq = Integer.parseInt(arrVarIzq[1]);
				for (int j = i + 1; j < realVariables.size(); j++) {
					String varCentro = realVariables.get(j);
					String[] arrVarCentro = varCentro.split("_");
					String primerNumeroArrCentro = arrVarCentro[0].substring(1, arrVarCentro[0].length());
					int noIniVarCent = Integer.parseInt(primerNumeroArrCentro);
					int noFinVarCent = Integer.parseInt(arrVarCentro[1]);
					if (noFinVarIzq == noIniVarCent) {
						String varDer = "X" + noIniVarIzq + "_" + noFinVarCent;
						String condicion1 = " c" + rowId++ + ": " + varIzq + " + " + varCentro + " - " + varDer
								+ " >= 0";
						writer.write(condicion1);
						writer.write("\n");
						String condicion2 = " c" + rowId++ + ": " + varIzq + " + " + varCentro + " - " + varDer
								+ " <= 1";
						writer.write(condicion2);
						writer.write("\n");
					}
				}
			}
			writer.write("Binaries");
			writer.write("\n");
			String condicion = "";
			for (int i = 0; i < realVariables.size(); i++) {
				String variable;
				if (i == realVariables.size() - 1) {
					variable = realVariables.get(i);
				} else {
					variable = realVariables.get(i) + " ";
				}

				condicion += " " + variable;
			}
			writer.write(" " + condicion.trim()+ " End");
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
