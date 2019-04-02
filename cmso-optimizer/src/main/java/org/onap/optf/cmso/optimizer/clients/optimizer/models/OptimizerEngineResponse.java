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

public class OptimizerEngineResponse
{

    public enum OptimizerEngineResponseStatus {
        IN_PROGRESS, COMPLETED, FAILED, IN_QUEUE,
    }

    private String requestId;
    private OptimizerResults optimizerResults;
    private OptimizerEngineResponseStatus status;
    private Integer pollingSeconds;
    private String errorMessage;
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public OptimizerResults getOptimizerResults() {
        return optimizerResults;
    }
    public void setOptimizerResults(OptimizerResults oprimizerResults) {
        this.optimizerResults = oprimizerResults;
    }
    public OptimizerEngineResponseStatus getStatus() {
        return status;
    }
    public void setStatus(OptimizerEngineResponseStatus status) {
        this.status = status;
    }
    public Integer getPollingSeconds() {
        return pollingSeconds;
    }
    public void setPollingSeconds(Integer pollingSeconds) {
        this.pollingSeconds = pollingSeconds;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
