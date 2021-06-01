package org.knime.rankaggregation.factory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;

/**
 * @author Randy Reyna Hernández
 */

public abstract class AbstractGenerateILPFactory {
	public AbstractGenerateILPFactory() {
	}

	public abstract void generateLPX(String tipo, String variante, BufferedDataTable tableSpecInput, Path localPath,
			URL url, ExecutionContext exec) throws IOException, CanceledExecutionException;
}
