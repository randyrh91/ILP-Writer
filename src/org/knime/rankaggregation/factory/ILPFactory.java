package org.knime.rankaggregation.factory;

import java.net.URL;
import java.nio.file.Path;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.rankaggregation.ILPWriterNodeModel;
import org.knime.rankaggregation.format.AbstractGeneralFile;
import org.knime.rankaggregation.format.AbstractReductedConstraintsFile;
import org.knime.rankaggregation.format.AbstractReductedVariablesFile;

public abstract class ILPFactory {
	
	public static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);
	protected BufferedDataTable tableSpecInput;
	protected Path localPath;
	protected URL url;
	protected ExecutionContext exec;
	
	public ILPFactory(BufferedDataTable tableSpecInput, Path localPath, URL url, ExecutionContext exec) {
		super();
		this.tableSpecInput = tableSpecInput;
		this.localPath = localPath;
		this.url = url;
		this.exec = exec;
	}
	public BufferedDataTable getTableSpecInput() {
		return tableSpecInput;
	}
	public void setTableSpecInput(BufferedDataTable tableSpecInput) {
		this.tableSpecInput = tableSpecInput;
	}
	public Path getLocalPath() {
		return localPath;
	}
	public void setLocalPath(Path localPath) {
		this.localPath = localPath;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public ExecutionContext getExec() {
		return exec;
	}
	public void setExec(ExecutionContext exec) {
		this.exec = exec;
	}
	public static NodeLogger getLogger() {
		return LOGGER;
	}
	
	public abstract AbstractGeneralFile createGeneralFile();
	public abstract AbstractReductedVariablesFile createReductedVariablesFile();
	public abstract AbstractReductedConstraintsFile createReductedConstraintsFile();
}
