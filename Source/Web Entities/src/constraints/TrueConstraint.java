package constraints;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author HUNG
 *
 */
public class TrueConstraint extends Constraint {
	
	public static TrueConstraint inst = new TrueConstraint();
	
	/**
	 * Private constructor.
	 */
	private TrueConstraint() {
	}
	
	@Override
	public void collectAtomicConstraints(ArrayList<AtomicConstraint> atomicConstraints) {
		// Do nothing
	}
	
	@Override
	public boolean evaluate(HashMap<AtomicConstraint, Boolean> booleanTable) {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see constraints.Constraint#printToString()
	 */
	@Override
	public String printToString() {
		return "TRUE";
	}

}
