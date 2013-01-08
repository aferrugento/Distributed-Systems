package example.server;


import java.util.*;
import java.util.Calendar.*;
import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class Delay extends Thread implements Serializable {

    private Vector<DelayedPost> toBeSent;
    private DelayedPost next;
    private Calendar time;
    public Calendar sleep;
    private TCPServer server;
    private Long timeMilS;
    private final Semaphore available;
    private final Semaphore fileRead;

    public Delay(TCPServer server) {
        this.toBeSent = new Vector<DelayedPost>();
        this.next = null;
        this.time = null;
        this.server = server;
        this.timeMilS = null;
        this.available = new Semaphore(0);
        this.fileRead = new Semaphore(0);

    }

    public void send(DelayedPost mess) {
        server.handle(mess.getAuthor(), mess.getPost(), false);
        toBeSent.remove(mess);
    }

    public synchronized void awake() {
        this.notify();
    }

    public void run() {
        System.out.println("Delayed posts responsible thread is awake now.");
        try {
            fileRead.acquire();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught.");
        }

        if (toBeSent.size() > 0) {
            verifyNext();
        } else {
            timeToSleep(false);
        }

        while (true) {
            try {
                available.acquire();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught.");
            }
            timeToSleep(true);
        }
    }

    public synchronized void timeToSleep(Boolean timeOut) {

        try {
            if (timeOut) {
                wait(this.timeMilS);
                send(next);
            } else {
                wait(); //waits for an addNew
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught.");
        }

    }

    public void addNew(DelayedPost post) {
        toBeSent.add(post);
        verifyNext();
        available.release();

    }

    public void verifyNext() {
        Calendar minTime = toBeSent.get(0).getTime();
        DelayedPost min = toBeSent.get(0);

        for (int i = 0; i < toBeSent.size(); i++) {
            if (toBeSent.get(i).getTime().before(minTime)) {
                minTime = toBeSent.get(i).getTime();
                this.next = toBeSent.get(i);
            }
        }

        this.next = min;
        this.time = minTime;
        this.timeMilS = this.time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        available.release();
    }

    public Vector<DelayedPost> getToBeSent() {
        return toBeSent;
    }

    public void setToBeSent(Vector<DelayedPost> toBeSent) {

        fileRead.release();
        this.toBeSent = toBeSent;
    }
}
