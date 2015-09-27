package edu.iastate.hungnv.babelref.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import references.Reference;

import entities.Entity;

/**
 * 
 * @author HUNG
 *
 */
public class Helper {
	
	/*
	 * Get the active editor and related properties
	 */
	
	public static ITextEditor getActiveEditor() {
		return (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}
	
	public static StyledText getActiveEditorStyledText() {
		return (StyledText) getActiveEditor().getAdapter(Control.class);
	}
	
	public static IFile getActiveEditorFile() {
		return ((IFileEditorInput) getActiveEditor().getEditorInput()).getFile();
	}
	
	public static String getProjectName() {
		return getActiveEditorFile().getProject().getName();
	}
	
	public static String getProjectPath() {
		return getActiveEditorFile().getProject().getLocation().toOSString().replace("/", "\\");
	}
	
	public static String getRelativeFilePath() {
		return getActiveEditorFile().getProjectRelativePath().toOSString().replace("/", "\\");
	}
	
	/**
	 * Opens an editor for a file.
	 */
	public static ITextEditor openEditor(String relativeFilePath) {
		ITextEditor editor = null;
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
			IFile file = project.getFile(relativeFilePath);
			
			IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorInput editorInput = new FileEditorInput(file);
			String editorId = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName()).getId();	
			
			editor = (ITextEditor) workbenchPage.openEditor(editorInput, editorId);
		} catch (Exception e) {
		}
		return editor;
	}
	
	/**
	 * Opens an editor and selects a string fragment in the file.
	 */
	public static void selectAndReveal(String relativeFilePath, int offset, int length ) {
		ITextEditor editor = Helper.openEditor(relativeFilePath);
		if (editor != null)
			editor.selectAndReveal(offset, length);
	}
	
	/**
	 * Saves all editors.
	 */
	public static boolean saveAllEditors() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return IDE.saveAllEditors(new IResource[] {workspaceRoot}, true);
	}
	
	/**
	 * Returns the entity that is being selected
	 */
	public static Entity getSelectedEntity() {
		Reference reference = BabelRefEntityManager.getReferenceFromApproxLocation(getProjectPath(), getRelativeFilePath(), getActiveEditorStyledText().getCaretOffset());
		return (reference != null ? reference.getEntity() : null);
	}
	
}
