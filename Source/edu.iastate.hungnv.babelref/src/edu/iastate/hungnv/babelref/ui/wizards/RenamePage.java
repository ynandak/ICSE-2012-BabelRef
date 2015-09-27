package edu.iastate.hungnv.babelref.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import edu.iastate.hungnv.babelref.rename.RenameInfo;

/**
 * 
 * @author HUNG
 *
 */
public class RenamePage extends UserInputWizardPage {

	private RenameInfo renameInfo;
	private Text newNameText;

	public RenamePage(RenameInfo renameInfo) {
		super("RenamePage");
		this.renameInfo = renameInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setFont(parent.getFont());
	
		Label label= new Label(composite, SWT.NONE);
		label.setText("New name:");
		label.setLayoutData(new GridData());
	
		newNameText= new Text(composite, SWT.BORDER);
		newNameText.setText(renameInfo.getOldName());
		newNameText.setFont(composite.getFont());
		newNameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		newNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				newNameTextModified();
			}
		});
	
		newNameText.selectAll();
		setPageComplete(false);
		setControl(composite);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.UserInputWizardPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible)
			newNameText.setFocus();
		super.setVisible(visible);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.UserInputWizardPage#performFinish()
	 */
	protected boolean performFinish() {
		initializeRefactoring();
		return super.performFinish();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.UserInputWizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		initializeRefactoring();
		return super.getNextPage();
	}
	
	/**
	 * Invoked when the newNameText is modified
	 */
	private void newNameTextModified() {
		String newName = newNameText.getText();
	    setPageComplete(!newName.isEmpty() && !newName.equals(renameInfo.getOldName()));
	}
	
	/**
	 * Initializes refactoring
	 */
	private void initializeRefactoring() {
		renameInfo.setNewName(newNameText.getText());
	}
	
}
