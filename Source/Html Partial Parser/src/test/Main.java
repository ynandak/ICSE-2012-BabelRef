package test;

import htmllexer.Token;
import htmllexer.Lexer;

import java.io.*;

/**
 * 
 * @author HUNG
 *
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Lexer lexer = new Lexer(new FileReader("generator/html.test"));
		Token nextToken;
		while ((nextToken = lexer.nextToken()) != null) {
			System.out.println(nextToken);
		}
	}
	
}