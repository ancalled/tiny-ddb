package one.aitec.ddb.test;

import one.aitec.ddb.Engine;
import one.aitec.ddb.replicator.transport.Transport;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class NodesTest {

    private static final String ACCS = "accounts";

    private Engine node1, node2;
    private Random random = new Random(System.currentTimeMillis());

    @Before
    public void init() {
        Transport transport = new DummyTransport();
        node1 = new Engine("node1", transport);
        node2 = new Engine("node2", transport);

        node1.start();
        node2.start();
    }


    @Test
    public void testReplication() throws InterruptedException {
        assertNull(node1.storage.get(ACCS, 1L));
        assertNull(node2.storage.get(ACCS, 1L));

        node1.storage.put(ACCS, new Account(1L, 2300));

        sleep(100);

        Account acc = (Account) node2.storage.get(ACCS, 1L);
        assertNotNull(acc);
        assertEquals(2300, acc.amount);

        Map<Long, Integer> amounts = new HashMap<>();
        long id = 2L;
        for (int i = 0; i < 100; i++) {
            int amount = random.nextInt(10000);
            amounts.put(id, amount);
            node2.storage.put(ACCS, new Account(id++, amount));
        }

        sleep(500);

        id = 2L;
        for (int i = 0; i < 100; i++) {
            acc = (Account) node1.storage.get(ACCS, id);
            int amount = amounts.get(id++);
            assertEquals(amount, acc.amount);
        }

        node1.storage.update(ACCS, a -> a.amount > 5000, new Account(0));

        sleep(500);

        node2.storage.find(ACCS, a -> a.amount > 5000, Account.class)
                .forEach(a -> assertEquals(0, a.amount));
    }
}
