package ex03;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

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

    public int getNumber() { return number; }
    public String getBinary() { return binary; }
    public String getOctal() { return octal; }
    public String getHexadecimal() { return hexadecimal; }

    @Override
    public String toString() {
        return number + "\t" + binary + "\t" + octal + "\t" + hexadecimal;
    }
}

class ViewResult implements View {
    protected static final String FILE_NAME = "numbers.ser";
    protected ArrayList<NumberRepresentation> items = new ArrayList<>();

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

class TableViewResult extends ViewResult {
    private int columnWidth;

    public TableViewResult(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    public void viewShow() {
        System.out.printf("%-" + columnWidth + "s%-" + columnWidth + "s%-" + columnWidth + "s%-" + columnWidth + "s%n",
                "Число", "Бінарне", "Вісімкове", "Шістнадцяткове");
        System.out.println("-".repeat(columnWidth * 4));
        for (NumberRepresentation item : items) {
            System.out.printf("%-" + columnWidth + "d%-" + columnWidth + "s%-" + columnWidth + "s%-" + columnWidth + "s%n",
                    item.getNumber(), item.getBinary(), item.getOctal(), item.getHexadecimal());
        }
    }
}

class ViewableResult implements Viewable {
    private int columnWidth;

    public ViewableResult(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    public View getView() {
        return new TableViewResult(columnWidth);
    }
}

public class Factory {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть ширину колонки для таблиці: ");
        int columnWidth = scanner.nextInt();

        View view = new ViewableResult(columnWidth).getView();
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
