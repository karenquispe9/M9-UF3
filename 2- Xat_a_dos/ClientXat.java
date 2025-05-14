import java.io.*;
import java.net.*;

public class ClientXat {
    private Socket socket;
    private PrintWriter sortida;
    private BufferedReader entrada;

    public void connecta() throws IOException {
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
        sortida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void enviarMissatge(String missatge) {
        sortida.println(missatge);
    }

    public void tancarClient() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Client tancat.");
        }
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connecta();

            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String nom = consola.readLine(); 
            System.out.println("Enviant missatge: " + nom);
            client.enviarMissatge(nom);

            FilLectorCX lector = new FilLectorCX(client.entrada);
            Thread t = new Thread(lector);
            t.start();

            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                System.out.println("Enviant missatge: " + missatge);
                client.enviarMissatge(missatge);
            } while (!missatge.equalsIgnoreCase(ServidorXat.MSG_SORTIR));

            t.join();
            consola.close();
            client.tancarClient();
            System.out.println("El servidor ha tancat la connexió.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
