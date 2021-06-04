package org.knime.rankaggregation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.util.FileUtil;
import org.knime.rankaggregation.factory.GenerateILPFactory;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * 
 * @author Randy Reyna Hernández
 * 
 *         This is the model implementation of ILPWriter.
 * 
 */
public class ILPWriterNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ILPWriterNodeModel.class);

	private static final int IN_PORT = 0;
	private static final String[] ALL_FORMAT = { "soc", "soi", "toc", "toi", "tog", "mjg", "pwg", "wmg" };
	protected static final String CFGKEY_FORMAT = "format";
	protected static final String CFGKEY_VARIANT = "variant";
	protected static final String CFGKEY_FILENAME = "FileURL";
	protected static final String CFGKEY_IF_FILE_EXIST = "ifFileExist";
	private URL m_url;
	private GenerateILPFactory lpxFactory;
	protected final static SettingsModelString m_format = new SettingsModelString(ILPWriterNodeModel.CFGKEY_FORMAT,
			"LPX (LiPS)");
	protected final static SettingsModelString m_variant = new SettingsModelString(ILPWriterNodeModel.CFGKEY_VARIANT,
			"Original Formulation");
	protected final static SettingsModelString m_file = new SettingsModelString(ILPWriterNodeModel.CFGKEY_FILENAME, "");
	protected final static SettingsModelString m_if_file_exist = new SettingsModelString(
			ILPWriterNodeModel.CFGKEY_IF_FILE_EXIST, "Overwrite");

	/**
	 * Constructor for the node model.
	 */
	protected ILPWriterNodeModel() {
		super(1, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		checkFileAccess(m_file.getStringValue(), false);
		m_url = FileUtil.toURL(m_file.getStringValue());
		Path localPath = FileUtil.resolveToPath(m_url);
		String formatValue = m_format.getStringValue();
		String vaiantValue = m_variant.getStringValue();
		lpxFactory = new GenerateILPFactory();
		lpxFactory.generateLPX(formatValue, vaiantValue, inData[IN_PORT], localPath, m_url, exec);
		return new BufferedDataTable[IN_PORT];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		String extension = getFileExtension(peekFlowVariableString("reader.url"));

		if (!Contains(ALL_FORMAT, extension)) {
			LOGGER.error("Can't generate a LPX Model for this file extension");
		}
		if (inSpecs[IN_PORT].getNumColumns() < 2) {
			LOGGER.error("Can't generate a LPX Model for this Precedence Matrix");
		}
		String path = m_file.getStringValue();
		String extPath = getFileExtension(path);
		String formFile = m_format.getStringValue();
		if (formFile.compareTo(CFGKEY_FORMAT) != 0) {
			if (!((extPath == "lpx" && formFile.compareTo("LPX (LiPS)") == 0)
					|| (extPath == "lp" && formFile.compareTo("LP (SCIP)") == 0))) {
				LOGGER.warn("An " + formFile + " file with extension ." + extPath + " will be generated.");
			}
		}

		checkFileAccess(path, true);
		for (int c = 0; c < inSpecs[IN_PORT].getNumColumns(); c++) {
			DataType colType = inSpecs[IN_PORT].getColumnSpec(c).getType();
			if (!colType.isCompatible(DoubleValue.class)) {
				throw new InvalidSettingsException("Class " + colType + " not supported.");
			}
		}
		return new DataTableSpec[IN_PORT];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		m_file.saveSettingsTo(settings);
		m_if_file_exist.saveSettingsTo(settings);
		m_format.saveSettingsTo(settings);
		m_variant.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		m_file.loadSettingsFrom(settings);
		try {
			m_url = stringToURL(m_file.getStringValue());
		} catch (MalformedURLException mue) {
			LOGGER.error(mue.getMessage());
		}
		m_format.loadSettingsFrom(settings);
		m_variant.loadSettingsFrom(settings);
		m_if_file_exist.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		m_format.validateSettings(settings);
		m_file.validateSettings(settings);
		m_if_file_exist.validateSettings(settings);
		m_variant.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	private static URL stringToURL(final String url) throws MalformedURLException {
		if ((url == null) || (url.equals(""))) {
			throw new MalformedURLException("URL not valid");
		}
		URL newURL;
		try {
			newURL = new URL(url);
		} catch (Exception e) {
			File tmp = new File(url);
			newURL = tmp.getAbsoluteFile().toURI().toURL();
		}
		return newURL;
	}

	private String getFileExtension(String path) {
		String extension = FilenameUtils.getExtension(path);
		return extension;
	}

	private void checkFileAccess(final String fileName, final boolean showWarnings) throws InvalidSettingsException {
		String election = m_if_file_exist.getStringValue();
		boolean isOKOverwrite = election.compareTo("Overwrite") == 0;
		String warning = CheckUtils.checkDestinationFile(fileName, isOKOverwrite);
		if ((warning != null) && showWarnings) {
			setWarningMessage(warning);
		}
	}

	private boolean Contains(String[] arr, String value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].compareTo(value) == 0) {
				return true;
			}
		}
		return false;
	}
}
