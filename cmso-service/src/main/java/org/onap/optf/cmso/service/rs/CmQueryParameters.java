/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed under the Creative
 * Commons License, Attribution 4.0 Intl. (the "License"); you may not use this documentation except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.service.rs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.model.DomainData;

/**
 * The Class CmQueryParameters.
 */
public class CmQueryParameters {

    /**
     * The Enum QueryColumns.
     */
    public enum QueryColumns {
        RequestScheduleId("request.scheduleId", String.class, "ss.schedule_id"),
        RequestScheduleName("request.scheduleName", String.class, "ss.schedule_name"),
        RequestUserId("request.userId", String.class, "ss.user_id"),
        RequestStatus("request.status", String.class, "ss.status"),
        RequestCreateDateTime("request.createDateTime", Date.class, "ss.create_date_time"),
        RequestOptimizerStatus("request.optimizerStatus", String.class, "ss.optimizer_status"),
        RequestApprovalUserId("request.approvalUserId", String.class, "sa.user_id"),
        RequestApprovalStatus("request.approvalStatus", String.class, "sa.status"),
        RequestApprovalType("request.approvalType", String.class, "at.approval_type"),
        WorkflowName("WorkflowName", DomainData.class, "dd.value"),
        vnfName("vnfName", String.class, "s.vnf_name"),
        vnfId("vnfId", String.class, "s.vnf_id"),
        vnfStatus("vnfStatus", String.class, "s.vnf_status"),
        // vnfScheduleId("vnfScheduleId", String.class, "s.id"),
        startTime("startTime", Date.class, "s.start_time"),
        finishTime("finishTime", Date.class, "s.finish_ime"),
        lastInstanceTime("lastInstanceTime", Date.class, "g.last_instance_time"),
        tmChangeId("tmChangeId", String.class, "s.tm_change_id"),
        concurrenyLimit("request.concurrencyLimit", Integer.class, "g.concurrency_limit"),
        // approvalUserId("approvalUserId", String.class, "approvalUserId"),
        // approvalStatus("approvalStatus", String.class, "approvalStatus"),
        // approvalType("approvalType", String.class, "approvalType"),
        ;

        private final String urlName;
        private final Class<?> type;
        private final String col;

        private QueryColumns(String urlName, Class<?> type, String col) {
            this.urlName = urlName;
            this.type    = type;
            this.col     = col;
        }

    }

    /**
     * Gets the query column.
     *
     * @param urlName the url name
     * @return the query column
     */
    public static QueryColumns getQueryColumn(String urlName) {
        for (QueryColumns qc : QueryColumns.values()) {
            if (qc.urlName.equals(urlName)) {
                return qc;
            }
        }
        return null;
    }

    /**
     * Builds the clause.
     *
     * @param urlName the url name
     * @param values the values
     * @return the string
     * @throws CmsoException the CMS exception
     */
    public static String buildClause(String urlName, List<String> values) throws CmsoException {
        QueryColumns qc = getQueryColumn(urlName);
        if (qc == null) {
            throw new CmsoException(Status.BAD_REQUEST, LogMessages.UNDEFINED_FILTER_ATTRIBUTE, urlName);
        }
        if (qc.type == Date.class) {
            return formatDate(urlName, values, qc);
        }
        if (qc.type == DomainData.class) {
            return formatDomainData(urlName, values, qc);
        }
        return formatString(urlName, values, qc);
    }

    private static String formatString(String urlName, List<String> values, QueryColumns qc) {
        StringBuilder clause = new StringBuilder();
        List<String> likes = new ArrayList<>();
        List<String> in = new ArrayList<>();
        for (String value : values) {
            if (value.contains("%")) {
                likes.add(value);
            }
            else {
                in.add(value);
            }
        }
        String delim = "(";
        if (in.size() > 0) {
            clause.append(delim).append(qc.col).append(" in ('");
            String inDelim = "";
            for (String value : in) {
                clause.append(inDelim).append(value).append("'");
                inDelim = ", '";
            }
            clause.append(") ");
            delim = " OR ";
        }
        if (likes.size() > 0) {
            for (String value : likes) {
                clause.append(delim).append(qc.col).append(" like '").append(value).append("'");
                delim = " OR ";
            }
        }
        if (!delim.equals("(")) {
            clause.append(")");
        }
        return clause.toString();
    }

    private static String formatDomainData(String urlName, List<String> values, QueryColumns qc) {
        StringBuilder clause = new StringBuilder();
        String delim = "(";
        if (values.size() > 0) {
            for (String value : values) {
                clause.append(delim).append(" (dd.name = '").append(qc.urlName).append("' AND dd.value = '")
                                .append(value).append("')");
                delim = " OR ";
            }
        }
        if (!delim.equals("(")) {
            clause.append(")");
        }
        return clause.toString();
    }

    private static String formatDate(String urlName, List<String> values, QueryColumns qc) throws CmsoException {
        List<String> clauses = new ArrayList<>();
        for (String value : values) {
            String[] dates = value.split(",");
            switch (dates.length) {
                case 2:
                    formatDatePair(qc, dates[0].trim(), dates[1].trim(), clauses);
                    break;
                case 1:
                    formatDatePair(qc, dates[0].trim(), "", clauses);
                    break;
                default:
                    throw new CmsoException(Status.BAD_REQUEST, LogMessages.INVALID_DATE_FILTER, urlName, value);
            }
        }
        StringBuilder clause = new StringBuilder();
        String delim = "(";
        for (String c : clauses) {
            clause.append(delim).append(c);
            delim = " OR ";
        }
        if (!delim.equals(")")) {
            clause.append(")");
        }
        return clause.toString();
    }

    private static void formatDatePair(QueryColumns qc, String lowDate, String highDate, List<String> clauses)
                    throws CmsoException {
        StringBuilder clause = new StringBuilder();
        DateTime date1 = null;
        DateTime date2 = null;
        if (!lowDate.equals("")) {
            date1 = convertDate(lowDate, qc.urlName);
        }
        if (!highDate.equals("")) {
            date2 = convertDate(highDate, qc.urlName);
        }
        String delim = "(";
        if (date1 != null) {
            clause.append(delim).append(qc.col).append(" >= ").append(date1.getMillis());
            delim = " AND ";
        }
        if (date2 != null) {
            clause.append(delim).append(qc.col).append(" <= ").append(date2.getMillis());
            delim = " AND ";
        }
        if (!delim.equals(")")) {
            clause.append(")\n");
            clauses.add(clause.toString());
        }
    }

    private static DateTime convertDate(String utcDate, String urlName) throws CmsoException {
        try {
            return ISODateTimeFormat.dateTimeParser().parseDateTime(utcDate);
        }
        catch (Exception e) {
            throw new CmsoException(Status.BAD_REQUEST, LogMessages.INVALID_DATE_FILTER, urlName, utcDate);
        }
    }

    
}
