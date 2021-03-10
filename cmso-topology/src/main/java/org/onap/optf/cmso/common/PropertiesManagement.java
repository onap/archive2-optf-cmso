/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed under the Creative
 * Commons License, Attribution 4.0 Intl. (the "License"); you may not use this documentation except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.common;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class PropertiesManagement {

    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();

    private  static final String algorithm = "AES";
    private  static final String cipherMode = "GCM";
    private  static final String paddingScheme = "NoPadding";
    private  static final String transformation = algorithm + "/" + cipherMode + "/" + paddingScheme;
    private  static final SecureRandom random = new SecureRandom();

    @Autowired
    Environment env;

    /**
     * Gets the property.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property
     */
    public String getProperty(String key, String defaultValue) {
        String value = env.getProperty(key, defaultValue);
        value = getDecryptedValue(value);
        return value;
    }

    /**
     * Gets the decrypted value.
     *
     * @param value the value
     * @return the decrypted value
     */
    public static String getDecryptedValue(String value) {
        if (value.startsWith("enc:")) {
            String secret = getSecret();
            value = decrypt(secret, value.substring(4));
        }
        return value;
    }

    /**
     * Gets the encrypted value.
     *
     * @param value the value
     * @return the encrypted value
     */
    public static String getEncryptedValue(String value) {
        String secret = getSecret();
        value = encrypt(secret, value);
        return value;
    }

    private static final String encrypt(String key, String value) {
        try {
	    byte[] bytesIV = new byte[16];
            random.nextBytes(bytesIV);
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            errors.error("Unexpected exception {0}", ex.getMessage());
            debug.debug("Unexpected exception", ex);
        }

        return null;
    }

    private static final String decrypt(String key, String encrypted) {
        try {
	    byte[] bytesIV = new byte[16];
            random.nextBytes(bytesIV);
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            errors.error("Unexpected exception {0}", ex.getMessage());
            debug.debug("Unexpected exception", ex);
        }
        return null;
    }

    private static String getSecret() {
        return "ONAPCMSOSECRETIV";
    }

}
