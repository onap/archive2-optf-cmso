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

/**
 * The Class ElementSlot.
 */
/*
     1,0,1
     2,0,1
     3,0,1
     4,0,1
     5,0,1
 */
public class ElementSlot {
    private Integer elementIndex = 0;
    private Integer slot = 0;
    private Integer loader = 0;
    private Long time = 0L;

    public Integer getElementIndex() {
        return elementIndex;
    }

    public void setElementIndex(Integer elementIndex) {
        this.elementIndex = elementIndex;
    }

    /**
     * Gets the slot.
     *
     * @return the slot
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     * Sets the slot.
     *
     * @param slot the new slot
     */
    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    /**
     * Gets the loader.
     *
     * @return the loader
     */
    public Integer getLoader() {
        return loader;
    }

    /**
     * Sets the loader.
     *
     * @param loader the new loader
     */
    public void setLoader(Integer loader) {
        this.loader = loader;
    }

    /**
     * Instantiates a new element slot.
     *
     * @param cols the values
     */
    public ElementSlot(String[] cols) {
        if (cols.length > 0) {
            elementIndex = Integer.valueOf(cols[0]);
        }
        if (cols.length > 1) {
            slot = Integer.valueOf(cols[1]);
        }
        if (cols.length > 2) {
            loader = Integer.valueOf(cols[2]);
        }
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}

