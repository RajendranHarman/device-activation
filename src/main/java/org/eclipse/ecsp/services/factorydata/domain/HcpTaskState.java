/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Timestamp;

/**
 * Represents the state of an HCP task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HcpTaskState {

    private long taskId;
    private String taskType;
    private String status;
    private Timestamp startTime;
    private Timestamp endTime;
    private String taskParams;
    private String result;

    /**
     * Returns the start time of the task.
     *
     * @return The start time of the task.
     */
    public Timestamp getStartTime() {
        return startTime != null ? (Timestamp) startTime.clone() : null;
    }

    /**
     * Sets the start time of the task.
     *
     * @param startTime The start time of the task.
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime != null ? (Timestamp) startTime.clone() : null;
    }

    /**
     * Returns the end time of the task.
     *
     * @return The end time of the task.
     */
    public Timestamp getEndTime() {
        return endTime != null ? (Timestamp) endTime.clone() : null;
    }

    /**
     * Sets the end time of the task.
     *
     * @param endTime The end time of the task.
     */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime != null ? (Timestamp) endTime.clone() : null;
    }

    /**
     * Returns the result of the task.
     *
     * @return The result of the task.
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result of the task.
     *
     * @param result The result of the task.
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Returns the ID of the task.
     *
     * @return The ID of the task.
     */
    public long getTaskId() {
        return taskId;
    }

    /**
     * Sets the ID of the task.
     *
     * @param taskId The ID of the task.
     */
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns the type of the task.
     *
     * @return The type of the task.
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets the type of the task.
     *
     * @param taskType The type of the task.
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * Returns the status of the task.
     *
     * @return The status of the task.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status The status of the task.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the parameters of the task.
     *
     * @return The parameters of the task.
     */
    public String getTaskParams() {
        return taskParams;
    }

    /**
     * Sets the parameters of the task.
     *
     * @param taskParams The parameters of the task.
     */
    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    /**
     * Returns a string representation of the HcpTaskState object.
     *
     * @return A string representation of the HcpTaskState object.
     */
    @Override
    public String toString() {
        return "HcpTaskState [taskId=" + taskId + ", taskType=" + taskType + ", status=" + status
                + ", startTime=" + startTime
                + ", endTime=" + endTime + ", taskParams=" + taskParams + ", result=" + result + "]";
    }
}
