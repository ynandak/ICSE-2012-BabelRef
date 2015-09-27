package references.detection;

import references.PhpRefToHtmlEntity;
import references.PhpRefToSqlTableColumn;
import references.Reference;
import references.ReferenceManager;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.LiteralNode;

/**
 * PhpVisitor visits PHP elements and detects entities.
 *  
 * @author HUNG
 *
 */
public class PhpVisitor {
	
	private String relativeFilePath;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public PhpVisitor(String relativeFilePath, ReferenceManager referenceManager) {
		this.relativeFilePath = relativeFilePath;
		this.referenceManager = referenceManager;
	}

	/**
	 * Visits a PHP $_REQUEST, $_POST, or $_GET variable.
	 */
	public void visitRequestVariable(datamodel.nodes.DataNode dataNode) {
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String htmlInputName = literalNode.getStringValue(); // e.g. 'user' in $_GET['user']
			
			Reference reference = new PhpRefToHtmlEntity(htmlInputName, literalNode.getLocation(), relativeFilePath);
			referenceManager.addReference(reference);
		}
	}
	
	/**
	 * Visits a PHP mysql_query statement.
	 */
	public void visitMysqlQueryStatement(datamodel.nodes.DataNode dataNode, String scope) {
		while (dataNode instanceof ConcatNode && !((ConcatNode) dataNode).getChildNodes().isEmpty()) {
			dataNode = ((ConcatNode) dataNode).getChildNodes().get(0); 
		}
		
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String sqlCode = literalNode.getStringValue(); // e.g. SELECT name FROM users
			ReferenceDetector.findReferencesInSqlCode(sqlCode, literalNode.getLocation(), scope, referenceManager);
		}
	}
	
	/**
	 * Visits a PHP the table column embedded in a PHP array access (e.g. 'name' in $sql_row['name']).
	 */
	public void visitSqlTableColumn(datamodel.nodes.DataNode dataNode, String scope) {
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String sqlTableColumnName = literalNode.getStringValue(); // e.g. 'name' in $sql_row['name']
			if (sqlTableColumnName.matches("[0-9]+")) // Ignore numbers, e.g. $sql_row[1]
				return;
			
			Reference reference = new PhpRefToSqlTableColumn(sqlTableColumnName, literalNode.getLocation(), scope);
			referenceManager.addReference(reference);
		}
	}
	
}
