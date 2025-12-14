package supernova.redis;

import supernova.util.Result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RedisConnection {

    PacketSerializer getPacketSerializer();

    void setPacketSerializer(PacketSerializer packetSerializer);

    Result<Void> publishPacket(RedisPacket redisPacket, long timeout);

    Result<Void> publishPacket(RedisPacket redisPacket, String responseChannel, ResponseCallback response, long timeout);

    Map<String, CompletableFuture<RedisPacket>> getPendingResponses();
}
