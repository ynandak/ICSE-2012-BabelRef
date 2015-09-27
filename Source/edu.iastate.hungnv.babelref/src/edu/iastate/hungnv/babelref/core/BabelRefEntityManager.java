package edu.iastate.hungnv.babelref.core;

import java.util.ArrayList;
import references.Character;
import references.Reference;
import references.ReferenceManager;

import babelref.FindEntitiesInProject;
import babelref.FindReferencesInFile;

import entities.Entity;
import entities.EntityManager;

/**
 * 
 * @author HUNG
 *
 */
public class BabelRefEntityManager {
	
	private static String projectFolder = "";				// The project to work on
	
	private static EntityManager entityManager = null;		// Information about entities and references in the project
	
	/**
	 * Detects the entities in a project.
	 */
	public static void detectEntitiesInProject(String _projectFolder) {
		projectFolder = _projectFolder;
		
		FindEntitiesInProject findEntitiesInProject = new FindEntitiesInProject(projectFolder, null, false); // TODO: Change to true later
		entityManager = findEntitiesInProject.execute();
	}
	
	/**
	 * Gets the entities in a given PHP file.
	 */
	public static ArrayList<Entity> getEntitiesInFile(String _projectFolder, String relativeFilePath) {
		if (!projectFolder.equals(_projectFolder))
			detectEntitiesInProject(_projectFolder);
		
		return entityManager.getEntitiesInFile(relativeFilePath);
	}

	/**
	 * Returns the reference at an approximate location (the location can be any character that make up the reference).
	 * detectEntitiesInProject() (and updateEntitiesInPage()) should be called to detect the entities and references first.
	 */
	public static Reference getReferenceFromApproxLocation(String _projectFolder, String relativeFilePath, int position) {
		if (!projectFolder.equals(_projectFolder))
			detectEntitiesInProject(_projectFolder);
		
		for (Reference reference : entityManager.getReferencesInFile(relativeFilePath)) {
			for (Character character : reference.getCharacters())
				if (character.getFilePath().equals(relativeFilePath) && character.getPosition() == position)
					return reference;
		}
		return null;
	}
	
	/**
	 * Updates the entities in a project when the content of a PHP page has changed.
	 */
	public static void updateEntitiesInPage(String _projectFolder, String relativeFilePath) {
		if (!projectFolder.equals(_projectFolder)) {
			detectEntitiesInProject(_projectFolder);
			return;
		}
		
		entityManager.removeReferencesInPage(relativeFilePath);
		entityManager.addReferencesInPage(relativeFilePath, detectReferencesInPage(projectFolder, relativeFilePath));
	}

	/**
	 * Detects the entities in a given PHP page.
	 * Note that the entities may be located in a different file than the PHP page.
	 */
	public static ArrayList<Entity> detectEntitiesInPage(String projectFolder, String relativeFilePath) {
		EntityManager entityManager = new EntityManager();
		entityManager.addReferencesInPage(relativeFilePath, detectReferencesInPage(projectFolder, relativeFilePath));
		entityManager.linkPhpRefsToHtmlEntities();
		return entityManager.getEntityListIncludingDanglingRefs();
	}
	
	/**
	 * Detects the references in a given PHP page.
	 * Note that the references may be located in a different file than the PHP page.
	 */
	public static ArrayList<Reference> detectReferencesInPage(String projectFolder, String relativeFilePath) {
		FindReferencesInFile findReferencesInFile = new FindReferencesInFile(projectFolder, relativeFilePath, null);
		ReferenceManager referenceManager = findReferencesInFile.execute();		
		return referenceManager.getReferenceList();
	}
	
}
