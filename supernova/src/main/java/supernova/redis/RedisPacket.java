package supernova.redis;

public abstract class RedisPacket {

    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public abstract String getChannelName();
}
