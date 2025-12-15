package supernova.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import supernova.redis.*;
import supernova.util.Result;
import supernova.util.Violation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LettuceRedisConnection implements RedisConnection, AutoCloseable {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> commands;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final Map<String, CompletableFuture<RedisPacket>> pendingResponses = new ConcurrentHashMap<>();
    private final Map<Class<? extends RedisPacket>, PacketHandler> HANDLERS = new ConcurrentHashMap<>();

    private final List<String> subscribedChannels = new ArrayList<>();

    private final PacketSerializer packetSerializer;

    public LettuceRedisConnection(String uri, PacketSerializer packetSerializer) {
        Objects.requireNonNull(uri, "URI cannot be null");
        this.packetSerializer = Objects.requireNonNull(packetSerializer, "Packet Serializer cannot be null");

        this.redisClient = RedisClient.create(uri);
        this.connection = redisClient.connect();
        this.commands = connection.sync();
        this.pubSubConnection = redisClient.connectPubSub();

        pubSubListener();
    }

    @Override
    public PacketSerializer getPacketSerializer() {
        return packetSerializer;
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

    @Override
    public Result<Void> publishPacket(RedisPacket packet, ResponseCallback response, long timeout) {
        try {
            final String correlationId = UUID.randomUUID().toString();
            packet.setCorrelationId(correlationId);

            final CompletableFuture<RedisPacket> future = new CompletableFuture<>();
            pendingResponses.put(correlationId, future);

            commands.publish(packet.getChannelName(), packetSerializer.serialize(packet));

            future.orTimeout(timeout, TimeUnit.MILLISECONDS)
                    .whenComplete((resp, ex) -> {
                        pendingResponses.remove(correlationId);

                        if (ex != null) {
                            response.call(null);
                        } else {
                            response.call(resp);
                        }
                    });

            return Result.success(null);
        } catch (Exception e) {
            return Result.violation(new Violation<>(e));
        }
    }

    @Override
    public <T extends RedisPacket> void registerHandler(Class<T> packetClass, PacketHandler handler) {
        HANDLERS.put(packetClass, handler);
    }

    @Override
    public void subscribe(String channelName) {
        pubSubConnection.sync().subscribe(channelName);
        subscribedChannels.add(channelName);
    }

    @Override
    public void unsubscribe(String channelName) {
        pubSubConnection.sync().unsubscribe(channelName);
        subscribedChannels.remove(channelName);
    }

    @Override
    public Map<String, CompletableFuture<RedisPacket>> getPendingResponses() {
        return Map.copyOf(pendingResponses);
    }

    @Override
    public void close() throws Exception {
        try {
            pubSubConnection.close();
            connection.close();
        } finally {
            redisClient.shutdown();
        }
    }

    private void pubSubListener() {
        pubSubConnection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                if (!subscribedChannels.isEmpty() && !subscribedChannels.contains(channel)) return;

                final RedisPacket packet = packetSerializer.deserialize(message, RedisPacket.class);

                final CompletableFuture<RedisPacket> future = pendingResponses.remove(packet.getCorrelationId());
                if (future != null) {
                    future.complete(packet);
                    return;
                }

                final PacketHandler handler = HANDLERS.get(packet.getClass());
                if (handler != null) {
                    final RedisPacket packetResponse = handler.handle(packet);

                    if (packetResponse != null) {
                        packetResponse.setCorrelationId(packet.getCorrelationId());
                        commands.publish(packetResponse.getChannelName(), packetSerializer.serialize(packetResponse));
                    }
                }
            }

            @Override public void message(String pattern, String channel, String message) {}
            @Override public void subscribed(String channel, long count) {}
            @Override public void psubscribed(String pattern, long count) {}
            @Override public void unsubscribed(String channel, long count) {}
            @Override public void punsubscribed(String pattern, long count) {}
        });
    }
}
