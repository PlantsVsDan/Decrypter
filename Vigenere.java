/**
 * File:        Vigenere.java
 * Description: Decrypts Vigenere Ciphers with a given key and/or CharSet.
 * Created:     11/09/18
 *
 * @author Justin Zhu
 * @version 1.0
 */

public class Vigenere extends Decrypter {

    public Vigenere(String cipherText) {
        super(cipherText);
    }

    @Override
    public String decrypt(String key) {
        return decrypt(key, CharSet.ALPHABETIC);
    }

    @Override
    public String decrypt(String key, CharSet charset) {
        char base = charset.getBaseChar(), keyBase = charset.getBaseChar();
        int numChars = charset.getNumChars();

        String plainText = "";
        for (int i = 0, keyIndex = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);
            if (charset.isInCharSet(c) && !CharSet.PUNCTUATION.isInCharSet(c)) {

                int shift = key.charAt(keyIndex % key.length());
                if (Character.isLetter(shift)) {
                    shift -= Character.isUpperCase((char) shift) ? 'A' : 'a';
                } else if (Character.isDigit(shift)) {
                    shift -= '0';
                } else {
                    return null; // Invalid key
                }

                c = super.undoShift(c, shift, charset);
                keyIndex++;
            }

            plainText += "" + c;
        }

        return plainText;
    }
}
