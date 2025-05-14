import java.io.*;

public class FilLectorCX implements Runnable {
    private BufferedReader entrada;

    public FilLectorCX(BufferedReader entrada) {
        this.entrada = entrada;
    }

    @Override
    public void run() {
        try {
            String resposta;
            while ((resposta = entrada.readLine()) != null) {
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + resposta);
                if (resposta.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
