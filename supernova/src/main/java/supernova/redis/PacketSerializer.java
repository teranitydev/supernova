package supernova.redis;

/**
 * Simple Redis Packet Serializer interface.
 */
public interface PacketSerializer {

    <T> String serialize(T value);

    <T> T deserialize(String str, Class<T> type);
}
