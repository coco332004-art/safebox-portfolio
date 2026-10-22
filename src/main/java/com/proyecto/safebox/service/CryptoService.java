
package com.proyecto.safebox.service;

import com.proyecto.safebox.model.Mensajes;
import com.proyecto.safebox.model.Usuarios;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

// Servicio encargado de todo el proceso criptográfico de los mensajes. 
// Utiliza una master key de la aplicación y un salt propio de cada usuario para derivar una clave simétrica (PBKDF2 + AES-GCM) usada al cifrar/descifrar.

@Service
public class CryptoService {

    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;     // bits
    private static final int NONCE_LENGTH = 12;        // bytes
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;         // bits

    private final String masterKey;
    private final SecureRandom secureRandom = new SecureRandom(); // Generador seguro de números aleatorios

    public CryptoService(@Value("${safebox.master-key}") String masterKey) { // Inyecta la master key desde application.properties
        this.masterKey = masterKey;
    }

    // Método privado que, a partir del salt del usuario (en Base64) y de la master key, 
    // deriva una clave simétrica usando PBKDF2WithHmacSHA256. Esta clave se usa después para operaciones de cifrado y descifrado.
    private SecretKey deriveKey(String userSaltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(userSaltBase64);
            PBEKeySpec spec = new PBEKeySpec(
                    masterKey.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Error derivando llave PBKDF2", e);
        }
    }

    // Toma el contenido en texto plano del mensaje, deriva la clave a partir del usuario, genera un nonce aleatorio y cifra el texto utilizando AES/GCM. 
    // Guarda en el mensaje el contenido cifrado y el nonce en Base64, limpiando o separando el contenido plano según el diseño.
    public void encryptMessage(Mensajes mensaje) {
        try {
            Usuarios u = mensaje.getUsuario();
            if (u == null) {
                throw new IllegalArgumentException("El mensaje debe tener usuario antes de cifrar.");
            }
            SecretKey key = deriveKey(u.getEncryptionSalt());

            byte[] nonce = new byte[NONCE_LENGTH];
            secureRandom.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] cipherBytes = cipher.doFinal(
                    mensaje.getContenido().getBytes(StandardCharsets.UTF_8));

            mensaje.setContenidoEnc(Base64.getEncoder().encodeToString(cipherBytes));
            mensaje.setNonce(Base64.getEncoder().encodeToString(nonce));
            mensaje.setContenido(null); // el texto plano desaparece del modelo
        } catch (Exception e) {
            throw new IllegalStateException("Error cifrando mensaje", e);
        }
    }

    // Opera a la inversa de encryptMessage: con el contenido cifrado y el nonce (contenido unico) almacenados en la entidad Mensajes, 
    // deriva la misma clave y reconstruye el texto plano para mostrarlo en la aplicación.
    public void decryptMessage(Mensajes mensaje) {
        try {
            Usuarios u = mensaje.getUsuario();
            if (u == null || mensaje.getContenidoEnc() == null || mensaje.getNonce() == null) {
                return;
            }
            SecretKey key = deriveKey(u.getEncryptionSalt());

            byte[] nonce = Base64.getDecoder().decode(mensaje.getNonce());
            byte[] cipherBytes = Base64.getDecoder().decode(mensaje.getContenidoEnc());

            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            mensaje.setContenido(new String(plainBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Error descifrando mensaje", e);
        }
    }
}
