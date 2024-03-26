/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ahmed
 */

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;
import java.util.Base64;

public class DES
{

	
	
private SecretKey secretkey; 
    
    
    public DES() throws NoSuchAlgorithmException 
    {
        generateKey();
    }
    
    
    /**
	* Step 1. Generate a DES key using KeyGenerator 
    */
    
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        this.setSecretkey(keyGen.generateKey());        
    }
    
    public byte[] encrypt (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance("DES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);       
        return byteCipherText;
    }
    
    public String decrypt (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {        
        Cipher desCipher = Cipher.getInstance("DES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
        byte[] byteDecryptedText = desCipher.doFinal(strCipherText);        
        return new String(byteDecryptedText);
    }   

    /**
     * @return the secretkey
     */
    public SecretKey getSecretkey() {
        return secretkey;
    }

    /**
     * @param secretkey the secretkey to set
     */
    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
    public void saveKeyFile(String fileName) throws Exception
    {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
    // Write the SecretKey object to the file
    oos.writeObject(this.secretkey);
    oos.close();
    }

}
