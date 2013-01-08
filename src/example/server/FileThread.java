package example.server;


public class FileThread extends Thread {

    private Server server;
    private ObjectFile objectUT;
    private ObjectFile objectPT;
    private ObjectFile objectMT;
    private ObjectFile objectDT;
    private ObjectFile objectUR;
    private ObjectFile objectPR;
    private ObjectFile objectMR;
    private ObjectFile objectDR;

    public FileThread(Server server) {
        this.server = server;
        this.objectUT = new ObjectFile();
        this.objectPT = new ObjectFile();
        this.objectMT = new ObjectFile();
        this.objectDT = new ObjectFile();
        this.objectUR = new ObjectFile();
        this.objectPR = new ObjectFile();
        this.objectMR = new ObjectFile();
        this.objectDR = new ObjectFile();

    }

    public void run() {
        try {
            server.loadUsersTCPInfo(this.objectUT);
            server.loadPostsTCPInfo(this.objectPT);
            server.loadMessagesTCPInfo(this.objectMT);
            server.loadDelayedPostsTCPInfo(objectDT);

            server.loadUsersRMIInfo(this.objectUR);
            server.loadPostsRMIInfo(this.objectPR);
            server.loadMessagesRMIInfo(this.objectMR);
            server.loadDelayedPostsRMIInfo(objectDR);
        } catch (ClassNotFoundException c) {
            System.out.println("ClassNotFoundException caught.");
        }
        while (true) {
            try {
                server.saveInfo(objectUT, objectPT, objectMT, objectDT, objectUR, objectPR, objectMR, objectDR);
                this.sleep(2000);


            } catch (InterruptedException i) {
                System.out.println("InterruptedException caught.");
            }

        }
    }
}