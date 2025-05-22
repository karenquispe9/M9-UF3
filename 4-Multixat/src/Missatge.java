public class Missatge {
    public static final String CODI_SORTIR_TOTS = "0000";
    public static final String CODI_CONECTAR = "1000";
    public static final String CODI_MSG_PERSONAL = "1001";
    public static final String CODI_MSG_GRUP = "1002";
    public static final String CODI_SORTIR_CLIENT = "1003";

    // Métodos para crear los distintos tipos de mensaje
    public static String getMissatgePersonal(String destinatari, String missatge) {
        return CODI_MSG_PERSONAL + "#" + destinatari + "#" + missatge;
    }

    public static String getMissatgeGrup(String missatge) {
        return CODI_MSG_GRUP + "#" + missatge;
    }

    public static String getMissatgeSortirClient(String missatge) {
        return CODI_SORTIR_CLIENT + "#" + missatge;
    }

    public static String getMissatgeSortirTots(String missatge) {
        return CODI_SORTIR_TOTS + "#" + missatge;
    }

    public static String getMissatgeConectar(String nom) {
        return CODI_CONECTAR + "#" + nom;
    }

    // Obtiene el código del mensaje
    public static String getCodiMissatge(String missatgeRaw) {
        if (missatgeRaw == null || !missatgeRaw.contains("#")) {
            System.out.println("ERROR: mensaje vacío o inválido");
            return null;
        }
        return missatgeRaw.split("#")[0];
    }

    // Divide el mensaje en partes
    public static String[] getPartsMissatge(String missatgeRaw) {
        if (missatgeRaw == null || !missatgeRaw.contains("#")) {
            System.out.println("ERROR: mensaje vacío o inválido");
            return null;
        }
        return missatgeRaw.split("#");
    }
}