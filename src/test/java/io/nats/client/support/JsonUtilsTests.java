// Copyright 2015-2018 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.client.support;

import io.nats.client.impl.DateTimeUtils;
import io.nats.client.impl.JsonUtils;
import io.nats.client.utils.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class JsonUtilsTests {

    @Test
    public void testParseStringArray() {
        String[] a = JsonUtils.getStringArray("fieldName", "...\"fieldName\": [\n      ],...");
        assertNotNull(a);
        assertEquals(0, a.length);

        a = JsonUtils.getStringArray("fieldName", "...\"fieldName\": [\n      \"value1\"\n    ],...");
        assertNotNull(a);
        assertEquals(1, a.length);
        assertEquals("value1", a[0]);

        a = JsonUtils.getStringArray("fieldName", "...\"fieldName\": [\n      \"value1\",\n      \"value2\"\n    ],...");
        assertNotNull(a);
        assertEquals(2, a.length);
        assertEquals("value1", a[0]);
        assertEquals("value2", a[1]);
    }

    @Test
    public void testGetJSONObject() {
        // object is there
        String json = "{\"object\": {\"field\": \"val\"}, \"other\":{}}";
        String object = JsonUtils.getJSONObject("object", json);
        assertEquals("{\"field\": \"val\"}", object);

        // object isn't
        json = "{\"other\":{}}";
        object = JsonUtils.getJSONObject("object", json);
        assertEquals(JsonUtils.EMPTY_JSON, object);

        // object there but incomplete
        json = "{\"object\": {\"field\": \"val\"";
        object = JsonUtils.getJSONObject("object", json);
        assertEquals(JsonUtils.EMPTY_JSON, object);
    }

    @Test
    public void testGetObjectArray() {
        String json = ResourceUtils.dataAsString("ConsumerListResponse.json");
        List<String> list = JsonUtils.getObjectArray("consumers", json);
        assertEquals(2, list.size());
    }

    @Test
    public void testBeginEnd() {
        StringBuilder sb = JsonUtils.beginJson();
        JsonUtils.addFld(sb, "name", "value");
        JsonUtils.endJson(sb);
        assertEquals("{\"name\":\"value\"}", sb.toString());

        sb = JsonUtils.beginFormattedJson();
        JsonUtils.addFld(sb, "name", "value");
        JsonUtils.endFormattedJson(sb);
        assertEquals("{\n    \"name\":\"value\"\n}", sb.toString());
    }

    @Test
    public void testAddFlds() {
        StringBuilder sb = new StringBuilder();

        JsonUtils.addFld(sb, "n/a", (String)null);
        assertEquals(0, sb.length());

        JsonUtils.addFld(sb, "n/a", "");
        assertEquals(0, sb.length());

        JsonUtils.addFld(sb, "n/a", (String[])null);
        assertEquals(0, sb.length());

        JsonUtils.addFld(sb, "n/a", new String[0]);
        assertEquals(0, sb.length());

        JsonUtils.addFld(sb, "n/a", (List<String>)null);
        assertEquals(0, sb.length());

        JsonUtils.addFld(sb, "n/a", new ArrayList<>());
        assertEquals(0, sb.length());

        JsonUtils.addFldWhenTrue(sb, "n/a", null);
        assertEquals(0, sb.length());

        JsonUtils.addFldWhenTrue(sb, "n/a", false);
        assertEquals(0, sb.length());

        sb = new StringBuilder();
        JsonUtils.addFld(sb, "foo", new String[]{"bar"});
        assertEquals(14, sb.length());
    }

    @Test
    public void testParseDateTime() {
        assertEquals(1611186068, DateTimeUtils.parseDateTime("2021-01-20T23:41:08.579594Z").toEpochSecond());
        assertEquals(1612293508, DateTimeUtils.parseDateTime("2021-02-02T11:18:28.347722551-08:00").toEpochSecond());
        assertEquals(-62135596800L, DateTimeUtils.parseDateTime("anything-not-valid").toEpochSecond());
    }
}
