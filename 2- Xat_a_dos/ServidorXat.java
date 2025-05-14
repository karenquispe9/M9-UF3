import java.io.*;
import java.net.*;

public class ServidorXat {

    public static final int PORT = 6789; // Puedes cambiarlo si está ocupado
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(BufferedReader entrada) throws IOException {
        return entrada.readLine(); // rep el nom del client
    }


    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.iniciarServidor();

            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter sortida = new PrintWriter(clientSocket.getOutputStream(), true);

            String nomClient = servidor.getNom(entrada);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(entrada);
            System.out.println("Fil de xat creat.");
            Thread t = new Thread(fil);
            t.start();
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
   
