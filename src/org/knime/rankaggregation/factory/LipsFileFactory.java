package org.knime.rankaggregation.factory;

import java.net.URL;
import java.nio.file.Path;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.rankaggregation.format.AbstractGeneralFile;
import org.knime.rankaggregation.format.AbstractReductedConstraintsFile;
import org.knime.rankaggregation.format.AbstractReductedVariablesFile;
import org.knime.rankaggregation.format.GeneralLPX;
import org.knime.rankaggregation.format.ReductedConstraintsLPX;
import org.knime.rankaggregation.format.ReductedVariablesLPX;

public class LipsFileFactory extends ILPFactory{

	public LipsFileFactory(BufferedDataTable tableSpecInput, Path localPath, URL url, ExecutionContext exec) {
		super(tableSpecInput, localPath, url, exec);
		
	}

	@Override
	public AbstractGeneralFile createGeneralFile() {
		return new GeneralLPX(tableSpecInput, localPath, url, exec);
	}

	@Override
	public AbstractReductedVariablesFile createReductedVariablesFile() {
		return new ReductedVariablesLPX(tableSpecInput, localPath, url, exec);
	}

	@Override
	public AbstractReductedConstraintsFile createReductedConstraintsFile() {
		return new ReductedConstraintsLPX(tableSpecInput, localPath, url, exec);
	}

}
