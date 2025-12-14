package supernova.redis;

public interface ResponseCallback {

    void call(RedisPacket redisPacket);
}
