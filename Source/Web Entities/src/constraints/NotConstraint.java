package constraints;

import java.util.ArrayList;
import java.util.HashMap;

public class NotConstraint extends Constraint {
	
	private Constraint oppositeConstraint;
	
	/**
	 * Constructor.
	 * @param oppositeConstraint
	 */
	public NotConstraint(Constraint oppositeConstraint) {
		this.oppositeConstraint = checkAndUpdateLength(oppositeConstraint) ? oppositeConstraint : TrueConstraint.inst;
	}
	
	/*
	 * Get properties
	 */
	
	public Constraint getOppositeConstraint() {
		return oppositeConstraint;
	}
	
	@Override
	public void collectAtomicConstraints(ArrayList<AtomicConstraint> atomicConstraints) {
		oppositeConstraint.collectAtomicConstraints(atomicConstraints);
	}
	
	@Override
	public boolean evaluate(HashMap<AtomicConstraint, Boolean> booleanTable) {
		return !oppositeConstraint.evaluate(booleanTable);
	}

	/*
	 * (non-Javadoc)
	 * @see constraints.Constraint#printToString()
	 */
	@Override
	public String printToString() {
		return "NOT " + oppositeConstraint.printToString();
	}

}
