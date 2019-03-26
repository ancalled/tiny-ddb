package one.aitec.ddb.replicator.transport;

public interface MessageListener {

    void onMessage(String sender, String topic, byte[] bytes);

}
