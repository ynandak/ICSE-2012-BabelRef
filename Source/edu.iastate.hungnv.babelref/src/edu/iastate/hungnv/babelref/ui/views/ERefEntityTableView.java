package edu.iastate.hungnv.babelref.ui.views;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import references.Reference;
import util.FileIO;
import edu.iastate.hungnv.babelref.core.BabelRefEntityManager;
import edu.iastate.hungnv.babelref.core.Helper;
import entities.Entity;

/**
 * 
 * @author HUNG
 *
 */
public class ERefEntityTableView extends ViewPart {

	/*
	 * EntityTableView controls
	 */
	private Label filePathLabel;
	
	private Button refreshButton;
	
	private TableViewer entityTableViewer;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		ERefEntityTableView entityTableView = new ERefEntityTableView();
		entityTableView.createPartControl(shell);
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
		entityTableViewer.getControl().setFocus();
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
		
	    entityTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		entityTableViewer.setContentProvider(new ArrayContentProvider());
		entityTableViewer.setInput(new ArrayList<Entity>());
		entityTableViewer.getTable().setHeaderVisible(true);
		entityTableViewer.getTable().setLinesVisible(true);
		entityTableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TableViewerColumn column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Reference Name");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Reference) element).getName();
			}
		});

		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Reference Type");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((Reference) element).getType().toString();
			}
		});
		
		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Reference) element).getFilePath();
			}
		});
		
		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				Reference reference = (Reference) element;
				String file = Helper.getProjectPath() + "/" + reference.getFilePath();
				int offset = reference.getPosition();
				return (offset >= 0 ? String.valueOf(FileIO.getLineFromOffsetInFile(file, offset)) : "");
			}
		});
		
		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Reference reference = (Reference) element;
				int offset = reference.getPosition();
				return (offset >= 0 ? String.valueOf(offset) : "");
			}
		});
		
		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(400);
		column.getColumn().setText("Path Constraints");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Reference reference = (Reference) element;
				return reference.getConstraint().printToString();
			}
		});

	    clearEntityTableView();
	    
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
	    
	    entityTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedObject != null)
					referenceSelected((Reference) selectedObject);
			}
	    });
	}
	
	/**
	 * Displays the EntityTableView
	 */
	private void displayEntityTableView(String projectPath, String projectName, String relativeFilePath) {
		ArrayList<Reference> references = BabelRefEntityManager.detectReferencesInPage(projectPath, relativeFilePath);
		Collections.sort(references, new Reference.ReferenceComparator(new Reference.ReferenceComparatorByName(), new Reference.ReferenceComparatorByFile(), new Reference.ReferenceComparatorByPosition()));
		
		filePathLabel.setText(projectName + "\\" + relativeFilePath);
		entityTableViewer.setInput(references);
	}
		
	/**
	 * Clears the EntityTableView
	 */
	private void clearEntityTableView() {
		filePathLabel.setText("");
		entityTableViewer.setInput(new ArrayList<Reference>());
	}
	
	/**
	 * Invoked when the refresh button is clicked.
	 */
	private void refreshButtonClicked() {
		Helper.saveAllEditors();
		if (Helper.getActiveEditor() != null)
			displayEntityTableView(Helper.getProjectPath(), Helper.getProjectName(), Helper.getRelativeFilePath());
	}
	
	/**
	 * Invoked when a reference is selected
	 */
	private void referenceSelected(Reference reference) {
		Helper.selectAndReveal(reference.getFilePath(), reference.getPosition(), reference.getName().length());
		entityTableViewer.getControl().setFocus();
	}

}
