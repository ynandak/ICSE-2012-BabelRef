package edu.iastate.hungnv.babelref.rename;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import references.Character;
import references.Reference;

/**
 * 
 * @author HUNG
 *
 */
public class RenameProcessor extends RefactoringProcessor {

	private RenameInfo renameInfo;

	public RenameProcessor(RenameInfo renameInfo) {
		this.renameInfo = renameInfo;
	}

	@Override
	public Object[] getElements() {
		return null;
	}
	
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	@Override
	public String getProcessorName() {
		return getIdentifier();
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return true;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) 
			throws CoreException {
		return null;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) 
			throws CoreException, OperationCanceledException {
		if (renameInfo.getEntity() == null)
			return RefactoringStatus.createFatalErrorStatus(Constants.FAILED_CHECK_DIALOG_MESSAGE);
		else
			return null;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws CoreException, OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) 
			throws CoreException, OperationCanceledException {

		HashMap<String, TextFileChange> mapFileToChanges = new HashMap<String, TextFileChange>();
		
		for (Reference reference : renameInfo.getEntity().getReferences()) {
			for (Character character : reference.getCharacters()) {
				String filePath = character.getFilePath();
				int position = character.getPosition();
				
				if (!mapFileToChanges.containsKey(filePath)) {
					IFile file = getIFileFromRelativePath(renameInfo.getProjectName(), filePath);
					TextFileChange change = new TextFileChange(file.getName(), file);
					change.setEdit(new MultiTextEdit());
					mapFileToChanges.put(filePath, change);
				}
				MultiTextEdit multiEdit = (MultiTextEdit) mapFileToChanges.get(filePath).getEdit();
				TextEdit edit;
				if (character.getPosition() == reference.getPosition()) // If it's the first character, then replace with the new name
					edit = new ReplaceEdit(position, 1, renameInfo.getNewName());
				else // Otherwise delete it
					edit = new DeleteEdit(position, 1);
				multiEdit.addChild(edit);
			}
		}	
			
		CompositeChange compositeChange = new CompositeChange("CompositeChange");
		compositeChange.addAll(mapFileToChanges.values().toArray(new Change[]{}));
		return compositeChange;
	}
	
	/*
	 * Helping methods
	 */
	
	private static IFile getIFileFromRelativePath(String projectName, String relativeFilePath) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		return project.getFile(relativeFilePath);
	}

}
