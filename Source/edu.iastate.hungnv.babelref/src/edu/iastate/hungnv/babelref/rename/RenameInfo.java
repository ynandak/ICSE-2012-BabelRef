package edu.iastate.hungnv.babelref.rename;

import entities.Entity;

/**
 * 
 * @author HUNG
 *
 */
public class RenameInfo {
	
	private String projectName;
	private Entity entity;
	private String newName;
	
	public RenameInfo(String projectName, Entity entity) {
		this.projectName = projectName;
		this.entity = entity;
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public String getOldName() {
		return entity.getName();
	}

	public String getNewName() {
		return newName;
	}

}
