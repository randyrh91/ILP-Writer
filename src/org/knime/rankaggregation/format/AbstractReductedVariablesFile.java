package org.knime.rankaggregation.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.FileUtil;
import org.knime.rankaggregation.ILPWriterNodeModel;

public abstract class AbstractReductedVariablesFile {

	protected static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);
	protected BufferedDataTable tableSpecInput;
	protected Path localPath;
	protected URL url;
	protected ExecutionContext exec;
	
	public AbstractReductedVariablesFile(BufferedDataTable tableSpecInput, Path localPath, URL url,
			ExecutionContext exec) {
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

	public abstract void generate() throws IOException, CanceledExecutionException;

	protected BufferedWriter openWriter(final Path localPath, final URL url) throws IOException {
		if (localPath != null) {
			return Files.newBufferedWriter(localPath, Charset.forName("UTF-8"));
		} else {
			OutputStream os = FileUtil.openOutputConnection(url, "PUT").getOutputStream();
			return new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		}
	}
}
