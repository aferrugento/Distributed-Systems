package example.server;


import java.io.*;

public class ObjectFile {

    private ObjectInputStream iS;
    private ObjectOutputStream oS;

    public void openRead(String fileName) throws IOException {
        iS = new ObjectInputStream(new FileInputStream(fileName));
    }

    public void openWrite(String fileName) throws IOException {
        oS = new ObjectOutputStream(new FileOutputStream(fileName));
        oS.reset();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return iS.readObject();
    }

    public void writeObject(Object o) throws IOException {
        oS.writeObject(o);
    }

    public void closeRead() throws IOException {
        iS.close();
    }

    public void closeWrite() throws IOException {
        oS.close();
    }
}