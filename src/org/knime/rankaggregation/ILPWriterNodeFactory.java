package org.knime.rankaggregation;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * 
 * @author Randy Reyna Hernández
 * 
 *         <code>NodeFactory</code> for the "ILPWriter" Node.
 */
public class ILPWriterNodeFactory extends NodeFactory<ILPWriterNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ILPWriterNodeModel createNodeModel() {
		return new ILPWriterNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<ILPWriterNodeModel> createNodeView(final int viewIndex, final ILPWriterNodeModel nodeModel) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new ILPWriterNodeDialog();
	}

}
