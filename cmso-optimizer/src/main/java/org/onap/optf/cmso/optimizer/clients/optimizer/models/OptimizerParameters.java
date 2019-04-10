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

import java.util.ArrayList;
import java.util.List;


/**
 * The Class OptimizerParameters.
 */
/*
 * numElements = 5;
maxTime = 5;
numLoaders = 1;
noConflict = [| true , true , true , true , true
 | true , true , true , true , true
 | false , true , false , true , false
 | false , false , false , false , false
 | true , false , true , false , true
 |];
slotCapacity = [5, 5, 5, 5, 5];
loaderCapacity = [|
5, 5, 5, 5, 5
|];


numAttributes = 0;
attributesRange = [];
attributes = [];
attributeConcurrencyLimit = [];
 */
public class OptimizerParameters {
    private Long numElements;
    private Long numLoaders;
    private List<Long> elementSlotCapacity = new ArrayList<>();
    private Long maxTime;
    private List<List<Boolean>> noConflict = new ArrayList<>();
    private List<List<Long>> loaderCapacity = new ArrayList<>();

    private Long numAttributes;
    private List<Long> attributesRange = new ArrayList<>();
    private List<List<Long>> attributes = new ArrayList<>();
    private List<List<Long>> attributeConcurrencyLimit = new ArrayList<>();

    /**
     * Gets the num elements.
     *
     * @return the num elements
     */
    public Long getNumElements() {
        return numElements;
    }

    /**
     * Sets the num elements.
     *
     * @param numElements the new num elements
     */
    public void setNumElements(Long numElements) {
        this.numElements = numElements;
    }

    /**
     * Gets the max time.
     *
     * @return the max time
     */
    public Long getMaxTime() {
        return maxTime;
    }

    /**
     * Sets the max time.
     *
     * @param maxTime the new max time
     */
    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Gets the num loaders.
     *
     * @return the num loaders
     */
    public Long getNumLoaders() {
        return numLoaders;
    }

    /**
     * Sets the num loaders.
     *
     * @param numLoaders the new num loaders
     */
    public void setNumLoaders(Long numLoaders) {
        this.numLoaders = numLoaders;
    }

    /**
     * Gets the no conflict.
     *
     * @return the no conflict
     */
    public List<List<Boolean>> getNoConflict() {
        return noConflict;
    }

    /**
     * Sets the no conflict.
     *
     * @param noConflict the new no conflict
     */
    public void setNoConflict(List<List<Boolean>> noConflict) {
        this.noConflict = noConflict;
    }

    /**
     * Gets the element slot capacity.
     *
     * @return the element slot capacity
     */
    public List<Long> getElementSlotCapacity() {
        return elementSlotCapacity;
    }

    /**
     * Sets the element slot capacity.
     *
     * @param slotCapacity the new element slot capacity
     */
    public void setElementSlotCapacity(List<Long> slotCapacity) {
        this.elementSlotCapacity = slotCapacity;
    }

    /**
     * Gets the loader capacity.
     *
     * @return the loader capacity
     */
    public List<List<Long>> getLoaderCapacity() {
        return loaderCapacity;
    }

    /**
     * Sets the loader capacity.
     *
     * @param loaderCapacity the new loader capacity
     */
    public void setLoaderCapacity(List<List<Long>> loaderCapacity) {
        this.loaderCapacity = loaderCapacity;
    }

    /**
     * Gets the num attributes.
     *
     * @return the num attributes
     */
    public Long getNumAttributes() {
        return numAttributes;
    }

    /**
     * Sets the num attributes.
     *
     * @param numAttributes the new num attributes
     */
    public void setNumAttributes(Long numAttributes) {
        this.numAttributes = numAttributes;
    }

    /**
     * Gets the attributes range.
     *
     * @return the attributes range
     */
    public List<Long> getAttributesRange() {
        return attributesRange;
    }

    /**
     * Sets the attributes range.
     *
     * @param attributesRange the new attributes range
     */
    public void setAttributesRange(List<Long> attributesRange) {
        this.attributesRange = attributesRange;
    }

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    public List<List<Long>> getAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes.
     *
     * @param attributes the new attributes
     */
    public void setAttributes(List<List<Long>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the attribute concurrency limit.
     *
     * @return the attribute concurrency limit
     */
    public List<List<Long>> getAttributeConcurrencyLimit() {
        return attributeConcurrencyLimit;
    }

    /**
     * Sets the attribute concurrency limit.
     *
     * @param attributeConcurrencyLimit the new attribute concurrency limit
     */
    public void setAttributeConcurrencyLimit(List<List<Long>> attributeConcurrencyLimit) {
        this.attributeConcurrencyLimit = attributeConcurrencyLimit;
    }



    /**
     * To mini zinc.
     *
     * @return the string
     */
    public String toMiniZinc() {
        StringBuilder sb = new StringBuilder();
        appendAttribute(sb, "numElements", numElements.toString());
        appendAttribute(sb, "maxTime", maxTime.toString());
        appendAttribute(sb, "numLoaders", numLoaders.toString());
        appendAttribute(sb, "numAttributes", numAttributes.toString());

        appendAttribute(sb, "noConflict", "[|\n" + formatBooleanRows(noConflict) + "|]");
        appendAttribute(sb, "elementSlotCapacity", "[" + formatLongList(elementSlotCapacity) + "]");
        appendAttribute(sb, "loaderCapacity", "[|\n" + formatLongRows(loaderCapacity) + "|]");


        if (attributesRange.size() > 0) {
            appendAttribute(sb, "attributesRange", "[" + formatLongList(attributesRange) + "]");
        } else {
            appendAttribute(sb, "attributesRange", "[]");
        }
        if (attributes.size() > 0) {
            appendAttribute(sb, "attributes", "[|\n" + formatLongRows(attributes) + "|]");
        } else {
            appendAttribute(sb, "attributes", "array2d(1..numElements, 1..numAttributes, [])");
        }
        if (attributeConcurrencyLimit.size() > 0) {
            appendAttribute(sb, "attributeConcurrencyLimit", "[|\n" + formatLongRows(attributeConcurrencyLimit) + "|]");
        } else {
            appendAttribute(sb, "attributeConcurrencyLimit", "array2d(1..numAttributes, 1..maxTime, [])");
        }
        return sb.toString();
    }

    private void appendAttribute(StringBuilder sb, String name, String value) {
        sb.append(name).append(" = ").append(value).append(";\n");
    }

    // Methods to dump minizinc parameters. THese may be very large
    //
    private String formatBooleanRows(List<List<Boolean>> list) {
        StringBuilder sb = new StringBuilder();
        String row = "";
        for (List<Boolean> objectList : list) {
            sb.append(row).append(formatBooleanList(objectList));
            row = "| ";
        }
        sb.append("\n");
        return sb.toString();
    }

    private String formatBooleanList(List<Boolean> list) {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (Object object : list) {
            sb.append(comma).append(object.toString());
            comma = ", ";
        }
        return sb.toString();
    }

    private String formatLongRows(List<List<Long>> list) {
        StringBuilder sb = new StringBuilder();
        String row = "";
        for (List<Long> objectList : list) {
            sb.append(row).append(formatLongList(objectList));
            row = "| ";
        }
        sb.append("\n");
        return sb.toString();
    }

    private String formatLongList(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (Object object : list) {
            sb.append(comma).append(object.toString());
            comma = ", ";
        }
        return sb.toString();
    }

}


