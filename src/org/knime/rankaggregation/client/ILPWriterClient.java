package org.knime.rankaggregation.client;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.rankaggregation.factory.ILPFactory;
import org.knime.rankaggregation.factory.LipsFileFactory;
import org.knime.rankaggregation.factory.ScipFileFactory;

public class ILPWriterClient {
	
	private ILPFactory factory;
	private URL m_url;
	private Path localPath;
	private BufferedDataTable table;
	ExecutionContext exec;

	
	public ILPWriterClient(URL m_url, Path localPath, BufferedDataTable table, ExecutionContext exec) {
		super();
		this.m_url = m_url;
		this.localPath = localPath;
		this.table = table;
		this.exec = exec;
	}


	public ILPFactory getFactory() {
		return factory;
	}


	public void setFactory(ILPFactory factory) {
		this.factory = factory;
	}


	public URL getM_url() {
		return m_url;
	}


	public void setM_url(URL m_url) {
		this.m_url = m_url;
	}


	public Path getLocalPath() {
		return localPath;
	}


	public void setLocalPath(Path localPath) {
		this.localPath = localPath;
	}


	public BufferedDataTable getTable() {
		return table;
	}


	public void setTable(BufferedDataTable table) {
		this.table = table;
	}


	public ExecutionContext getExec() {
		return exec;
	}


	public void setExec(ExecutionContext exec) {
		this.exec = exec;
	}


	public ILPFactory generateFile(String typeFile) throws IOException, CanceledExecutionException {
		switch (typeFile) {
		case "Original Formulation_LPX (LiPS)":
			factory = new LipsFileFactory(table, localPath, m_url, exec);
			factory.createGeneralFile().generate();
			break;
		case "Reduced Variables Formulation_LPX (LiPS)":
			factory = new LipsFileFactory(table, localPath, m_url, exec);
			factory.createReductedVariablesFile().generate();
			break;
		case "Reduced Variables and Constraints Formulation_LPX (LiPS)":
			factory = new LipsFileFactory(table, localPath, m_url, exec);
			factory.createReductedConstraintsFile().generate();
			break;
		case "Original Formulation_LP (SCIP)":
			factory = new ScipFileFactory(table, localPath, m_url, exec);
			factory.createGeneralFile().generate();
			break;
		case "Reduced Variables Formulation_LP (SCIP)":
			factory = new ScipFileFactory(table, localPath, m_url, exec);
			factory.createReductedVariablesFile().generate();
			break;
		case "Reduced Variables and Constraints Formulation_LP (SCIP)":
			factory = new ScipFileFactory(table, localPath, m_url, exec);
			factory.createReductedConstraintsFile().generate();
			break;
		default:
			factory = new LipsFileFactory(table, localPath, m_url, exec);
			factory.createGeneralFile().generate();
			break;
		}
		return factory;
	}
}
