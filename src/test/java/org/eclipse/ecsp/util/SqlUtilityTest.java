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

package org.eclipse.ecsp.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.services.shared.util.SqlUtility;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test class for SqlUtility.
 */
public class SqlUtilityTest {

    private static final String STATE = "PROVISIONED";
    private static final String IMEI = "123";
    private static final String SERIAL_NUMBER = "009";

    @Test
    public void getPreparedSqlNullCheck() {
        assertNull(SqlUtility.getPreparedSql(null, null, null));
    }

    @Test
    public void getPreparedSqlNullPrefix() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql(null, operator, orderedMap));
    }

    @Test
    public void getPreparedSqlNullOperator() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, null, orderedMap));
    }

    @Test
    public void getPreparedSqlNullOrderedMap() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql(prefix, operator, null));
    }

    @Test
    public void getPreparedSqlPrefixTest1() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, null, null));
    }

    @Test
    public void getPreparedSqlOperatorTest1() {
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql(null, operator, null));
    }

    @Test
    public void getPreparedSqlOrderedMapTest1() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String prefix1 = null;
        String opreator1 = null;
        assertNull(SqlUtility.getPreparedSql(prefix1, opreator1, orderedMap));
    }

    @Test
    public void getPreparedSqlEmptyCheck() {
        String prefix = "";
        String operator = "";
        assertNull(SqlUtility.getPreparedSql(prefix, operator, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlEmptyPrefix() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql("", operator, orderedMap));
    }

    @Test
    public void getPreparedSqlEmptyOperator() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, "", orderedMap));
    }

    @Test
    public void getPreparedSqlEmptyOrderedMap() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql(prefix, operator, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlPrefixTest2() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, "", Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlOperatorTest2() {
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql("", operator, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlOrderedMapTest2() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        assertNull(SqlUtility.getPreparedSql(StringUtils.EMPTY, StringUtils.EMPTY, orderedMap));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck1() {
        assertNull(SqlUtility.getPreparedSql("", null, null));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck2() {
        assertNull(SqlUtility.getPreparedSql(null, "", null));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck3() {
        assertNull(SqlUtility.getPreparedSql(null, null, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck4() {
        assertNull(SqlUtility.getPreparedSql("", "", null));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck5() {
        assertNull(SqlUtility.getPreparedSql("", null, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlEmptyNullCheck6() {
        assertNull(SqlUtility.getPreparedSql(null, "", Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlPrefixTest3() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, null, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlPrefixTest4() {
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        assertNull(SqlUtility.getPreparedSql(prefix, "", null));
    }

    @Test
    public void getPreparedSqlOperatorTest3() {
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql(null, operator, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSqlOperatorTest4() {
        String operator = " and ";
        assertNull(SqlUtility.getPreparedSql("", operator, null));
    }

    @Test
    public void getPreparedSqlOrderedMapTest3() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        assertNull(SqlUtility.getPreparedSql(null, "", orderedMap));
    }

    @Test
    public void getPreparedSqlOrderedMapTest4() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        assertNull(SqlUtility.getPreparedSql("", null, orderedMap));
    }

    @Test
    public void getPreparedSql() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        String operator = " and ";
        assertNotNull(SqlUtility.getPreparedSql(prefix, operator, orderedMap));
    }

    @Test
    public void getArrayValuesMapNullCheck() {
        assertNotNull(SqlUtility.getArrayValues((Map<String, Object>) null));
    }

    @Test
    public void getArrayValuesListNullCheck() {
        assertNotNull(SqlUtility.getArrayValues((List<Map<String, Object>>) null));
    }

    @Test
    public void getArrayValuesMapEmptyCheck() {
        assertNotNull(SqlUtility.getArrayValues(Collections.emptyMap()));
    }

    @Test
    public void getArrayValuesListEmptyCheck() {
        assertNotNull(SqlUtility.getArrayValues(Collections.emptyList()));
    }

    @Test
    public void getArrayValuesMap() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        assertNotNull(SqlUtility.getArrayValues(orderedMap));
    }

    @Test
    public void getArrayValuesList() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        List<Map<String, Object>> orderedMapList = new ArrayList<>();
        orderedMapList.add(orderedMap);
        assertNotNull(SqlUtility.getArrayValues(orderedMapList));
    }

    @Test
    public void prepareLikeQueryNullCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(null, null));
    }

    @Test
    public void prepareLikeQueryEmptyCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void prepareLikeQueryTest1() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(null, Collections.emptyList()));
    }

    @Test
    public void prepareLikeQueryTest2() {
        List<String> containsLikeValueList = SqlUtility.getList("1,2", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(null, containsLikeValueList));
    }

    @Test
    public void prepareLikeQueryTest3() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(Collections.emptyList(), null));
    }

    @Test
    public void prepareLikeQueryTest4() {
        List<String> containsLikeValueList = SqlUtility.getList("1,2", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(Collections.emptyList(), containsLikeValueList));
    }

    @Test
    public void prepareLikeQueryTest5() {
        List<String> containsLikeFieldList = SqlUtility.getList("a", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(containsLikeFieldList, null));
    }

    @Test
    public void prepareLikeQueryTest6() {
        List<String> containsLikeFieldList = SqlUtility.getList("a", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(containsLikeFieldList, Collections.emptyList()));
    }

    @Test
    public void prepareLikeQueryNotSameListSize() {
        List<String> containsLikeFieldList = SqlUtility.getList("a", CommonConstants.COMMA);
        List<String> containsLikeValueList = SqlUtility.getList("1,2", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList));
    }

    @Test
    public void prepareLikeQuery() {
        List<String> containsLikeFieldList = SqlUtility.getList("a,b", CommonConstants.COMMA);
        List<String> containsLikeValueList = SqlUtility.getList("1,2", CommonConstants.COMMA);
        assertNotNull(SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList));
    }

    @Test
    public void prepareRangeQueryNullCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(null, null));
    }

    @Test
    public void prepareRangeQueryEmptyCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void prepareRangeQueryTest1() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(null, Collections.emptyList()));
    }

    @Test
    public void prepareRangeQueryTest2() {
        List<String> rangeValueList = SqlUtility.getList("1_1", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(null, rangeValueList));
    }

    @Test
    public void prepareRangeQueryTest3() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(Collections.emptyList(), null));
    }

    @Test
    public void prepareRangeQueryTest4() {
        List<String> rangeValueList = SqlUtility.getList("1_1", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(Collections.emptyList(), rangeValueList));
    }

    @Test
    public void prepareRangeQueryTest5() {
        List<String> rangeFieldList = SqlUtility.getList("a_b,c_d", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(rangeFieldList, null));
    }

    @Test
    public void prepareRangeQueryTest6() {
        List<String> rangeFieldList = SqlUtility.getList("a_b,c_d", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(rangeFieldList, Collections.emptyList()));
    }

    @Test
    public void prepareRangeQueryNotSameListSize() {
        List<String> rangeFieldList = SqlUtility.getList("a_b,c_d", CommonConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_1", CommonConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList));
    }

    @Test(expected = NullPointerException.class)
    public void prepareRangeQueryOutOfRange() {
        List<String> rangeFieldList = SqlUtility.getList("a_b", CommonConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_1_1", CommonConstants.COMMA);
        SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);
    }

    @Test
    public void prepareRangeQuery() {
        List<String> rangeFieldList = SqlUtility.getList("a_b", CommonConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_2", CommonConstants.COMMA);
        assertNotNull(SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList));
    }

    @Test
    public void prepareOrderByQueryNull() {
        assertNotNull(SqlUtility.prepareOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareOrderByQueryEmptySortingOrder() {
        assertNotNull(SqlUtility.prepareOrderByQuery(StringUtils.EMPTY, "imei"));
    }

    @Test
    public void prepareOrderByQueryEmptySortByAsc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("asc", StringUtils.EMPTY));
    }

    @Test
    public void prepareOrderByQueryEmptySortByDesc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("desc", StringUtils.EMPTY));
    }

    @Test
    public void prepareOrderByQuerySortByAsc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("asc", "imei"));
    }

    @Test
    public void prepareOrderByQuerySortByDesc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("desc", "imei"));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmpty() {
        assertEquals(StringUtils.EMPTY, StringUtils.EMPTY,
            SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmptySortBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, "asc"));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmptyOrderBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", StringUtils.EMPTY));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByAsc() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "asc"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDesc() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "desc"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "order"));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmptyInput() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareSortByAndOrderByQuery_EmptySortBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, "asc", "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuery_EmptyOrderBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", StringUtils.EMPTY, "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByAscOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "asc", "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDescOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "desc", "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDummyOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "order", "table"));
    }

    @Test
    public void getList() {
        assertEquals(Collections.EMPTY_LIST, SqlUtility.getList(StringUtils.EMPTY, CommonConstants.COMMA));
    }
}