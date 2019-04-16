/*******************************************************************************
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
 ******************************************************************************/

package org.onap.optf.cmso;

import java.util.HashMap;
import java.util.Map;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

public class CmsoEnvironmentPostProcessor implements EnvironmentPostProcessor {
    // TODO tested in ONAP springboot and this is called before all of the properties files have been
    // loaded...
    // perhaps there is a post post processor? Until this works. DB password will be in the clear in the
    // proeprties files.
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String pwd = environment.getProperty("cmso.database.password");
        if (pwd != null) {
            pwd = PropertiesManagement.getDecryptedValue(pwd);
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.password", pwd);
            MapPropertySource propertySource = new MapPropertySource("abc", map);
            MutablePropertySources proeprtySources = environment.getPropertySources();
            proeprtySources.addLast(propertySource);
        }
    }

}
