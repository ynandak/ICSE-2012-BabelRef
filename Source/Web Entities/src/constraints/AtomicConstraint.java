package constraints;

import java.util.ArrayList;
import java.util.HashMap;

import sourcetracing.SourceCodeLocation;

import datamodel.nodes.ext.SelectNode;

/**
 * 
 * @author HUNG
 *
 */
public class AtomicConstraint extends Constraint {
	
	private SelectNode selectNode;
	
	private AtomicConstraint identicalConstraint= null; // The constraint that share the same SelectNode with this constraint.

	/**
	 * Constructor
	 * @param selectNode
	 */
	public AtomicConstraint(SelectNode selectNode) {
		this.selectNode = selectNode;
	}
	
	/*
	 * Get properties
	 */
	
	public SelectNode getSelectNode() {
		return selectNode;
	}
	
	@Override
	public void collectAtomicConstraints(ArrayList<AtomicConstraint> atomicConstraints) {
		for (AtomicConstraint atomicConstraint : atomicConstraints) {
			if (hasSameConstraint(atomicConstraint)) {
				identicalConstraint = atomicConstraint; // @see constraints.AtomicConstraint.evaluate(HashMap<AtomicConstraint, Boolean>)
				return; // Avoid duplicate atomic constraints
			}
		}		
		identicalConstraint = this; // @see constraints.AtomicConstraint.evaluate(HashMap<AtomicConstraint, Boolean>)
		atomicConstraints.add(this); 
	}
	
	/**
	 * Returns true if these two atomic constraints are the same (sharing the same SelectNode).
	 * @param atomicConstraint
	 */
	private boolean hasSameConstraint(AtomicConstraint atomicConstraint) {
		String mySignature = String.valueOf(getSelectNode().hashCode());
		if (getSelectNode().getConditionString() != null) {
			SourceCodeLocation location = getSelectNode().getConditionString().getLocation().getLocationAtOffset(0);
			if (!location.isUndefined()) {
				mySignature = location.getFilePath() + location.getPosition();
			}
		}
		
		String theirSignature = String.valueOf(atomicConstraint.getSelectNode().hashCode());
		if (atomicConstraint.getSelectNode().getConditionString() != null) {
			SourceCodeLocation location = atomicConstraint.getSelectNode().getConditionString().getLocation().getLocationAtOffset(0);
			if (!location.isUndefined()) {
				theirSignature = location.getFilePath() + location.getPosition();
			}
		}
		
		return mySignature.equals(theirSignature);
	}
	
	@Override
	public boolean evaluate(HashMap<AtomicConstraint, Boolean> booleanTable) {
		// @see constraints.AtomicConstraint.collectAtomicConstraints(HashSet<AtomicConstraint>)
		return booleanTable.get(identicalConstraint);
	}
	
	/*
	 * (non-Javadoc)
	 * @see constraints.Constraint#printToString()
	 */
	@Override
	public String printToString() {
		if (selectNode.getConditionString() == null)
			return "[" + String.valueOf(selectNode.hashCode()) + "]";
		
		return selectNode.getConditionString().getStringValue(); // or: String.valueOf(selectNode.getConditionString().hashCode());
	}
	
}
