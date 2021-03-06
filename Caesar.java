/**
 * File:        Caesar.java
 * Description: Decrypts Caesar Ciphers with a given shift and/or CharSet.
 * Created:     11/08/18
 *
 * @author Justin Zhu
 * @version 1.0
 */

public class Caesar extends Decrypter {

    public Caesar(String cipherText) {
        super(cipherText);
    }

    public String decrypt(int shift) {
        return decrypt(shift, CharSet.ALPHANUMERIC);
    }

    public String decrypt(int shift, CharSet charset) {
        char base = charset.getBaseChar();
        int numChars = charset.getNumChars();

        String plainText = "";
        for (int i = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);

            if (charset.isInCharSet(c))
                c = super.undoShift(c, shift, charset);

            plainText += "" + c;
        }

        return plainText;
    }

    @Override
    public String decrypt(String key) {
        return decrypt(key, CharSet.ALPHANUMERIC);
    }

    @Override
    public String decrypt(String key, CharSet charset) {
        try {
            return decrypt(Integer.parseInt(key) % 26, charset);
        } catch (Exception e) {
            return null;
        }
    }
}
