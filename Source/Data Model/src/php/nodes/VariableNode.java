package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Variable;

import php.ElementManager;
import php.elements.PhpVariable;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class VariableNode extends VariableBaseNode {
	
	private ExpressionNode variableNameExpressionNode;	// The name of the variable
	private String variableName = null;
	
	/*
	Holds a variable. note that the variable name can be expression, 
	
	e.g. $a
	*/
	public VariableNode(Variable variable) {
		super(variable);
		variableNameExpressionNode = ExpressionNode.createInstance(variable.getName());
	}
	
	/**
	 * Resolves the name of the variable.
	 */
	public String resolveVariableName(ElementManager elementManager) {
		if (variableName == null)
			variableName = variableNameExpressionNode.resolveName(elementManager);
		return variableName;
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		String variableName = resolveVariableName(elementManager);
		PhpVariable phpVariable = elementManager.getVariableFromFunctionScope(variableName);
		if (phpVariable == null)
			return new SymbolicNode(this);
		else if (phpVariable.getDataNode() instanceof SymbolicNode) {
			SymbolicNode symbolicNode = new SymbolicNode(this);
			symbolicNode.setParentNode((SymbolicNode) phpVariable.getDataNode());
			return symbolicNode;
		}
		else
			return phpVariable.getDataNode();
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		String variableName = resolveVariableName(elementManager);
		PhpVariable phpVariable = new PhpVariable(variableName);
		return phpVariable;
	}
	
}