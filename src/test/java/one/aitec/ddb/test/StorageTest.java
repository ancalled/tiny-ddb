package one.aitec.ddb.test;

import one.aitec.ddb.storage.ReadableStorage;
import one.aitec.ddb.storage.InMemoryStorage;
import one.aitec.ddb.storage.WritableStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    private static final String ACCS = "accounts";

    private ReadableStorage rs;
    private WritableStorage ws;


    @Before
    public void init() {
        InMemoryStorage storage = new InMemoryStorage();
        rs = storage;
        ws = storage;
    }

    @Test
    public void testAdd() {

        Account acc = new Account(1L, 0);
        ws.put(ACCS, acc);

        Account acc1 = (Account) rs.get(ACCS, 1L);
        assertEquals(acc.amount, acc1.amount);

        ws.update(ACCS, a -> a.id == 1L, new Account(1000));
        Account acc2 = (Account) rs.get(ACCS, 1L);
        assertEquals(1000, acc2.amount);

        Optional<Account> acc3Opt = rs.findFirst(ACCS, a -> a.amount == 1000, Account.class);
        assertTrue(acc3Opt.isPresent());
        Account acc3 = acc3Opt.get();
        assertEquals(1L, acc3.id);

        ws.remove(ACCS, 1L);

        assertFalse(rs.findFirst(ACCS, a -> a.amount == 1000, Account.class).isPresent());
    }

}
