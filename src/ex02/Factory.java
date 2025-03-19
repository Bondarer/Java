package ex02;
import java.io.*;
import java.util.ArrayList;

interface View {
    void viewShow();
    void viewInit();
    void viewSave() throws IOException;
    void viewRestore() throws IOException, ClassNotFoundException;
}

interface Viewable {
    View getView();
}

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

    public void restore() { convert(); }
    @Override
    public String toString() {
        return "Число: " + number + " | Бінарне: " + binary + " | Вісімкове: " + octal + " | Шістнадцяткове: " + hexadecimal;
    }
}

class ViewResult implements View {
    private static final String FILE_NAME = "numbers.ser";
    private ArrayList<NumberRepresentation> items = new ArrayList<>();

    @Override
    public void viewInit() {
        for (int i = 1; i <= 5; i++) {
            items.add(new NumberRepresentation(i * 10));
        }
    }

    @Override
    public void viewShow() {
        for (NumberRepresentation item : items) {
            System.out.println(item);
        }
    }

    @Override
    public void viewSave() throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            os.writeObject(items);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void viewRestore() throws IOException, ClassNotFoundException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            items = (ArrayList<NumberRepresentation>) is.readObject();
            for (NumberRepresentation item : items) {
                item.restore();
            }
        }
    }
}

class ViewableResult implements Viewable {
    @Override
    public View getView() {
        return new ViewResult();
    }
}

public class Factory {
    public static void main(String[] args) {
        View view = new ViewableResult().getView();
        view.viewInit();
        System.out.println("До збереження:");
        view.viewShow();

        try {
            view.viewSave();
            view.viewRestore();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Після відновлення:");
        view.viewShow();
    }
}

