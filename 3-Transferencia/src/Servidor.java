package src;


import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost"; //doreccion del host, es este caso es local = localhost

    //inicia el serversocket y acepta una connexio
    public Socket connectar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        //acepta la conexion entrante de client
        return serverSocket.accept();
    }

    //tanca la connexio del socket
    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
    }

    //llegeix el nom del fitxer del client i envia el contingut del fitxer com byte[]
    public void enviarFitxer(Socket socket) throws IOException, ClassNotFoundException {
        //fluxes d'entrada i sortida per enviar i rebre objectes
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        System.out.println("Esperant el nom del fitxer del client...");

        //llegim el nom del fitxer enviat pel client
        String nomFitxer = (String) in.readObject();
        if (nomFitxer == null || nomFitxer.trim().isEmpty()) {
            System.out.println("Nom del fitxer buit o nul. Sortint...");
            return;
        }

        System.out.println("Nomfitxer rebut: " + nomFitxer);

        //creem un objecte Fitxer amb el nom rebut
        Fitxer fitxer = new Fitxer(nomFitxer);
        try {
            byte[] bytes = fitxer.getContingut();
            System.out.println("Contingut del fitxer a enviar: " + bytes.length + " bytes");
            
            //enviem el contingut del fitxer com byte[] al client
            out.writeObject(bytes);
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        } catch (IOException e) {
            System.out.println("Error llegint el fitxer del client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            Socket socket = servidor.connectar();
            System.out.println("Connexio acceptada: " + socket.getInetAddress());
            servidor.enviarFitxer(socket);
            servidor.tancarConnexio(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
