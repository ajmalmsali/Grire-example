package org.grire.Helpers.mapdb;

/**
 * Wraps single transaction in a block
 */
public interface TxBlock {

    void tx(DB db) throws TxRollbackException;
}
