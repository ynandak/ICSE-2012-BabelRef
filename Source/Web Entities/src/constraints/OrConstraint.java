package constraints;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author HUNG
 *
 */
public class OrConstraint extends Constraint {

	private Constraint firstConstraint;
	private Constraint secondConstraint;
	
	/**
	 * Constructor
	 * @param firstConstraint
	 * @param secondConstraint
	 */
	public OrConstraint(Constraint firstConstraint, Constraint secondConstraint) {
		this.firstConstraint = checkAndUpdateLength(firstConstraint) ? firstConstraint : TrueConstraint.inst;
		this.secondConstraint = checkAndUpdateLength(secondConstraint) ? secondConstraint : TrueConstraint.inst;
	}
	
	/*
	 * Get properties
	 */
	
	public Constraint getFirstConstraint() {
		return firstConstraint;
	}
	
	public Constraint getSecondConstraint() {
		return secondConstraint;
	}
	
	@Override
	public void collectAtomicConstraints(ArrayList<AtomicConstraint> atomicConstraints) {
		firstConstraint.collectAtomicConstraints(atomicConstraints);
		secondConstraint.collectAtomicConstraints(atomicConstraints);
	}

	@Override
	public boolean evaluate(HashMap<AtomicConstraint, Boolean> booleanTable) {
		return firstConstraint.evaluate(booleanTable) || secondConstraint.evaluate(booleanTable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see constraints.Constraint#printToString()
	 */
	@Override
	public String printToString() {
		return firstConstraint.printToString() + "     OR     " + secondConstraint.printToString();
	}
	
}
