package main.java.CustomImplamentation;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ReadWriteLock {
    private int readers, writers, waitingWriters;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition readable = lock.newCondition();
    private final Condition writable = lock.newCondition();

    public void lockWrite() {
        lock.lock();
        try {
            while (readers != 0 || writers != 0)
                writable.awaitUninterruptibly();
            writers = 1;
        } finally {
            lock.unlock();
        }
    }

    public void unlockWrite() {
        lock.lock();
        try {
            writers = 0;
            if (waitingWriters != 0)
                writable.signal();
            else
                readable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void lockRead() {
        lock.lock();
        try {
            while (writers != 0)
                readable.awaitUninterruptibly();
            ++readers;
        } finally {
            lock.unlock();
        }
    }

    public void unlockRead() {
        lock.lock();
        try {
            if (--readers == 0)
                writable.signal();
        } finally {
            lock.unlock();
        }
    }
}
