package supernova.redis;

import supernova.util.Result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a Redis connection wrapper for publishing, listening and subscribing to
 * Object type {@link RedisPacket}.
 *
 * @author Izhar Atharzi
 * @since 0.0.3
 */
public interface RedisConnection {

    /**
     * Returns the {@link PacketSerializer} used for serializing and deserialize {@link RedisPacket}.
     */
    PacketSerializer getPacketSerializer();

    Map<String, CompletableFuture<RedisPacket>> getPendingResponses();

    Result<Void> publishPacket(RedisPacket redisPacket, long timeout);

    Result<Void> publishPacket(RedisPacket redisPacket, ResponseCallback response, long timeout);

    <T extends RedisPacket> void registerHandler(Class<T> packetClass, PacketHandler handler);

    void subscribe(String channelName);

    void unsubscribe(String channelName);
}
