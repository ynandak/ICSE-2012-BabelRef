package edu.iastate.hungnv.babelref.ui.wizards;

import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import edu.iastate.hungnv.babelref.rename.Constants;
import edu.iastate.hungnv.babelref.rename.RenameInfo;

/**
 * 
 * @author HUNG
 *
 */
public class RenameWizard extends RefactoringWizard {

	private RenameInfo renameInfo;

	public RenameWizard(ProcessorBasedRefactoring refactoring, RenameInfo renameInfo) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE);
		this.renameInfo = renameInfo;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
	 */
	protected void addUserInputPages() {
		setDefaultPageTitle(Constants.WIZARD_DIALOG_TITLE);
		addPage(new RenamePage(renameInfo));
	}
	
}
