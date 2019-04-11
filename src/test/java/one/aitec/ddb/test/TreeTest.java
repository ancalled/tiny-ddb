package one.aitec.ddb.test;

import one.aitec.ddb.nodetree.TreeLeaf;
import one.aitec.ddb.nodetree.TreeNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {

    @Test
    public void testPutGetRemobeElement() {
        TreeNode<Account> node = new TreeNode<>();

        Account account = new Account(34L, 1000);
        String key = Long.toString(account.id);
        node.putElement(key, new TreeLeaf<>(null, key, account));
        assertEquals(account, node.getElement(key));

        node.removeElement(key);

        assertNull(node.getElement(key));
    }

    @Test
    public void updateElementTest() {
        TreeNode<Account> node = new TreeNode<>();
        node.putElement("1", new TreeLeaf<>(null, "1", new Account(1L, 1000)));
        node.putElement("2", new TreeLeaf<>(null, "2", new Account(2L, 1040)));
        node.putElement("3", new TreeLeaf<>(null, "3", new Account(3L, 0)));
        node.putElement("4", new TreeLeaf<>(null, "4", new Account(4L, 500)));
        node.putElement("5", new TreeLeaf<>(null, "5", new Account(5L, 0)));

        node.updateElement(null,
                a -> a.amount > 1000,
                a -> a.amount = 1000,
                null,
                res -> {
                    assertEquals(1, res.affectedKeys.size());
                    assertTrue(res.affectedKeys.contains("2"));
                });

        TreeLeaf<Account> element = (TreeLeaf<Account>) node.getElement("2");
        assertEquals(1000, (int) element.getData().amount);
    }

    @Test
    public void treeTest() {
        TreeNode<TreeNode<Account>> node = new TreeNode<>();

        Account account = new Account(1L, 1000);
        node.put("/account/1", account);

        Account found = node.findItem("/account/1", Account.class);
        assertEquals(account, found);

        assertNull(node.findItem("/account/2"));
        assertNull(node.findItem("/acco_unt/1"));

        node.remove("/account/1");

        assertNull(node.findItem("/account/1"));

        node.put("/account/4", new Account(4L, 3000));

        node.update("/account/4",
                Account.class,
                null,
                null,
                a -> a.amount -= 500);

        Account acc4 = node.findItem("/account/4", Account.class);
        assertEquals(2500, (int) acc4.amount);

        System.out.println(node);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNodeReference() {
        TreeNode<TreeNode<Account>> node = new TreeNode<>();
        node.put("/{id}/{class}", new Account(1L, 1000));
        node.put("/{id}/{class}", new Account(2L, 1000));
        node.put("/{id}/{class}", new Account(3L, 1000));
        System.out.println(node);

        TreeNode<Account> acc1Node = (TreeNode<Account>) node.findNode("/1/account");
        TreeNode<Account> acc2Node = (TreeNode<Account>) node.findNode("/2/account");

        assertEquals(1L, (long) acc1Node.getElement("account").getData().id);
        assertEquals(2L, (long) acc2Node.getElement("account").getData().id);
    }


    @Test
    public void varsInPath() {
        TreeNode<TreeNode<Account>> node = new TreeNode<>();
        node.put(new Account(1L, 1000));
        node.put(new Account(2L, 1000));
        node.put("{class}/{id}/inner", new Account(3L, 2000));
        node.put("{random}", new Account(4L, 3000), r -> {
            String newKey = r.affectedKeys.get(0);
            System.out.println("new path: " + r.path + newKey + "\n");
        });
        System.out.println(node.toString());


        Account acc = node.findItem("/account/1", Account.class);
        assertEquals(1000, (int) acc.amount);

        Account acc2 = node.findItem("/account/3/inner", Account.class);
        assertEquals(2000, (int) acc2.amount);
    }

    @Test
    public void testReplicate() {
        TreeNode<TreeNode<Account>> node1 = new TreeNode<>();
        node1.put(new Account(1L, 100));
        node1.put(new Account(2L, 200));
        node1.put("{class}/{id}/inner", new Account(3L, 2000));
        node1.put("{random}", new Account(4L, 3000), r -> {
            String newKey = r.affectedKeys.get(0);
            System.out.println("new path: " + r.path + newKey + "\n");
        });

        System.out.println("Node 1");
        System.out.println(node1);
        System.out.println();

        TreeNode<TreeNode<Account>> node2 = new TreeNode<>();

        System.out.println("Node 2 before update");
        System.out.println(node2);
        System.out.println();
        
        node1.checkAndUpdate(node2);
        System.out.println("Node 2 after update");
        System.out.println(node2);

        node2.update("/account/2",
                Account.class,
                null,
                null,
                a -> a.amount -= 500);

        System.out.println("Node 2 after modification");
        System.out.println(node2);

        System.out.println("Node 1 before update");
        System.out.println(node1);

        node2.checkAndUpdate(node1);
        System.out.println("Node 1 after update");
        System.out.println(node1);
    }

}
