package one.aitec.ddb;

public interface Executable {

    void await() throws InterruptedException;

    void countDown();
}
