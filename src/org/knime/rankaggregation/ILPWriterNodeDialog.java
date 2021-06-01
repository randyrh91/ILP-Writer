package org.knime.rankaggregation;

import java.awt.Component;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.util.FileUtil;

/**
 * @author Randy Reyna Hernández
 * 
 *         <code>NodeDialog</code> for the "LPXWriter" Node.
 * 
 *         This node dialog derives from {@link DefaultNodeSettingsPane} which
 *         allows creation of a simple dialog with standard components. If you
 *         need a more complex dialog please derive directly from
 *         {@link org.knime.core.node.NodeDialogPane}.
 * 
 * 
 */
public class ILPWriterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring LPXWriter node dialog. This is just a suggestion
	 * to demonstrate possible default dialog components.
	 */
	protected ILPWriterNodeDialog() {
		SettingsModelString urlFileModelString = ILPWriterNodeModel.m_file;
		SettingsModelString ifFileExistModelString = ILPWriterNodeModel.m_if_file_exist;
		SettingsModelString formatModelString = ILPWriterNodeModel.m_format;
		String ext = ".lpx";
		DialogComponentFileChooser dialog = new DialogComponentFileChooser(urlFileModelString, "writer_historyId", ext);

		createNewGroup("Format:");
		addDialogComponent(
				new DialogComponentStringSelection(formatModelString, "Your choice", "LPX (LiPS)", "LP (SCIP)"));

		createNewGroup("Variant:");
		addDialogComponent(new DialogComponentStringSelection(
				new SettingsModelString(ILPWriterNodeModel.CFGKEY_VARIANT, "Original Formulation"), "Your choice",
				"Original Formulation", "Reduced Variables Formulation",
				"Reduced Variables and Constraints Formulation"));

		createNewGroup("Output location:");
		addDialogComponent(dialog);

		createNewGroup("Writer options:");
		addDialogComponent(new DialogComponentButtonGroup(
				new SettingsModelString(ILPWriterNodeModel.CFGKEY_IF_FILE_EXIST, "Overwrite"), false,
				"If file exists...", "Overwrite", "Abort"));

		formatModelString.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent arg0) {
				String format = formatModelString.getStringValue().trim();
				switch (format) {
				case "LPX (LiPS)":
					Component[] panelLPX = dialog.getComponentPanel().getComponents();
					FilesHistoryPanel hpPX = (FilesHistoryPanel) panelLPX[0];
					hpPX.setSuffixes(".lpx");
					break;
				case "LP (SCIP)":
					Component[] panelLP = dialog.getComponentPanel().getComponents();
					FilesHistoryPanel hpLP = (FilesHistoryPanel) panelLP[0];
					hpLP.setSuffixes(".lp");
					break;
				default:
					break;
				}
			}
		});

		urlFileModelString.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				String selFile = urlFileModelString.getStringValue().trim();
				if (!selFile.isEmpty()) {
					try {
						URL newUrl = FileUtil.toURL(selFile);
						Path path = FileUtil.resolveToPath(newUrl);
						boolean isLocalDestination = path != null;
						ifFileExistModelString.setEnabled(isLocalDestination);
					} catch (IOException | URISyntaxException | InvalidPathException ex) {
						// ignore
					}
				}
			}
		});
	}
}
