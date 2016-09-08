package me.noip.chankyin.mrmrg.ui.comp;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JNumberField extends JTextField{
	public JNumberField(){
		setDocument(new NumberOnlyDocument());
	}

	public JNumberField(String text){
		super(text);
		setDocument(new NumberOnlyDocument());
	}

	public JNumberField(int columns){
		super(columns);
		setDocument(new NumberOnlyDocument());
	}

	public JNumberField(String text, int columns){
		super(text, columns);
		setDocument(new NumberOnlyDocument());
	}

	public JNumberField(NumberOnlyDocument doc, String text, int columns){
		super(doc, text, columns);
	}

	public static class NumberOnlyDocument extends PlainDocument{
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException{
			if(str != null && str.length() > 0){
				int[] filtered = str.chars().filter(c -> (char) c == '.' || (char) c == '-' || '0' <= (char) c && (char) c <= '9').toArray();
				if(filtered.length > 0){
					super.insertString(offs, new String(filtered, 0, filtered.length), a);
				}
			}
		}
	}
}
