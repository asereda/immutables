package org.immutables.mongo.repository.internal;

import com.google.common.collect.ImmutableList;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.DBObject;
import org.bson.io.BasicOutputBuffer;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InsertObjectListTest {

    <T> List<T> forEachLoop(List<T> elements) throws IOException {
        SimpleTypeAdapter<T> adapter = new SimpleTypeAdapter<>();
        BsonEncoding.InsertObjectList<T> list = new BsonEncoding.InsertObjectList<T>(ImmutableList.copyOf(elements), adapter);

        BasicOutputBuffer buffer = new BasicOutputBuffer();


        for (DBObject ignored: list) {
            list.writeCurrent(buffer);
        }

        return adapter.writes();
    }

    <T> List<T> indexLoop(List<T> elements) throws IOException {

        SimpleTypeAdapter<T> adapter = new SimpleTypeAdapter<>();
        BsonEncoding.InsertObjectList<T> list = new BsonEncoding.InsertObjectList<T>(ImmutableList.copyOf(elements), adapter);

        BasicOutputBuffer buffer = new BasicOutputBuffer();

        for (int i = 0; i < list.size(); i++) {
            DBObject ignored = list.get(i);
            list.writeCurrent(buffer);
        }

        return adapter.writes();
    }



    @Test
    public void forEach_test() throws Exception {
        assertEquals(forEachLoop(Collections.<String>emptyList()), Collections.<String>emptyList());
        assertEquals(forEachLoop(Collections.singletonList("foo")), Collections.singletonList("foo"));
        assertEquals(forEachLoop(Arrays.asList("foo", "bar")), Arrays.asList("foo", "bar"));
    }

    @Test
    public void indexLoop_test() throws Exception {
        assertEquals(indexLoop(Collections.<String>emptyList()), Collections.<String>emptyList());
        assertEquals(indexLoop(Collections.singletonList("foo")), Collections.singletonList("foo"));
        assertEquals(indexLoop(Arrays.asList("foo", "bar")), Arrays.asList("foo", "bar"));
    }


    private static class SimpleTypeAdapter<T> extends TypeAdapter<T> {

        private final List<T> writes;

        SimpleTypeAdapter() {
            this.writes = new ArrayList<>();
        }

        List<T> writes() {
            return Collections.unmodifiableList(writes);
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            writes.add(value);

        }

        @Override
        public T read(JsonReader in) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

}