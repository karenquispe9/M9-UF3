package src;


import java.io.*;
import java.net.*;

//representa un client que es connecta a un servidor 
public class Client {
    private static final String DIR_ARRIBADA = "C:\\Users\\karen\\Downloads";  //directori on es guardaran els fitxers rebuts
    private Socket socket; //conexio amb el servidor
    private ObjectOutputStream out; //flux d'objectes per enviar al servidor
    private ObjectInputStream in; //flux d'objectes per rebre del servidor

    //aqui establim la connexio amb el servidor
    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Connectant a -> localhost:" + 9999);
        System.out.println("Connexio acceptada: localhost/127.0.0.1");

        //inicialitzem els fluxes d'entrada i sortida
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    //aqui llegim el nom del fitxer a rebre i el contingut del fitxer com byte[]
    public void rebreFitxer() throws IOException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Introdueix la ruta completa del fitxer a descarregar ('sortir' per cancel·lar): ");
        String nomFitxer = reader.readLine();

        if (nomFitxer == null || nomFitxer.trim().equalsIgnoreCase("sortir")) {
            System.out.println("Operació cancel·lada.");
            return;
        }

        // Enviamos el nombre del archivo al servidor
        out.writeObject(nomFitxer);

        // Recibimos los bytes del archivo
        byte[] bytes = (byte[]) in.readObject();

        if (bytes == null) {
            System.out.println("No s'ha rebut contingut.");
            return;
        }

        // Guardamos el archivo localmente
        String nomDesti = DIR_ARRIBADA + File.separator + new File(nomFitxer).getName();
        try (FileOutputStream fos = new FileOutputStream(nomDesti)) {
            fos.write(bytes);
        }

        System.out.println("Fitxer rebut i guardat com: " + nomDesti);
    }

    public void tancarConnexio() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Connexio tancada.");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxer(); 
            client.tancarConnexio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
