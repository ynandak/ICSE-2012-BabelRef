package edu.iastate.hungnv.babelref.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import edu.iastate.hungnv.babelref.core.Helper;
import edu.iastate.hungnv.babelref.rename.Constants;
import edu.iastate.hungnv.babelref.rename.RenameInfo;
import edu.iastate.hungnv.babelref.rename.RenameProcessor;
import edu.iastate.hungnv.babelref.ui.views.BabelRefEntityView;
import edu.iastate.hungnv.babelref.ui.wizards.RenameWizard;

/**
 * 
 * @author HUNG
 *
 */
public class RenameAction implements IEditorActionDelegate {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action ) {
		if (Helper.saveAllEditors()) {
			openWizard();
		}
	}
	
	/**
	 * Opens the Rename wizard.
	 */
	private void openWizard() {
		RenameInfo renameInfo = new RenameInfo(Helper.getProjectName(), Helper.getSelectedEntity());
		
		RefactoringProcessor processor = new RenameProcessor(renameInfo);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
				
		RenameWizard wizard = new RenameWizard(refactoring, renameInfo);
		RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
		try {
			BabelRefEntityView.inst_.refactoringStarted();
			operation.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Constants.FAILED_CHECK_DIALOG_MESSAGE);
			BabelRefEntityView.inst_.refactoringEnded();
		} catch (InterruptedException e) {
			// Canceled by the user
		}
	}

}
