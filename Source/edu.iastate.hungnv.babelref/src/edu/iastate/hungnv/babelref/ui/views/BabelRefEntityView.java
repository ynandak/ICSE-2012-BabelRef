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
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import references.Reference;

import util.FileIO;

import edu.iastate.hungnv.babelref.core.BabelRefEntityManager;
import edu.iastate.hungnv.babelref.core.Helper;
import entities.Entity;
import entities.UndeclaredEntity;

/**
 * 
 * @author HUNG
 *
 */
public class BabelRefEntityView extends ViewPart {
	
	/*
	 * EntityView controls
	 */
	private Label filePathLabel;
	
	private Button refreshButton;
	
	private Label entityListLabel;
	
	private Label referenceListLabel;
	
	private TableViewer entityTableViewer;
	
	private TableViewer referenceTableViewer;
	
	/*
	 * These fields are used to update results after refactoring operations.
	 */
	public static BabelRefEntityView inst_ = null;	// The current instance of BabelRefEntityView
	
	private boolean refactoringInProgress = false; 	// Indicates whether a refactoring is being performed
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		BabelRefEntityView entityView = new BabelRefEntityView();
		entityView.createPartControl(shell);
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		inst_ = this; // Set the current instance of BabelRefEntityView
		
		parent.setLayout(new GridLayout(2, true));
		
		filePathLabel = new Label(parent, SWT.NONE);
		filePathLabel.setText("");
		filePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		refreshButton = new Button(parent, SWT.PUSH);
	    refreshButton.setText("Refresh");
	    refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		
		entityListLabel = new Label(parent, SWT.NONE);
		entityListLabel.setText("Entity List:");
		entityListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		referenceListLabel = new Label(parent, SWT.NONE);
		referenceListLabel.setText("Reference List:");
		referenceListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		entityTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		entityTableViewer.setContentProvider(new ArrayContentProvider());
		entityTableViewer.setInput(new ArrayList<Entity>());
		entityTableViewer.getTable().setHeaderVisible(true);
		entityTableViewer.getTable().setLinesVisible(true);

		TableViewerColumn column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Entity Name");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Entity) element).getName();
			}
		});

		column = new TableViewerColumn(entityTableViewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Entity Type");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((Entity) element).getType().toString();
			}
		});
		
		referenceTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		referenceTableViewer.setContentProvider(new ArrayContentProvider());
		referenceTableViewer.setInput(new ArrayList<Reference>());
		referenceTableViewer.getTable().setHeaderVisible(true);
		referenceTableViewer.getTable().setLinesVisible(true);
		
		column = new TableViewerColumn(referenceTableViewer, SWT.NONE);
		column.getColumn().setWidth(150);
		column.getColumn().setText("Name");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Reference) element).getName();
			}
		});
		
		column = new TableViewerColumn(referenceTableViewer, SWT.NONE);
		column.getColumn().setWidth(150);
		column.getColumn().setText("Type");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Reference) element).getType();
			}
		});
		
		column = new TableViewerColumn(referenceTableViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return ((Reference) element).getFilePath();
			}
		});
		
		column = new TableViewerColumn(referenceTableViewer, SWT.NONE);
		column.getColumn().setWidth(75);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				Reference reference = (Reference) element;
				String file = Helper.getProjectPath() + "/" + reference.getFilePath();
				int offset = reference.getPosition();
				return (offset >= 0 ? String.valueOf(FileIO.getLineFromOffsetInFile(file, offset)) : "");
			}
		});
		
		column = new TableViewerColumn(referenceTableViewer, SWT.NONE);
		column.getColumn().setWidth(75);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Reference reference = (Reference) element;
				int offset = reference.getPosition();
				return (offset >= 0 ? String.valueOf(offset) : "");
			}
		});

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
	    entityTableViewer.getControl().setLayoutData(gridData);
	    referenceTableViewer.getControl().setLayoutData(gridData);
	    
	    clearEntityView();
	    
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
					entitySelected((Entity) selectedObject);
			}
	    });

	    referenceTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedObject != null)
					referenceSelected((Reference) selectedObject);			
			}
	    });
	    
	    registerEventHandlers2();
	}
	
	private void registerEventHandlers2() {
		final IPropertyListener iPropertyListener = new IPropertyListener() {
	    	
	    	private boolean wasDirty = false;
			
			@Override
			public void propertyChanged(Object source, int propId) {
				if (Helper.getActiveEditor().isDirty())
					wasDirty = true;
				else {
					if (wasDirty && !refactoringInProgress) {
						editorSaved();
					}
					wasDirty = false;
				}
			}
		};
		
		IPartListener2 iPartListener = new IPartListener2() {
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(true);
				if (part instanceof ITextEditor) {
					part.addPropertyListener(iPropertyListener);
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}
		};
		
		getViewSite().getPage().addPartListener(iPartListener);
	}
	
	/**
	 * Displays the Entity View
	 */
	private void displayEntityView(String projectPath, String projectName, String relativeFilePath) {
		ArrayList<Entity> entities = BabelRefEntityManager.getEntitiesInFile(projectPath, relativeFilePath);
		// Remove dangling references from the list
		for (Entity entity : new ArrayList<Entity>(entities)) {
			if (entity instanceof UndeclaredEntity) {
				entities.remove(entity);
			}
		}
		Collections.sort(entities, new Entity.EntityComparator(new Entity.EntityComparatorByName(), new Entity.EntityComparatorByType(), new Entity.EntityComparatorByFile()));
		
		filePathLabel.setText(projectName + "\\" + relativeFilePath);
		entityListLabel.setText("Entity List: (" + entities.size() + " entities found)");
		referenceListLabel.setText("Reference List:");
		entityTableViewer.setInput(entities);
		referenceTableViewer.setInput(new ArrayList<Reference>());
	}
		
	/**
	 * Clears the Entity View
	 */
	private void clearEntityView() {
		filePathLabel.setText("");
		entityListLabel.setText("Entity List:");
		referenceListLabel.setText("Reference List:");
		entityTableViewer.setInput(new ArrayList<Entity>());
		referenceTableViewer.setInput(new ArrayList<Reference>());
	}
	
	/**
	 * Invoked when the refresh button is clicked.
	 */
	private void refreshButtonClicked() {
		Helper.saveAllEditors();
		if (Helper.getActiveEditor() != null)
			displayEntityView(Helper.getProjectPath(), Helper.getProjectName(), Helper.getRelativeFilePath());
	}
	
	/**
	 * Invoked when an entity is selected
	 */
	private void entitySelected(Entity entity) {
		ArrayList<Reference> references = entity.getReferences();
		Collections.sort(references, new Reference.ReferenceComparator(new Reference.ReferenceComparatorByName(), new Reference.ReferenceComparatorByFile(), new Reference.ReferenceComparatorByPosition()));
		
		referenceListLabel.setText("Reference List: (" + references.size() + " references found)");
		referenceTableViewer.setInput(references);

		for (Reference reference : entity.getReferences()) {
			if (filePathLabel.getText().endsWith(reference.getFilePath())) {
				referenceSelected(reference);
				break;
			}
		}
		entityTableViewer.getControl().setFocus();
	}

	/**
	 * Invoked when a reference is selected
	 */
	private void referenceSelected(Reference reference) {
		Helper.selectAndReveal(reference.getFilePath(), reference.getPosition(), reference.getName().length());
		referenceTableViewer.getControl().setFocus();
	}
	
	/**
	 * Invoked when the editor is saved
	 */
	private void editorSaved() {
		// TODO: [AdhocCode] For SquirrelMail, re-detect on the current page only. For others, re-detect on the whole project. 
		if (Helper.getProjectPath().contains("SquirrelMail"))
			BabelRefEntityManager.updateEntitiesInPage(Helper.getProjectPath(), Helper.getRelativeFilePath());
		else
			BabelRefEntityManager.detectEntitiesInProject(Helper.getProjectPath());				
		displayEntityView(Helper.getProjectPath(), Helper.getProjectName(), Helper.getRelativeFilePath());
	}
	
	/**
	 * Invoked when a refactoring operation started
	 */
	public void refactoringStarted() {
		refactoringInProgress = true;
	}
	
	/**
	 * Invoked when a refactoring operation ended
	 */
	public void refactoringEnded() {
		refactoringInProgress = false;
		editorSaved();
	}

}