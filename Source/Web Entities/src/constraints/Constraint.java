package constraints;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import config.WebEntitiesConfig;
import logging.MyLevel;
import logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Constraint {
	
	protected int length; // Length of this constraint
	
	/**
	 * Protected constructor.
	 */
	protected Constraint() {
		this.length = 1;
	}
	
	/**
	 * Returns true if the constraint can be added as a child node to the current constraint
	 * (the length of the constraint will not exceed a certain limit).
	 * In that case, also update the length of the current constraint.
	 */
	protected boolean checkAndUpdateLength(Constraint constraint) {
		if (this.length + constraint.length <= WebEntitiesConfig.CONSTRAINT_MAX_LENGTH) {
			this.length += constraint.length;
			return true;
		}
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In Constraint.java: Constraint has reached its maximum length of " + WebEntitiesConfig.CONSTRAINT_MAX_LENGTH);
			return false;
		}
	}

	/**
	 * Returns true if this constraint satisfies another constraint 
	 * (e.g. A & B satisfies A, but A & B does not satisfy B & C).
	 */
	public boolean satisfies(Constraint constraint) {
		return !(new AndConstraint(this, new NotConstraint(constraint)).exists());
	}
	
	/**
	 * Returns true if there is a configuration of AtomicConstraints that makes this constraint evaluates to TRUE.
	 */
	public boolean exists() {
		// Collect atomic constraints
		ArrayList<AtomicConstraint> atomicConstraintsList = new ArrayList<AtomicConstraint>();
		collectAtomicConstraints(atomicConstraintsList);
		AtomicConstraint[] atomicConstraints = atomicConstraintsList.toArray(new AtomicConstraint[]{});
		
		if (atomicConstraints.length >= WebEntitiesConfig.CONSTRAINT_MAX_ATOMIC_NUM) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In Constraint.java: Constraint has too many atomic predicates (" + atomicConstraints.length + ").");
			return false;
		}
		
		// Initialize the logicTable
		boolean[] logicTable = new boolean[atomicConstraints.length];
		for (int i = 0; i < logicTable.length; i++)
			logicTable[i] = true;
		
		boolean stop = (logicTable.length == 0);
		while (!stop) {
			// Set the atomic constraints based on logicTable
			HashMap<AtomicConstraint, Boolean> booleanTable = new HashMap<AtomicConstraint, Boolean>();
			for (int i = 0; i < logicTable.length; i++)
				booleanTable.put(atomicConstraints[i], logicTable[i]);
			
			// Evaluate this constraint
			boolean result = evaluate(booleanTable);
			if (result)
				return true;
			
			// Alter the logicTable
			stop = true;
			for (int i = 0; i < logicTable.length; i++) {
				if (logicTable[i]) {
					logicTable[i] = false;
					for (int j = 0; j < i; j++)
						logicTable[j] = true;
					stop = false;
					break;
				}
			}
		}
		return false;
	}
	
	/**
	 * Collects the AtomicConstraints that make up this constraint.
	 * @param atomicConstraints
	 */
	protected abstract void collectAtomicConstraints(ArrayList<AtomicConstraint> atomicConstraints);
	
	/**
	 * Evaluates the constraint based on the boolean table for AtomicConstraints.
	 * @param booleanTable
	 */
	protected abstract boolean evaluate(HashMap<AtomicConstraint, Boolean> booleanTable);
	
	/**
	 * Displays the constraint in a string.
	 */
	public abstract String printToString();
	
	/*
	 * Provides formatting for XML
	 */

	/**
	 * Prints the constraint to an XML element
	 */
	public Element printToXmlElement(Document document) {
		// TODO: Print Constraint to XML
		return null;
	}
	
}
