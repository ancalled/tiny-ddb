package one.aitec.ddb.test;

import one.aitec.ddb.storage.Storable;

public class Account implements Storable {

    Long id;
    Integer amount;


    public Account(Long id, Integer amount) {
        this.id = id;
        this.amount = amount;
    }

    public Account(Integer amount) {
        this.amount = amount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void update(Storable change) {
        if (change instanceof Account) {
            Account accChange = (Account) change;
            if (accChange.amount != null) {
                this.amount = accChange.amount;
            }
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
