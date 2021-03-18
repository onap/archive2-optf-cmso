/*
 * ============LICENSE_START==============================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * =======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a 
 * copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express 
 * or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 * 
 */

package org.onap.optf.cmso.utilities;

import org.onap.optf.cmso.optimizer.common.PropertiesManagement;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;

/**
 * The Class PropertiesAdmin.
 */
public class PropertiesAdmin {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing argument");
            return;
        }
        String value = "";
        if (args[0].startsWith("dec:")) {
            value = PropertiesManagement.getDecryptedValue(args[0].substring(4));
        } else {
            value = PropertiesManagement.getEncryptedValue(args[0]);
        }
        System.out.println(args[0] + " : " + value);
    }
    @Test
    public void testEncryptionDecryption()
    {
    String encryptedInputData = PropertiesManagement.getEncryptedValue("Hello");
    String decryptedData = PropertiesManagement.getDecryptedValue(encryptedInputData);
    assertEquals(decryptedData,"Hello");
    }

}
