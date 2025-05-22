import java.io.*;
import java.net.*;
import java.util.Scanner;


/**
 * Classe ClientXat
 * Aquesta classe representa un client del xat que es pot connectar al servidor,
 * rebre i enviar missatges, tant personals com grupals.
 * També permet sortir del xat o tancar-lo per a tots els clients.
 */
public class ClientXat implements Runnable {
     // Socket per connectar-se amb el servidor
    private Socket socket;

    // Objecte per enviar missatges al servidor
    private ObjectOutputStream oos;

    // Objecte per rebre missatges del servidor
    private ObjectInputStream ois;

    // Boolean per saber si hem d'acabar l'execució
    private boolean sortir = false;

    //estableix voneccio amb el servidor i crea els fluxos d'entrada i sortida
    public void connecta() throws IOException {
        // Creem el socket cap al servidor (HOST i PORT definits al ServidorXat)
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void tancarClient() {
        try {
            sortir = true;
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            if (socket != null) socket.close();
            System.out.println("Tancant client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Error enviant missatge: " + e.getMessage());
        }
    }

    //inic el fil d'execució secundari
    public void run() {
        try {
            while (!sortir) {
                //llegim el missatge del servidor
                String missatgeCru = (String) ois.readObject();
                //obtenim el codi del missatge per saber de quin tipus es
                String codi = Missatge.getCodiMissatge(missatgeCru);

                if (codi == null) continue;

                switch (codi) {
                    //si es missatge per a tot el grup
                    case Missatge.CODI_MSG_GRUP:
                        System.out.println("Missatge grup: " + Missatge.getPartsMissatge(missatgeCru)[1]);
                        break;
                    //si es missatge personal
                    case Missatge.CODI_MSG_PERSONAL:
                        String[] parts = Missatge.getPartsMissatge(missatgeCru);
                        System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                        break;
                    //si ha de sortir tothom
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;

                    default:
                        System.out.println("Missatge desconegut");
                }
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();//es conecta al servidor
            Thread threadRebre = new Thread(client);//creem un fil per rebre missatge 
            threadRebre.start();

            Scanner scanner = new Scanner(System.in);
            boolean sortirMenu = false;

            //bucle principal del menu
            while (!sortirMenu) {
                mostrarAjuda();
                String opcio = scanner.nextLine();

                switch (opcio) {
                    case "1":
                        System.out.print("Introdueix el nom: ");
                        String nom = scanner.nextLine();
                        client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                        break;

                    case "2":
                        System.out.print("Destinatari: ");
                        String destinatari = scanner.nextLine();
                        System.out.print("Missatge a enviar: ");
                        String missatge = scanner.nextLine();
                        client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatge));
                        break;

                    case "3":
                        System.out.print("Missatge a enviar: ");
                        missatge = scanner.nextLine();
                        client.enviarMissatge(Missatge.getMissatgeGrup(missatge));
                        break;

                    case "4":
                        client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                        sortirMenu = true;
                        break;

                    case "5":
                        client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                        sortirMenu = true;
                        break;

                    default:
                        sortirMenu = true;
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.tancarClient();
        }
    }

    public static void mostrarAjuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }
}