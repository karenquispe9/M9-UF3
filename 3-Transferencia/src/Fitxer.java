package src;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//representa un fitxer del sistema
public class Fitxer {
    private final String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public byte[] getContingut() throws IOException {
        File file = new File(nom);
        //comprovem si el fitxer existeix
        if (!file.exists()) {
            throw new IOException("El fitxer no existeix: " + nom);
        }

        //comprovem si es un fitxer
        FileInputStream fis = new FileInputStream(file);

        //creem un array de bytes amb la mida del fitxer
        contingut = new byte[(int) file.length()];
        fis.read(contingut);
        fis.close();

        return contingut;
    }
}
