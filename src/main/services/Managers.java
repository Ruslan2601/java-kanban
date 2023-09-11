package main.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import main.interfaces.HistoryManager;
import main.interfaces.TaskManager;

import java.io.IOException;
import java.time.Instant;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(final JsonWriter jsonWriter, final Instant instant) throws IOException {
                if (instant != null) {
                    jsonWriter.value(instant.toEpochMilli());
                } else {
                    jsonWriter.nullValue();
                }
            }

            @Override
            public Instant read(final JsonReader jsonReader) throws IOException {
                long millisEpoch = jsonReader.nextLong();
                if (millisEpoch == -1) {
                    return null;
                } else {
                    return Instant.ofEpochMilli(millisEpoch);
                }
            }
        });
        return gsonBuilder.create();
    }
}
