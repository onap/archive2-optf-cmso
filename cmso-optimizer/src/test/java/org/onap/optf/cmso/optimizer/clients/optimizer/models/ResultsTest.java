/*
 *  ============LICENSE_START==============================================
 *  Copyright (c) 2019 AT&T Intellectual Property.
 *  =======================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain a
 *  copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 */

package org.onap.optf.cmso.optimizer.clients.optimizer.models;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.yaml.snakeyaml.introspector.PropertyUtils;

@RunWith(MockitoJUnitRunner.class)
public class ResultsTest extends PropertyUtils {
    @Test
    public void yamlTests() {
        OptimizerResponseUtility util = new OptimizerResponseUtility();
        File resultsFile = new File("src/test/data/resultsTest001.yaml");
        OptimizerResults results = util.parseOptimizerResult(resultsFile);
        Assert.assertTrue(results != null);

    }


}
