package one.aitec.ddb.replicator.transport;

public interface Transport {


    void publish(String sender, String topic, byte[] bytes);

    void addListener(MessageListener listener);

}
