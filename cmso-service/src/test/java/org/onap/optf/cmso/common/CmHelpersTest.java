/*-
 * ============LICENSE_START============================================
 * OPTF-CMSO
 * =====================================================================
 * Copyright (c) 2019 IBM.
 * =====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 * 
 */

package org.onap.optf.cmso.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.ElementData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.service.rs.models.CmDomainDataEnum;

public class CmHelpersTest {

    CmHelpers cmHelpers;

    @Before
    public void setUp() {
        cmHelpers = new CmHelpers();
    }

    @Test
    public void testGetDomainData() {

        DomainData dmdata = new DomainData();
        dmdata.setName(CmDomainDataEnum.WorkflowName.toString());
        dmdata.setValue("TestValue");
        List<DomainData> listOfDomainData = new ArrayList<>();
        listOfDomainData.add(dmdata);

        Schedule schedule = new Schedule();
        schedule.setDomainData(listOfDomainData);

        String domainData = cmHelpers.getDomainData(schedule, CmDomainDataEnum.WorkflowName);
        String eventData = cmHelpers.getEventData(schedule, CmDomainDataEnum.WorkflowName);

        assertEquals("TestValue", domainData);
        assertEquals("TestValue", eventData);

    }

    @Test
    public void testGetDomainDataAsNull() {

        DomainData dmdata = new DomainData();
        dmdata.setName("TestKey");
        dmdata.setValue("TestValue");
        List<DomainData> listOfDomainData = new ArrayList<>();
        listOfDomainData.add(dmdata);

        Schedule schedule = new Schedule();
        schedule.setDomainData(listOfDomainData);

        String domainData = cmHelpers.getDomainData(schedule, CmDomainDataEnum.WorkflowName);
        assertNull(domainData);

    }

    @Test
    public void testGetElementData() {

        ElementData elementData = new ElementData();
        elementData.setName(CmDomainDataEnum.WorkflowName.toString());
        elementData.setValue("TestValue");
        List<ElementData> listOfElementData = new ArrayList<>();
        listOfElementData.add(elementData);

        String elementDataVal = cmHelpers.getElementData(listOfElementData, CmDomainDataEnum.WorkflowName);
        assertEquals("TestValue", elementDataVal);

    }

    @Test
    public void testGetElementDataAsNull() {

        ElementData elementData = new ElementData();
        elementData.setName("Test");
        elementData.setValue("TestValue");
        List<ElementData> listOfElementData = new ArrayList<>();
        listOfElementData.add(elementData);

        String elementDataVal = cmHelpers.getElementData(listOfElementData, CmDomainDataEnum.WorkflowName);
        assertNull(elementDataVal);

    }

}
