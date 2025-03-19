import java.io.*;

class NumberRepresentation implements Serializable {
    private static final long serialVersionUID = 1L;
    private int number;
    private transient String binary;
    private transient String octal;
    private transient String hexadecimal;

    public NumberRepresentation(int number) {
        this.number = number;
        convert();
    }

    private void convert() {
        this.binary = Integer.toBinaryString(number);
        this.octal = Integer.toOctalString(number);
        this.hexadecimal = Integer.toHexString(number);
    }

    public int getNumber() { return number; }
    public String getBinary() { return binary; }
    public String getOctal() { return octal; }
    public String getHexadecimal() { return hexadecimal; }

    public void restore() {
        convert();
    }

    @Override
    public String toString() {
        return "Число: " + number + " | Бінарне: " + binary + " | Вісімкове: " + octal + " | Шістнадцяткове: " + hexadecimal;
    }
}

class Serializer {
    private static final String FILE_NAME = "number_data.ser";

    public static void save(NumberRepresentation obj) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(obj);
        }
    }

    public static NumberRepresentation load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            NumberRepresentation obj = (NumberRepresentation) in.readObject();
            obj.restore();
            return obj;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            NumberRepresentation num = new NumberRepresentation(255);
            System.out.println("Збереження: " + num);
            Serializer.save(num);

            NumberRepresentation loadedNum = Serializer.load();
            System.out.println("Відновлення: " + loadedNum);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
