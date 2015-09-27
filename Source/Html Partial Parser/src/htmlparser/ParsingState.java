package htmlparser;

import html.elements.HtmlScriptTag;
import html.elements.HtmlTag;
import htmllexer.Lexer;

import java.util.Stack;

/**
 * ParsingState includes two properties: (1) the lexical state of the lexer, 
 * and (2) the stack of the HTML tags that the parser has recognized so far.
 * 
 * @author HUNG
 *
 */
public class ParsingState {
	
	protected int lexicalState = Lexer.YYINITIAL;
	
	protected Stack<HtmlTag> htmlStack = new Stack<HtmlTag>();
	
	/*
	 * Lexical State
	 */
	
	public void setLexicalState(int lexicalState) {
		this.lexicalState = lexicalState;
	}
	
	public int getLexicalState() {
		return lexicalState;
	}
	
	/*
	 * HTML Stack
	 */
	
	public void pushHtmlTag(HtmlTag tag) {
		htmlStack.push(tag);
	}
	
	public HtmlTag popHtmlTag() {
		return htmlStack.pop();
	}
	
	public HtmlTag peekHtmlTag() {
		return htmlStack.peek();
	}
	
	public boolean htmlStackIsEmpty() {
		return htmlStack.isEmpty();
	}
	
	/*
	 * Save the state
	 */
	
	public ParsingState save() {
		ParsingState clonedState = new ParsingState();
		clonedState.lexicalState = lexicalState;
		clonedState.htmlStack = new Stack<HtmlTag>();
		clonedState.htmlStack.addAll(htmlStack);
		return clonedState;
	}
	
	/*
	 * Get properties
	 */
	
	public boolean isInJavascriptCode() {
		return (!htmlStackIsEmpty() && peekHtmlTag() instanceof HtmlScriptTag);
	}
	
}
