package org.knime.rankaggregation.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.util.FileUtil;

/**
 * @author Randy Reyna Hernández
 */

public abstract class AbstractGenerateILPFile {
	public AbstractGenerateILPFile() {
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
