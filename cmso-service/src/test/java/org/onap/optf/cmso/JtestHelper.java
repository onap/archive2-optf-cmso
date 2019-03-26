/*
 * ============LICENSE_START=======================================================================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * ===================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================================================
 * 
 */

package org.onap.optf.cmso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.text.StrSubstitutor;

public class JtestHelper {
    private static String templatefolder = "src/test/templates" + File.separator;

    /**
     * Template.
     *
     * @param filename the filename
     * @param values the values
     * @return the string
     */
    public static String template(String filename, Map<String, String> values) {
        String data = "";
        Scanner sc = null;
        try {
            File tfld = new File(templatefolder + filename);
            sc = new Scanner(tfld);
            sc.useDelimiter("\\Z");
            data = sc.next();
            StrSubstitutor ss = new StrSubstitutor(values);
            data = ss.replace(data);
        } catch (FileNotFoundException e) {
            data = "";
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
        return data;
    }
}
