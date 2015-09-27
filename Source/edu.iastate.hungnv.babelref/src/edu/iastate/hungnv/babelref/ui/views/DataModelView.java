package edu.iastate.hungnv.babelref.ui.views;

import java.util.ArrayList;
import main.CreateDataModelForFile;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.SourceCodeLocation;
import sourcetracing.UndefinedLocation;
import util.FileIO;

import config.DataModelConfig;
import datamodel.DataModel;
import datamodel.nodes.ext.ConcatNode;
import datamodel.nodes.ext.DataNode;
import datamodel.nodes.ext.DataNodeVisitor;
import datamodel.nodes.ext.LiteralNode;
import datamodel.nodes.ext.RepeatNode;
import datamodel.nodes.ext.SelectNode;
import datamodel.nodes.ext.SymbolicNode;
import edu.iastate.hungnv.babelref.core.Helper;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelView extends ViewPart {

	/*
	 * DataModelView controls
	 */
	private Label filePathLabel;
	
	private Button refreshButton;
	
	private TreeViewer dataModelTreeViewer;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		DataModelView dataModelView = new DataModelView();
		dataModelView.createPartControl(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		dataModelTreeViewer.getControl().setFocus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		filePathLabel = new Label(parent, SWT.NONE);
		filePathLabel.setText("");
		filePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		refreshButton = new Button(parent, SWT.PUSH);
	    refreshButton.setText("Refresh");
	    refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		
		dataModelTreeViewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		dataModelTreeViewer.setContentProvider(new DataModelContentProvider());
		dataModelTreeViewer.setInput(null);
		dataModelTreeViewer.getTree().setHeaderVisible(true);
		dataModelTreeViewer.getTree().setLinesVisible(true);
		dataModelTreeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TreeViewerColumn column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Data Model Tree");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return getTypeOfDataNode((DataNode) element);
			}
			
			public Image getImage(Object element) {
				return getIconForDataNode((DataNode) element);
			}
		});

		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Text");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTextOfDataNode((DataNode) element);
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getFileOfDataNode((DataNode) element);
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int line = getLineInFileOfDataNode((DataNode) element);
				return (line >= 0 ? String.valueOf(line) : "");
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int offset = getOffsetInFileOfDataNode((DataNode) element);
				return (offset >= 0 ? String.valueOf(offset) : "");
			}
		});
		
	    clearDataModelView();
	    
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
	    refreshButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		refreshButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				refreshButtonClicked();
			}
    	});
	    
	    dataModelTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedObject != null)
					dataNodeSelected((DataNode) selectedObject);
			}
	    });
	}
	
	/**
	 * Displays the Data Model View
	 */
	private void displayDataModelView(String projectPath, String projectName, String relativeFilePath) {
		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(projectPath, relativeFilePath, "", DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT);
		DataModel dataModel = createDataModelForFile.execute();
		
		filePathLabel.setText(projectName + "\\" + relativeFilePath);
		dataModelTreeViewer.setInput(dataModel);
		dataModelTreeViewer.expandToLevel(2);
	}
		
	/**
	 * Clears the Data Model View
	 */
	private void clearDataModelView() {
		filePathLabel.setText("");
		dataModelTreeViewer.setInput(null);
	}
	
	/**
	 * Invoked when the refresh button is clicked.
	 */
	private void refreshButtonClicked() {
		Helper.saveAllEditors();
		if (Helper.getActiveEditor() != null)
			displayDataModelView(Helper.getProjectPath(), Helper.getProjectName(), Helper.getRelativeFilePath());
	}
	
	/**
	 * Invoked when a DataNode is selected
	 */
	private void dataNodeSelected(DataNode dataNode) {
		Helper.selectAndReveal(getFileOfDataNode(dataNode), getOffsetInFileOfDataNode(dataNode), getTextOfDataNode(dataNode).length());
		dataModelTreeViewer.getControl().setFocus();
	}
	
	/**
	 * SelectChildNode represents a branch of a SelectNode.
	 */
	private class SelectChildNode extends DataNode {
		
		private DataNode dataNode;
		private boolean isTrueBranch;
		
		public SelectChildNode(DataNode dataNode, boolean isTrueBranch) {
			this.dataNode = dataNode;
			this.isTrueBranch = isTrueBranch;
		}
		
		public DataNode getDataNode() {
			return dataNode;
		}
		
		public boolean isTrueBranch() {
			return isTrueBranch;
		}

		@Override
		protected void acceptVisitor(DataNodeVisitor visitor) {
		}

		@Override
		public Element printGraphToXmlFormat(Document document) {
			return null;
		}
		
	}

	/**
	 * Content provider for the Data Model tree.
	 */
	private class DataModelContentProvider implements ITreeContentProvider {
		
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			DataModel dataModel = (DataModel) inputElement;
			DataNode outputDataNode = DataNode.createInstance(dataModel.getOutputDataNode());
			if (outputDataNode == null)
				outputDataNode = new SymbolicNode((Element) null);
			return new Object[]{outputDataNode};
		}

		@Override
		public Object[] getChildren(Object parentNode) {
			ArrayList<DataNode> dataNodeList = new ArrayList<DataNode>();
			
			if (parentNode instanceof ConcatNode) {
				dataNodeList.addAll(((ConcatNode) parentNode).getChildNodes());
			}
			
			else if (parentNode instanceof SelectNode) {
				SelectNode selectNode = (SelectNode) parentNode;
				if (selectNode.getNodeInTrueBranch() != null)
					dataNodeList.add(new SelectChildNode(selectNode.getNodeInTrueBranch(), true));
				if (selectNode.getNodeInFalseBranch() != null)
					dataNodeList.add(new SelectChildNode(selectNode.getNodeInFalseBranch(), false));
			}
			
			else if (parentNode instanceof SelectChildNode) {
				dataNodeList.add(((SelectChildNode) parentNode).getDataNode());
			}
			
			else if (parentNode instanceof RepeatNode) {
				dataNodeList.add(((RepeatNode) parentNode).getDataNode());
			}
			
			else if (parentNode instanceof SymbolicNode) {
				if (((SymbolicNode) parentNode).getParentNode() != null)
					dataNodeList.add(((SymbolicNode) parentNode).getParentNode());
			}
			
			else {
				// LiteralNode has no children.
			}
			
			return dataNodeList.toArray(new DataNode[]{});
		}

		@Override
		public boolean hasChildren(Object element) {
			return (getChildren(element).length > 0);
		}
		
	}
	
	/*
	 * Label provider for Data Nodes
	 */
	
	private String getTypeOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectChildNode)
			return (((SelectChildNode) dataNode).isTrueBranch() ? "True" : "False");
		else
			return dataNode.getClass().getSimpleName().replace("Node", "");
	}
	
	private Image getIconForDataNode(DataNode dataNode) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (dataNode instanceof ConcatNode)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (dataNode instanceof SelectNode)
			imageID = ISharedImages.IMG_TOOL_CUT;
		
		else if (dataNode instanceof SelectChildNode)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else if (dataNode instanceof RepeatNode)
			imageID = ISharedImages.IMG_TOOL_REDO;
		
		else if (dataNode instanceof SymbolicNode)
			imageID = ISharedImages.IMG_OBJS_WARN_TSK;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}
	
	private String getTextOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectNode)
			return (((SelectNode) dataNode).getConditionString() != null ? ((SelectNode) dataNode).getConditionString().getStringValue() : "");
		
		else if (dataNode instanceof SymbolicNode)
			return (((SymbolicNode) dataNode).getPhpNode() != null ? ((SymbolicNode) dataNode).getPhpNode().getStringValue() : "");
		
		else if (dataNode instanceof LiteralNode)
			return ((LiteralNode) dataNode).getStringValue();
		
		else
			return "";
	}
	
	private SourceCodeLocation getLocationOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectNode)
			return (((SelectNode) dataNode).getConditionString() != null ? ((SelectNode) dataNode).getConditionString().getLocation().getLocationAtOffset(0) : UndefinedLocation.inst);
		
		else if (dataNode instanceof SymbolicNode)
			return (((SymbolicNode) dataNode).getPhpNode() != null ? ((SymbolicNode) dataNode).getPhpNode().getLocation().getLocationAtOffset(0) : UndefinedLocation.inst);
		
		else if (dataNode instanceof LiteralNode)
			return ((LiteralNode) dataNode).getLocation().getLocationAtOffset(0);
		
		else
			return UndefinedLocation.inst;
	}
	
	private String getFileOfDataNode(DataNode dataNode) {
		return getLocationOfDataNode(dataNode).getFilePath();
	}
	
	private int getOffsetInFileOfDataNode(DataNode dataNode) {
		return getLocationOfDataNode(dataNode).getPosition();
	}
	
	private int getLineInFileOfDataNode(DataNode dataNode) {
		SourceCodeLocation location = getLocationOfDataNode(dataNode);
		if (location.isUndefined())
			return -1;
		else
			return FileIO.getLineFromOffsetInFile(Helper.getProjectPath() + "\\" + location.getFilePath(), location.getPosition());
	}
	
}
