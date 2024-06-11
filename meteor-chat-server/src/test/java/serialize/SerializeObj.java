package serialize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializeObj implements Serializable {
    private static final long serialVersionUID = -2012558958766755430L;
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "SerializeObj{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    private void writeObject(ObjectOutputStream outputStream) {
        System.out.println("name={" + name + "}object be serialized");
        try {
            outputStream.defaultWriteObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readObject(ObjectInputStream inputStream) {
        System.out.println("name={" + name + "}object read from serialized data");
        try {
            inputStream.defaultReadObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
