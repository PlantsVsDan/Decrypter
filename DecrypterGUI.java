/**
 * File:        DecrypterGUI.java
 * Description: The JFrame that handles user interaction. It takes in encrypted messages and keys, and outputs deceptions.
 * Created:     11/13/2018
 *
 * @author Justin Zhu and danIv
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;

public class DecrypterGUI 	extends JFrame
							implements 	ActionListener, 
										PropertyChangeListener {
    private JTextArea ciphertext, plaintext, keyInput, keyGuess;
    private JButton go;
    private KeyGen keyGen;
    private CharSet decrypterCharSet; // CharSet of the key: NUMERIC for Caesar, ALPHABETIC for Vigenere
    private String defaultKeyMessage = "Enter your key here, or no key for brute force\0";
    private JProgressBar progressBar;
    private Task bruteForce;
    private ProgressMonitor progressMonitor;

    // Constructor
    public DecrypterGUI(int decrypterNum) {
        super("Decrypter");

        if (decrypterNum == 1) {
            // Caesar cipher
            decrypterCharSet = CharSet.NUMERIC;
            defaultKeyMessage = "Enter your shift here, or no shift for brute force\0";
        } else if (decrypterNum == 2) {
            // Vigenere cipher
            defaultKeyMessage = "Enter your key here, the length of the longest key you want to try, or nothing at all for a brute force of keys up to 3 characters in length\0";
            decrypterCharSet = CharSet.ALPHABETIC;
        }
        setupGui();

        ciphertext.setText("Type or paste your text here...");
        keyInput.setText(defaultKeyMessage);
    }

    public void refresh() {
        String text = ciphertext.getText().trim();
        String key = keyInput.getText();
        boolean isKeyLength = decrypterCharSet != CharSet.NUMERIC;

        if(isKeyLength) {
            for (char c : key.toCharArray()) {
                if (!CharSet.NUMERIC.isInCharSet(c) && !Character.isWhitespace(c))
                    isKeyLength = false;
            }
        }

        if (key.equals("") || key.equals(defaultKeyMessage) || isKeyLength) {
            // No key was provided, or they entered the maximum length of the key

            int maxLength = 3;

	        try {
	            maxLength = Integer.parseInt(key);
	        } catch (NumberFormatException e) {
	            // This means that the user didn't specify how long they wanted their key, but rather inputted a key (or nothing at all) and want default brute forcing
	            // This means that we can leave maxLength at the default: 3
	        }

	        keyGen = new KeyGen(decrypterCharSet, text, maxLength, "dictionary.txt");
	        
	        bruteForce = new Task(); // this task should keep track of the progress of the brute force in KeyGen
	        bruteForce.addPropertyChangeListener(this);
			System.out.println("executing");
			bruteForce.execute();
			
			keyGen.generateAll();
	        setText(); // Take the KeyGen's output, format it, and display it in the lower text boxes
        } else {
            Decrypter cipher;
            switch (decrypterCharSet) {
                case ALPHABETIC:
                    cipher = new Vigenere(text);
                    break;
                default:
                    cipher = new Caesar(text);
                    break;
            }
            plaintext.setText(cipher.decrypt(key));
            keyGuess.setText("");
        }
    }

    private void setText() {
        String decryptions = keyGen.getKeyDecryptions();
        String keys = "", texts = "";
        String[] strings = decryptions.split("\0");
        for (int i = 0; i < strings.length; i++) {
            if (i % 2 == 0) {
                keys += strings[i] + "\n";
            } else {
                texts += strings[i] + "\n";
            }
        }

        if (strings.length <= 1) {
            texts = "No probable decryptions found";
            keys = "No probable keys found";
        }

        plaintext.setText(texts);
        keyGuess.setText(keys);
    }

    // Called when the Decrypt button is clicked
    public void actionPerformed(ActionEvent e) {
        refresh();
    }

    // ********************** GUI setup ********************************

    private void setupGui() {
        ciphertext = new JTextArea(10, 20);
        ciphertext.setLineWrap(true);
        ciphertext.setWrapStyleWord(true);
        JScrollPane ciphertextPane = new JScrollPane(ciphertext,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        plaintext = new JTextArea(10, 20);
        plaintext.setEditable(false);
        plaintext.setBackground(Color.LIGHT_GRAY);
        plaintext.setLineWrap(true);
        plaintext.setWrapStyleWord(true);
        JScrollPane plaintextPane = new JScrollPane(plaintext,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        keyInput = new JTextArea(10, 15);
        keyInput.setLineWrap(true);
        keyInput.setWrapStyleWord(true);
        JScrollPane keyInputPane = new JScrollPane(keyInput,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        keyGuess = new JTextArea(10, 15);
        keyGuess.setEditable(false);
        keyGuess.setBackground(Color.LIGHT_GRAY);
        keyGuess.setLineWrap(true);
        keyGuess.setWrapStyleWord(true);
        JScrollPane keyGuessPane = new JScrollPane(keyGuess,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        go = new JButton("Decrypt");
        go.addActionListener(this);

        JLabel cipherTextLabel = new JLabel("Ciphertext:");
        JLabel plainTextLabel = new JLabel("Plaintext:");
        Box box1 = Box.createVerticalBox();
        box1.add(Box.createVerticalStrut(10));
        box1.add(cipherTextLabel);
        box1.add(ciphertextPane);
        box1.add(Box.createVerticalStrut(10));
        box1.add(plainTextLabel);
        box1.add(plaintextPane);

        JLabel keyInputLabel = new JLabel("Key Input:");
        Box box2 = Box.createVerticalBox();
        box2.add(Box.createVerticalStrut(10));
        box2.add(keyInputLabel);
        box2.add(keyInputPane);
        box2.add(Box.createVerticalStrut(10));

        Box box3 = Box.createHorizontalBox();
        box3.add(box1);
        box3.add(Box.createHorizontalStrut(10));

        JLabel keyGuessLabel = new JLabel("Key Guess:");
        Box box4 = Box.createVerticalBox();
        box4.add(keyGuessLabel);
        box4.add(keyGuessPane);
        box2.add(box4);
        box3.add(box2);

        Box boxGo = Box.createHorizontalBox();
        boxGo.add(Box.createHorizontalStrut(20));
        boxGo.add(go);
        boxGo.add(Box.createHorizontalStrut(20));

        Box box5 = Box.createVerticalBox();
        box5.add(box3);
        box5.add(Box.createVerticalStrut(10));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        box5.add(progressBar);
        box5.add(Box.createVerticalStrut(10));
        box5.add(boxGo);

        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        c.add(box5);
    }
    
    private int getKeyGenProgress(String currentKey) {
    	int length = keyGen.getKeyMaxLength();
    	// For these purposes, it should be safe to assume the number of chars in the character set + 1 (we will treat no char as a char)
    	// Example: _z < aa
    	// _ < a
    	// So this won't be perfect, but it will do
    	long power = 1;
    	long ans = 0;
    	for(int i = currentKey.length() - 1; i >= 0; i--) {
    		ans += (currentKey.charAt(i) - CharSet.ALPHABETIC.getBaseChar() + 1) * power;
    		power *= CharSet.ALPHABETIC.getNumChars() + 1;
    	}
    	return (int) (ans * 100 / Math.pow((long) CharSet.ALPHABETIC.getNumChars() + 1, keyGen.getKeyMaxLength()));
    }
    
    // ********************** Multithreading for Progress Bar ******************************** 
	// Copied (and modified) from ProgressBarDemo.java and ProgressMonitorDemo.java
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	System.out.println("starting");
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            firePropertyChange("progress", 0, progress + 1);
            while (progress < 100) {
            	//Sleep for 1/100th of a second.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignore) {}
                int newProgress = getKeyGenProgress(keyGen.getCurrentKey()) + 1; // +1 because it *should* say 100% at the end
                System.out.println(progress);
                newProgress = Math.min(newProgress, 100);
                setProgress(newProgress);
                firePropertyChange("progress", progress, newProgress);
                progress = newProgress;
            }
            System.out.println("out of loop");
            return null;
        }
        
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            System.out.println("Done!");
        }
    }
    
    /**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			System.out.println("hi");
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			//progressMonitor.setProgress(progress);
			//String message = String.format("Completed %d%%.\n", progress);
			//progressMonitor.setNote(message);
		}

	}

}
