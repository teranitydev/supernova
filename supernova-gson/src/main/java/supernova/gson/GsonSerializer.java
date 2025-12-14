package supernova.gson;

import com.google.gson.Gson;
import supernova.redis.PacketSerializer;

/**
 * Gson (JSON) Redis Packet Serializer.
 */
public class GsonSerializer implements PacketSerializer {

    private final Gson GSON = new Gson();

    @Override
    public <T> String serialize(T value) {
        return GSON.toJson(value);
    }

    @Override
    public <T> T deserialize(String str, Class<T> type) {
        return GSON.fromJson(str, type);
    }
}
