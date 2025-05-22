import java.io.*;
import java.net.*;


/**
 * Classe GestorClient
 * Aquesta classe s'encarrega de gestionar cada client connectat al servidor.
 * Cada client té el seu propi fil d'execució per poder atendre diverses connexions alhora.
 * El servidor crea un objecte GestorClient per a cada client que es connecta.
 */
public class GestorClient implements Runnable {

    private Socket clientSocket;

    // Objecte per enviar missatges al client
    private ObjectOutputStream oos;

    // Objecte per rebre missatges del client
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir = false;


    /**
     * Constructor de la classe GestorClient
     * Inicialitza el socket i els fluxos d'entrada i sortida del client.
     */
    public GestorClient(Socket socket, ServidorXat servidor) {
        this.clientSocket = socket;
        this.servidorXat = servidor;
        try {
            this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
            this.ois = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        String missatgeCru = "";
        try {
            while (!sortir && (missatgeCru = (String) ois.readObject()) != null) {
                processaMissatge(missatgeCru);
            }
        } catch (Exception e) {
            System.out.println("Error llegint missatge: " + e.getMessage());
        } finally {
            try {
                // Tancam el socket en cas d'error o desconexió
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Envia un missatge al client des del servidor
     */
    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processa els missatges que rep el client segons el codi que tenen.
     * Els missatges poden ser: connectar, enviar missatge personal, sortir, etc.
     */
    private void processaMissatge(String missatgeCru) {
        String codi = Missatge.getCodiMissatge(missatgeCru);
        if (codi == null) return;

        switch (codi) {
            // Si el client vol connectar, agafem el seu nom i l'afegim al xat
            case Missatge.CODI_CONECTAR:
                String[] parts = Missatge.getPartsMissatge(missatgeCru);
                if (parts.length > 1) {
                    nom = parts[1];
                    servidorXat.afegirClient(this);
                }
                break;
             // El client vol sortir, l'eliminem de la llista
            case Missatge.CODI_SORTIR_CLIENT:
                servidorXat.eliminarClient(nom);
                sortir = true;
                break;
             // El client vol tancar el xat per a tothom
            case Missatge.CODI_SORTIR_TOTS:
                servidorXat.finalitzarXat();
                sortir = true;
                break;
             // El client envia un missatge privat a un altre usuari
            case Missatge.CODI_MSG_PERSONAL:
                parts = Missatge.getPartsMissatge(missatgeCru);
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String text = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nom, text);
                }
                break;

            default:
                System.out.println("Codi desconegut: " + codi);
        }
    }
}