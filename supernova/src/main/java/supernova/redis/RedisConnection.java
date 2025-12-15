package supernova.redis;

import supernova.util.Result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RedisConnection {

    PacketSerializer getPacketSerializer();

    Map<String, CompletableFuture<RedisPacket>> getPendingResponses();

    Result<Void> publishPacket(RedisPacket redisPacket, long timeout);

    Result<Void> publishPacket(RedisPacket redisPacket, ResponseCallback response, long timeout);

    <T extends RedisPacket> void registerHandler(Class<T> packetClass, PacketHandler handler);

    void subscribe(String channelName);

    void unsubscribe(String channelName);
}
