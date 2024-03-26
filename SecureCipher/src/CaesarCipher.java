import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.crypto.SecretKey;

public class CaesarCipher {
	
	private String alphabet;
	private int key;
	private SecretKey secretkey; 
	
	public CaesarCipher()
	{
		
		alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
//		System.out.println("The encrypted text = "+ this.encrypt("HELLO"));
//		
//		System.out.println("The decrypted text = " + this.decrypt(this.encrypt("HELLO")));
	}
	
	public CaesarCipher(int key)
	{
		
		this();
		this.key = key;
	
	}
	
	 public SecretKey getSecretkey() {
	        return secretkey;
	    }
	public String decrypt(String cipherText)
	{
		String plainText="";
		
		for(int i=0; i<cipherText.length(); i++) // loop through all characters
		{
			char plainCharacter = cipherText.charAt(i);
			int decryptKey = alphabet.length() - key % alphabet.length();
			
			int position = alphabet.indexOf(plainCharacter); // get the position in the alphabet
			
			int newPosition = (position + decryptKey) % alphabet.length(); // position of the cipher character
			
			
			char cipherCharacter = alphabet.charAt(newPosition);
			
			
			plainText += cipherCharacter; // appending this cipher character to the cipherText
			
		}
		
		
		return plainText;
	}
	
	public String encrypt(String plainText)
	{
		String cipherText="";
		
		for(int i=0; i<plainText.length(); i++) // loop through all characters
		{
			char plainCharacter = plainText.charAt(i);
			
			int position = alphabet.indexOf(plainCharacter); // get the position in the alphabet
			
			int newPosition = (position + key) % alphabet.length(); // position of the cipher character
			
			
			char cipherCharacter = alphabet.charAt(newPosition);
			
			
			cipherText += cipherCharacter; // appending this cipher character to the cipherText
			
		}
		
		
		return cipherText;
	}
	 public int getKey() {
	        return key;
	    }
	public void setKey (int  key) {
		this.key = key;
	}

	public static void main(String[] args) {
		
		new CaesarCipher();

	}
	public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
	public void saveKeyFile(String fileName) throws Exception{

	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
	    // Write the SecretKey object to the file
	    oos.writeObject(this.secretkey);
	    oos.close();
	    }

		
	

}
