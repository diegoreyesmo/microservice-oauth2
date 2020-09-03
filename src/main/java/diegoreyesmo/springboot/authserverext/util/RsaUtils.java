package diegoreyesmo.springboot.authserverext.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import diegoreyesmo.springboot.authserverext.exception.AuthServerException;

public class RsaUtils {

	private static final String ALGORITHM = "RSA";

	private static byte[] parsePEMFile(File pemFile) throws AuthServerException {
		if (!pemFile.isFile() || !pemFile.exists()) {
			throw new AuthServerException(String.format("El archivo '%s' no existe", pemFile.getAbsolutePath()));
		}
		try (FileReader fileReader = new FileReader(pemFile); PemReader reader = new PemReader(fileReader)) {
			PemObject pemObject = reader.readPemObject();
			return pemObject.getContent();
		} catch (IOException e) {
			throw new AuthServerException(e.getMessage(), e);
		}

	}

	private static PublicKey getPublicKey(byte[] keyBytes) throws AuthServerException {
		PublicKey publicKey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			publicKey = kf.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AuthServerException(e.getMessage(), e);
		}

		return publicKey;
	}

	private static PrivateKey getPrivateKey(byte[] keyBytes) throws AuthServerException {
		PrivateKey privateKey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			privateKey = kf.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AuthServerException(e.getMessage(), e);
		}
		return privateKey;
	}

	public static PublicKey readPublicKeyFromFile(String filepath) throws AuthServerException {
		byte[] bytes = parsePEMFile(new File(filepath));
		return getPublicKey(bytes);
	}

	public static PrivateKey readPrivateKeyFromFile(String filepath) throws AuthServerException {
		byte[] bytes = parsePEMFile(new File(filepath));
		return getPrivateKey(bytes);
	}
}
