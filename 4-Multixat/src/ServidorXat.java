import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Clase principal del servidor del chat múltiple.
 * Gestiona las conexiones de clientes, mensajes grupales y salidas.
 */
public class ServidorXat {
    public static final int PORT = 9999; // Puerto donde escuchará el servidor
    public static final String HOST = "localhost";  // Dirección del host
    public static final String MSG_SORTIR = "sortir";  // Código o mensaje que indica que un usuario quiere salir

    // Tabla hash para almacenar los clientes conectados
    private Hashtable<String, GestorClient> clients = new Hashtable<>();
    private boolean sortir = false;

    /**
     * Método que inicia el servidor y empieza a aceptar conexiones.
     * throws IOException Si hay errores al crear el socket del servidor.
     */
    public void servidorAEscoltar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

        while (!sortir) {
             // Aceptar nueva conexión de cliente
            Socket socketClient = serverSocket.accept();

            // Crear un nuevo gestor de cliente
            GestorClient gestorClient = new GestorClient(socketClient, this);

            // Iniciar un nuevo hilo para atender al cliente
            Thread thread = new Thread(gestorClient);
            thread.start();
        }

        pararServidor(serverSocket);
    }


    
    public void pararServidor(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void afegirClient(GestorClient client) {

        // Añadimos el cliente a la tabla hash
        clients.put(client.getNom(), client);
        enviarMissatgeGrup("Entra: " + client.getNom());
    }

    public void eliminarClient(String nom) {
        clients.remove(nom);
        System.out.println("Client " + nom + " eliminat.");
    }

    public void enviarMissatgeGrup(String missatge) {
     System.out.println("DEBUG: multicast " + missatge);
    
     // Recorrer todos los clientes y enviarles el mensaje con el formato correcto
     Enumeration<GestorClient> clientsEnum = clients.elements();
     while (clientsEnum.hasMoreElements()) {
         GestorClient client = clientsEnum.nextElement();
         // Usar el formato de mensaje de grupo
         client.enviarMissatge("Servidor", Missatge.getMissatgeGrup(missatge));
     }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClient client = clients.get(destinatari);
        if (client != null) {
            client.enviarMissatge(remitent, missatge);
        } else {
            System.out.println("Destinatario no encontrado: " + destinatari);
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(Missatge.CODI_SORTIR_TOTS + "#Adéu");
        clients.clear();
        sortir = true;
        System.out.println("Tancant tots els clients.");
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.servidorAEscoltar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

