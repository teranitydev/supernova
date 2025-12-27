package supernova.redis;

/**
 * A handler which handle {@link RedisPacket}.
 *
 * @author Izhar Atharzi
 * @since 0.0.3
 */
public interface PacketHandler {

    /**
     * The handler function.
     *
     * @param packet The primary packet
     * @return Returns the packet response if there's a response, otherwise just returns null
     */
    <T extends RedisPacket> RedisPacket handle(T packet);
}
