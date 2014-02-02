package ajmal.Helpers.Listeners;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 21/8/2013
 * Time: 2:46 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ConcurrentIterator {

    volatile Iterator iter;

    public ConcurrentIterator(Iterator iter) {
        this.iter = iter;
    }

    synchronized public Object getNext() {
        return (iter.hasNext()?iter.next():-1l);
    }
}
