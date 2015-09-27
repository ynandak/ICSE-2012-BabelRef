package htmlparser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import sourcetracing.Location;
import sourcetracing.SingleLocation;

import logging.MyLevel;
import logging.MyLogger;
import html.elements.HtmlAttribute;
import html.elements.HtmlAttributeValue;
import html.elements.HtmlElement;
import html.elements.HtmlTag;
import html.elements.HtmlText;
import htmllexer.Lexer;
import htmllexer.Token;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlParser {
	
	private ParsingState parsingState;
	
	private String htmlCode;
	
	private Location htmlLocation;
	
	/**
	 * Constructor
	 * @param parsingState
	 * @param htmlCode
	 * @param htmlLocation
	 */
	public HtmlParser(ParsingState parsingState, String htmlCode, Location htmlLocation) {
		this.parsingState = parsingState;
		this.htmlCode = htmlCode;
		this.htmlLocation = htmlLocation;
	}
	
	/*
	 * Parse the HTML code
	 */
	
	public ArrayList<HtmlElement> parse() {
		ArrayList<Token> tokens = tokenize();
		return parseElements(tokens);
	}
	
	private ArrayList<Token> tokenize() {
		ArrayList<Token> tokens = new ArrayList<Token>();
		
		Lexer lexer = new Lexer(new StringReader(htmlCode));
		lexer.yybegin(parsingState.getLexicalState()); // Set the lexical state
		while (true) {
			try {
				Token nextToken = lexer.nextToken();
				if (nextToken == null)
					break;
				tokens.add(nextToken);				
			} catch (IOException e) {
				MyLogger.log(MyLevel.JAVA_EXCEPTION, e.getStackTrace().toString());
			}			
		}
		parsingState.setLexicalState(lexer.yystate()); // Update the lexical state
		
		return tokens;
	}
	
	private ArrayList<HtmlElement> parseElements(ArrayList<Token> tokens) {
		ArrayList<HtmlElement> htmlElements = new ArrayList<HtmlElement>();
		
		for (Token token : tokens) {
			String tokenValue = token.getValue();
			Location tokenLocation = new SingleLocation(htmlLocation, token.getPosition());
			
			switch (token.getType()) {
				case OpeningTag: {
					HtmlTag tag = HtmlTag.createHtmlTag(tokenValue, tokenLocation);
					if (!parsingState.htmlStackIsEmpty())
						parsingState.peekHtmlTag().addChildElement(tag);
					parsingState.pushHtmlTag(tag);
					break;
				}
				case ClosingTag: {
					if (!parsingState.htmlStackIsEmpty() && parsingState.peekHtmlTag().getLowerCaseType().equals(tokenValue.toLowerCase())) {
						HtmlTag tag = parsingState.popHtmlTag();
						
						/*====== ELEMENT EXTRACTION ======*/
						htmlElements.add(tag);
						/*================================*/
					}
					break;
				}
				case AttrName: {
					HtmlTag tag = parsingState.peekHtmlTag();
					HtmlAttribute attribute = new HtmlAttribute(tag, tokenValue, tokenLocation);
					tag.addAttribute(attribute);
					break;
				}
				case AttrValStart: {
					HtmlTag tag = parsingState.peekHtmlTag();
					HtmlAttribute attribute = tag.getLastAttribute();				
					attribute.addValueFragment("", tokenLocation.getLocationAtOffset(1));
					break;
				}
				case AttrValFrag: {
					HtmlTag tag = parsingState.peekHtmlTag();
					HtmlAttribute attribute = tag.getLastAttribute();				
					attribute.addValueFragment(tokenValue, tokenLocation);
					break;
				}
				case AttrValEnd: {
					HtmlTag tag = parsingState.peekHtmlTag();
					HtmlAttribute attribute = tag.getLastAttribute();				
					HtmlAttributeValue attributeValue = attribute.getValue();
					attributeValue.unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
					
					/*====== ELEMENT EXTRACTION ======*/
					htmlElements.add(attribute);
					/*================================*/
					break;
				}
				case AttrValue: {
					HtmlTag tag = parsingState.peekHtmlTag();
					HtmlAttribute attribute = tag.getLastAttribute();				
					attribute.addValueFragment(tokenValue, tokenLocation);
					
					/*====== ELEMENT EXTRACTION ======*/
					htmlElements.add(attribute);
					/*================================*/
					break;
				}
				case Text: {
					if (!parsingState.htmlStackIsEmpty()) {
						HtmlText text = new HtmlText(tokenValue, tokenLocation);
						parsingState.peekHtmlTag().addChildElement(text);
					}
					break;
				}
			}
		}
		
		return htmlElements;
	}
	
}
