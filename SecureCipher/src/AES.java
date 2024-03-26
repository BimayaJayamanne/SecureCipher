

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

public class AES
{

	
	
private SecretKey secretkey; 
    
    
    public AES() throws NoSuchAlgorithmException 
    {
        generateKey();
    }
    
    
    /**
	* Step 1. Generate a AES key using KeyGenerator 
    */
    
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        this.setSecretkey(keyGen.generateKey());        
    }
    
    public byte[] encrypt (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {
        Cipher aesCipher = Cipher.getInstance("AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
        aesCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);       
        return byteCipherText;
    }
    
    public String decrypt (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {        
        Cipher aesCipher = Cipher.getInstance("AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
        aesCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
        byte[] byteDecryptedText = aesCipher.doFinal(strCipherText);        
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
