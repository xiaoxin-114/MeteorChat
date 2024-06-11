package serialize;

import java.io.*;

public class TestMain {
    private static final String FILE_NAME = "test.txt";
    public static void main(String[] args) {
        try(FileOutputStream fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            SerializeObj obj = new SerializeObj();
            obj.setName("meteor");
            obj.setAge(10);
            System.out.println(obj);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        readObject();
    }

    public static void writeObject(Object obj){
        try(FileOutputStream fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            System.out.println(obj);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readObject(){
        try(FileInputStream fis = new FileInputStream(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);) {
            SerializeObj o1 = (SerializeObj) ois.readObject();
            System.out.println(o1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
