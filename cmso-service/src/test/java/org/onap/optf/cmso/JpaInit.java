/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
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

package org.onap.optf.cmso;

import java.util.concurrent.atomic.AtomicBoolean;
import org.onap.optf.cmso.model.ApprovalType;
import org.onap.optf.cmso.model.Domain;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class JpaInit {

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Initialize the entity manager
     * @param entityManager
     */
    public static void init(TestEntityManager entityManager) {
        if (initialized.compareAndSet(true, true))
        {
            return;
        }
        Domain d = new Domain();
        d.setDomain("ChangeManagement");
        entityManager.persist(d);
        ApprovalType at = new ApprovalType();
        at.setApprovalCount(1);
        at.setDomain("ChangeManagement");
        at.setApprovalType("Tier 2");
        entityManager.persist(at);
        entityManager.flush();

    }
}
