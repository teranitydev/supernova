package supernova.redis;

/**
 * Simple Redis Packet Serializer interface.
 *
 * @author Izhar Atharzi
 * @since 0.0.3
 */
public interface PacketSerializer {

    <T> String serialize(T value);

    <T> T deserialize(String str, Class<T> type);
}
