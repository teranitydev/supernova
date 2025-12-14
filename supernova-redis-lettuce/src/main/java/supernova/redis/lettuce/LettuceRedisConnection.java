package supernova.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import supernova.redis.PacketSerializer;
import supernova.redis.RedisConnection;
import supernova.redis.RedisPacket;
import supernova.redis.ResponseCallback;
import supernova.util.Result;
import supernova.util.Violation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LettuceRedisConnection implements RedisConnection {

    private final RedisClient redisClient;

    private final StatefulRedisConnection<String, String> connection;

    private final RedisCommands<String, String> commands;

    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final Map<String, CompletableFuture<RedisPacket>> pendingResponses = new ConcurrentHashMap<>();

    private PacketSerializer packetSerializer;

    public LettuceRedisConnection(String uri) {
        Objects.requireNonNull(uri);

        this.redisClient = RedisClient.create(uri);
        this.connection = redisClient.connect();
        this.commands = connection.sync();
        this.pubSubConnection = redisClient.connectPubSub();

        pubSubConnection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                final CompletableFuture<RedisPacket> future = pendingResponses.remove(channel);
                if (future != null) {
                    final RedisPacket packet = packetSerializer.deserialize(message, RedisPacket.class);
                    future.complete(packet);
                }
            }

            @Override
            public void message(String s, String k1, String s2) {}
            @Override
            public void subscribed(String s, long l) {}
            @Override
            public void psubscribed(String s, long l) {}
            @Override
            public void unsubscribed(String s, long l) {}
            @Override
            public void punsubscribed(String s, long l) {}
        });
    }

    @Override
    public PacketSerializer getPacketSerializer() {
        return packetSerializer;
    }

    @Override
    public void setPacketSerializer(PacketSerializer packetSerializer) {
        this.packetSerializer = packetSerializer;
    }

    @Override
    public Result<Void> publishPacket(RedisPacket redisPacket, long timeout) {
        try {
            commands.publish(redisPacket.getChannelName(), packetSerializer.serialize(redisPacket));
            return Result.success(null);
        } catch (Exception e) {
            return Result.violation(new Violation<>(e));
        }
    }

    public Result<Void> publishPacket(RedisPacket packet, String responseChannel, ResponseCallback response, long timeout) {
        try {
            CompletableFuture<RedisPacket> future = new CompletableFuture<>();
            pendingResponses.put(responseChannel, future);

            pubSubConnection.async().subscribe(responseChannel)
                    .thenRun(() -> commands.publish(packet.getChannelName(), packetSerializer.serialize(packet)));

            future.orTimeout(timeout, TimeUnit.MILLISECONDS)
                    .whenCompleteAsync((resp, ex) -> {
                        pubSubConnection.async().unsubscribe(responseChannel);
                        if (ex != null) {
                            pendingResponses.remove(responseChannel);
                            response.call(null);
                        } else {
                            pendingResponses.remove(responseChannel);
                            response.call(resp);
                        }
                    });

            return Result.success(null);
        } catch (Exception e) {
            return Result.violation(new Violation<>(e));
        }
    }

    @Override
    public Map<String, CompletableFuture<RedisPacket>> getPendingResponses() {
        return Map.copyOf(pendingResponses);
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }
}
