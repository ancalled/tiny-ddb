package one.aitec.ddb.test;

import one.aitec.ddb.Change;
import one.aitec.ddb.storage.ReadableStorage;
import one.aitec.ddb.storage.SingleWriter;
import one.aitec.ddb.storage.InMemoryStorage;
import one.aitec.ddb.storage.Storable;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static one.aitec.ddb.Change.Action.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SingleWriterTest {

    private static final String ACCS = "accounts";

    private ReadableStorage rs;
    private SingleWriter swt;

    @Before
    public void init() {
        InMemoryStorage storage = new InMemoryStorage();
        rs = storage;
        swt = new SingleWriter("node1", storage);
        swt.start();
    }


    @Test
    public void testThread() throws InterruptedException {

        Change<Account> change = new Change<>(ACCS, PUT, new Account(1L, 250), new CountDownLatch(1));
        swt.publish(change);
        change.await();

        Account acc = (Account) rs.get(ACCS, 1L);
        assertEquals(250, acc.amount);

        change = new Change<>(ACCS, UPDATE, a -> a.amount == 250, new Account(350), new CountDownLatch(1));
        swt.publish(change);
        change.await();

        Account acc1 = (Account) rs.get(ACCS, 1L);
        assertEquals(350, acc1.amount);

        change = new Change<>(ACCS, REMOVE, 1L, new CountDownLatch(1));
        swt.publish(change);
        change.await();

        assertNull(rs.get(ACCS, 1L));
    }


}
