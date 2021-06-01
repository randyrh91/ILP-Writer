package org.knime.rankaggregation.factory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.rankaggregation.format.GenerateGeneralLPX;
import org.knime.rankaggregation.format.GenerateReductedConstraintsLP;
import org.knime.rankaggregation.format.AbstractGenerateILPFile;
import org.knime.rankaggregation.format.GenerateGeneralLP;
import org.knime.rankaggregation.format.GenerateReductedConstraintsLPX;
import org.knime.rankaggregation.format.GenerateReductedVariablesLP;
import org.knime.rankaggregation.format.GenerateReductedVariablesLPX;

/**
 * @author Randy Reyna Hernández
 */

public class GenerateILPFactory extends AbstractGenerateILPFactory {

	private AbstractGenerateILPFile file;

	@Override
	public void generateLPX(String tipo, String variante, BufferedDataTable tableSpecInput, Path localPath, URL url,
			ExecutionContext exec) throws IOException, CanceledExecutionException {
		String variante_tipo = variante.trim() + "_" + tipo.trim();
		switch (variante_tipo) {
		case "Original Formulation_LPX (LiPS)":
			file = new GenerateGeneralLPX(tableSpecInput, localPath, url, exec);
			break;
		case "Reduced Variables Formulation_LPX (LiPS)":
			file = new GenerateReductedVariablesLPX(tableSpecInput, localPath, url, exec);
			break;
		case "Reduced Variables and Constraints Formulation_LPX (LiPS)":
			file = new GenerateReductedConstraintsLPX(tableSpecInput, localPath, url, exec);
			break;
		case "Original Formulation_LP (SCIP)":
			file = new GenerateGeneralLP(tableSpecInput, localPath, url, exec);
			break;
		case "Reduced Variables Formulation_LP (SCIP)":
			file = new GenerateReductedVariablesLP(tableSpecInput, localPath, url, exec);
			break;
		case "Reduced Variables and Constraints Formulation_LP (SCIP)":
			file = new GenerateReductedConstraintsLP(tableSpecInput, localPath, url, exec);
			break;
		default:
			file = new GenerateGeneralLPX(tableSpecInput, localPath, url, exec);
			break;
		}
		file.generate();
	}

}
