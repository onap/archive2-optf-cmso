/*
 * Copyright © 2018 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.service.rs;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.optf.cmso.JpaInit;
import org.onap.optf.cmso.JtestHelper;
import org.onap.optf.cmso.common.CMSRequestError;
import org.onap.optf.cmso.service.rs.models.CMSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.collection.mutable.StringBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CMSOServiceImplTest {

    @Autowired
    CMSOService cMSOServiceImpl;

    @Autowired
    private TestEntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        JpaInit.init(entityManager);
    }

    @Test
    public void test_createScheduleRequest() {
        String[] templates = {"changemanagement/MultipleVnfImmediate.json.template",};
        String[] domains = {"ChangeManagement",};
        String[] userIds = {"jf9860",};
        String[] callbackUrls = {"http://localhost:8089/",};
        String[] callbackDatum = {"sdafafadfdasfdsafdsa",};
        String[] workflows = {"Replace", "Update", "NewOne",};
        Integer[] normalDurationInSeeconds = {10,};
        Integer[] additionalDurationInSeeconds = {10,};
        String[] results = {"500:", // {additionalDurationInSeconds=10, workflow=Replace,
                                    // domain=ChangeManagement,
                                    // callbackData=sdafafadfdasfdsafdsa, testid=79e1,
                                    // callbackUrl=http://localhost:8089/,
                                    // uuid=a36b45b9-dff4-45b4-ac6b-2a4f35e179e1, userId=jf9860,
                                    // normalDurationInSeconds=10}
                "500:", // {additionalDurationInSeconds=10, workflow=Update,
                        // domain=ChangeManagement,
                        // callbackData=sdafafadfdasfdsafdsa, testid=c525,
                        // callbackUrl=http://localhost:8089/,
                        // uuid=26b189f7-b075-4013-b487-d938b895c525, userId=jf9860,
                        // normalDurationInSeconds=10}
                "500:", // {additionalDurationInSeconds=10, workflow=NewOne,
                        // domain=ChangeManagement,
                        // callbackData=sdafafadfdasfdsafdsa, testid=8e87,
                        // callbackUrl=http://localhost:8089/,
                        // uuid=4f59b14a-8040-4257-8981-defcb8f38e87, userId=jf9860,
                        // normalDurationInSeconds=10}
        };

        int i = 0;
        for (String template : templates) {
            for (String domain : domains) {
                for (String userId : userIds) {
                    for (String callbackUrl : callbackUrls) {
                        for (String callbackData : callbackDatum) {
                            for (String workflow : workflows) {
                                for (Integer normalDuration : normalDurationInSeeconds) {
                                    for (Integer additionalDuration : additionalDurationInSeeconds) {
                                        Map<String, String> values = new HashMap<String, String>();
                                        String scheduleId = UUID.randomUUID().toString();
                                        values.put("uuid", scheduleId);
                                        values.put("testid", scheduleId.substring(scheduleId.length() - 4));
                                        values.put("domain", domain);
                                        values.put("userId", userId);
                                        values.put("callbackUrl", callbackUrl);
                                        values.put("callbackData", callbackData);
                                        values.put("workflow", workflow);
                                        values.put("normalDurationInSeconds", normalDuration.toString());
                                        values.put("additionalDurationInSeconds", additionalDuration.toString());
                                        String json = JtestHelper.template(template, values);
                                        ObjectMapper om = new ObjectMapper();
                                        CMSMessage scheduleMessage;
                                        try {
                                            scheduleMessage = om.readValue(json, CMSMessage.class);
                                            MockHttpServletRequest mrequest = new MockHttpServletRequest();
                                            mrequest.url.append(scheduleId);

                                            Response response = cMSOServiceImpl.createScheduleRequest("v2", scheduleId,
                                                    scheduleMessage, mrequest.request);

                                            Object result = response.getEntity();
                                            StringBuilder sb = new StringBuilder();
                                            sb.append(response.getStatus()).append(":");
                                            if (result instanceof CMSRequestError) {
                                                String r = result.toString().replaceAll(" : Reason :.*$", "");
                                                sb.append(r.replaceAll(scheduleId, "<uuid>"));
                                            }
                                            // Generate results[] entry
                                            System.out.println("\"" + sb.toString() + "\", //" + values.toString());

                                            // Debug an assertion
                                            System.out.println(results[i] + ":" + sb.toString());
                                            assertEquals(results[i].equals(sb.toString()), true);
                                            i++;

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
