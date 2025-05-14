import java.io.*;

public class FilServidorXat implements Runnable {
    private BufferedReader entrada;

    public FilServidorXat(BufferedReader entrada) {
        this.entrada = entrada;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = entrada.readLine()) != null) {
                if (missatge.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) break;
                System.out.println("Rebut: " + missatge);
            }
            System.out.println("Fil de xat finalitzat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
