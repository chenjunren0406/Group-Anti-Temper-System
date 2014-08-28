/**
 * The RSA Encryption code basically comes from:
 *     http://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
 * The author who owns the credit is "JavaDigest"
 * 
 * Author of this class is Yanfei Wu
 */
package GASS.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class CryptoKit {
	public static final String PRIVATE_KEY_FILE = "privateKey";
	public static final String PUBLIC_KEY_FILE = "publicKey";
	
	/* RSA key pair management */
	public static boolean writePublicKeyToFile(String identity, PublicKey publicKey) {
		try {
			File publicKeyFile = new File(identity + "." + PUBLIC_KEY_FILE);
			
			// Create files to store public key
			publicKeyFile.delete();
			publicKeyFile.createNewFile();
	
			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(publicKey);
			publicKeyOS.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * generate RSA key pair for a particular identity
	 * @param name identity who owns the generated key pair
	 * @return true if successful
	 */
	public static boolean generateKeyFile(String name) {
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			final KeyPair key = keyGen.generateKeyPair();

			File privateKeyFile = new File(name + "." + PRIVATE_KEY_FILE);
			File publicKeyFile = new File(name + "." + PUBLIC_KEY_FILE);
	
			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null) {
				privateKeyFile.getParentFile().mkdirs();
			}
			privateKeyFile.createNewFile();
	
			if (publicKeyFile.getParentFile() != null) {
				publicKeyFile.getParentFile().mkdirs();
			}
			publicKeyFile.createNewFile();
	
			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(key.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
			privateKeyOS.writeObject(key.getPrivate());
			privateKeyOS.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * check whether key pair for a particular identity exists
	 * @param name the identity for checking
	 * @return true if key pair exists
	 */
	public static boolean areKeysPresent(String name) {
	    File privateKey = new File(name + "." + PRIVATE_KEY_FILE);
	    File publicKey = new File(name + "." + PUBLIC_KEY_FILE);
	    return (privateKey.exists() && publicKey.exists());
	}
	
	/**
	 * get public key of a particular identity
	 * @param name the identity who owns the public key
	 * @return public key instance, null if error occurs
	 */
	public static PublicKey getPublicKey(String name) {
		PublicKey publicKey = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					new FileInputStream(name + "." + PUBLIC_KEY_FILE));
			publicKey = (PublicKey) inputStream.readObject();
			inputStream.close();
		} catch (Exception e) {
			System.out.println(name + "'s public key doesn't exist.");
		}
		
		return publicKey;
	}
	
	/**
	 * get private key of a particular identity
	 * @param name the identity who owns the private key
	 * @return private key instance, null if error occurs
	 */
	public static PrivateKey getPrivateKey(String name) {
		PrivateKey privateKey = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					new FileInputStream(name + "." + PRIVATE_KEY_FILE));
			privateKey = (PrivateKey) inputStream.readObject();
			inputStream.close();
		} catch (Exception e) {
			System.out.println(name + "'s private key doesn't exist.");
		}
		
		return privateKey;
	}
	
	/* RSA encryption and decryption */
	
	/**
	 * Use RSA public key to process (typically encrypt) the byte array
	 * @param text the content to be processed
	 * @param key the public key
	 * @return result of process, null if error occurs
	 */
	public static byte[] RSAPublicProcess(byte[] text, PublicKey key) {
	    byte[] encrypted = null;
	    try {
	    	// get an RSA cipher object and print the provider
	    	final Cipher cipher = Cipher.getInstance("RSA");
	    	// encrypt the plain text using the public key
	    	cipher.init(Cipher.ENCRYPT_MODE, key);
	    	encrypted = cipher.doFinal(text);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return encrypted;
	}

	/**
	 * Use RSA private key to process (typically decrypt) the byte array
	 * @param text the content to be processed
	 * @param key the private key
	 * @return result of process, null if error occurs
	 */
	public static byte[] RSAPrivateProcess(byte[] text, PrivateKey key) {
	    byte[] decrypted = null;
	    try {
	    	// get an RSA cipher object and print the provider
	    	final Cipher cipher = Cipher.getInstance("RSA");
	    	// decrypt the text using the private key
	    	cipher.init(Cipher.DECRYPT_MODE, key);
	    	decrypted = cipher.doFinal(text);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	    return decrypted;
	}
	
	/* RSA Signature */
	
	/**
	 * create signature of a byte array with private key
	 * @param content the array to be signed
	 * @param pk the private key used to sign
	 * @return the signature, null if error occurs
	 */
	public static byte[] signWithRSA(byte[] content, PrivateKey pk) {
		try {
			Signature dsa = Signature.getInstance("SHA1withRSA");
			dsa.initSign(pk);
			dsa.update(content);
			return dsa.sign();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * very the signature of a byte array with the signature and public key received
	 * @param content the array to be verified
	 * @param signature signature received
	 * @param pk public key used to sign the content
	 * @return true if verified, false otherwise or error occurs
	 */
	public static boolean verifyWithRSA(byte[] content, byte[] signature, PublicKey pk) {
		try {
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(pk);
			sig.update(content);
			return sig.verify(signature);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/* AES encryption using array of bytes as key */
	public static void encryptAES(byte[] key, InputStream is, OutputStream os) throws Throwable {
		encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os);
	}

	public static void decryptAES(byte[] key, InputStream is, OutputStream os) throws Throwable {
		encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
	}

	private static void encryptOrDecrypt(byte[] key, int mode, InputStream is, OutputStream os) throws Throwable {
		Cipher c = Cipher.getInstance("AES");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		
		if (mode == Cipher.ENCRYPT_MODE) {
			c.init(Cipher.ENCRYPT_MODE, k);
			CipherInputStream cis = new CipherInputStream(is, c);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			c.init(Cipher.DECRYPT_MODE, k);
			CipherOutputStream cos = new CipherOutputStream(os, c);
			doCopy(is, cos);
		}
	}

	private static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}

}