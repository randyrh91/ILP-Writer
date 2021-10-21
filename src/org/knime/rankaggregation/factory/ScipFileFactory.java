package org.knime.rankaggregation.factory;

import java.net.URL;
import java.nio.file.Path;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.rankaggregation.format.AbstractGeneralFile;
import org.knime.rankaggregation.format.AbstractReductedConstraintsFile;
import org.knime.rankaggregation.format.AbstractReductedVariablesFile;
import org.knime.rankaggregation.format.GeneralLP;
import org.knime.rankaggregation.format.ReductedConstraintsLP;
import org.knime.rankaggregation.format.ReductedVariablesLP;

public class ScipFileFactory extends ILPFactory{

	public ScipFileFactory(BufferedDataTable tableSpecInput, Path localPath, URL url, ExecutionContext exec) {
		super(tableSpecInput, localPath, url, exec);
	}

	@Override
	public AbstractGeneralFile createGeneralFile() {
		return new GeneralLP(tableSpecInput, localPath, url, exec);
	}

	@Override
	public AbstractReductedVariablesFile createReductedVariablesFile() {
		return new ReductedVariablesLP(tableSpecInput, localPath, url, exec);
	}

	@Override
	public AbstractReductedConstraintsFile createReductedConstraintsFile() {
		return new ReductedConstraintsLP(tableSpecInput, localPath, url, exec);
	}

}
