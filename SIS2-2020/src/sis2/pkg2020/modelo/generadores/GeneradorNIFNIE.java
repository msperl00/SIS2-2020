package sis2.pkg2020.modelo.generadores;



import sis2.pkg2020.vista.ModeloXML;
import sis2.pkg2020.modelo.Trabajadorbbdd;


/**
 * Clase que se encarga de la validación y correción de los errores en el nif y
 * en el nie del trabajador
 *
 * @author Marco Speranza López
 */
public class GeneradorNIFNIE {

    private final String letrasNie = "XYZ";
    private final String letrasNif = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String numeros = "0123456789";
    private ModeloXML modelo;
    private String nifnie;
    private Trabajadorbbdd trabajador;

    public GeneradorNIFNIE(Trabajadorbbdd trabajador) {

        this.trabajador = trabajador;
        this.nifnie = this.trabajador.getNifnie();
        this.modelo = modelo;

    }
    
   

    /**
     * Metodo que devulve la cadena correcta calculada segun si es nif o nie
     * y que llama a los metodos que relizan dichas operaciones.
     *
     * @return boolean
     */
    public String validar() {

        if (isNIE(nifnie.substring(0, 1))) {
           // System.out.println("Es NIE");
            String correcto = GeneradorNIE(nifnie);
            if (!correcto.equals(nifnie)) {
             
                return correcto;
                
            }

        } else if (isNIF(nifnie.substring(0, 1))) {
          //  System.out.println("ES NIF");
            String correcto = GeneradorNIF(nifnie);
            if (!correcto.equals(nifnie)) {
        
               return correcto;

            }
        }

        return nifnie;
    }

    private boolean isNIF(String inicial) {
        if (numeros.contains(inicial)) {
            return true;
        }
        return false;
    }

    private boolean isNIE(String inicial) {

        if (letrasNie.contains(inicial) && nifnie.startsWith(inicial)) {
            return true;
        }
        return false;
    }

    private String GeneradorNIF(String nifnie) {
        String prueba = nifnie;
        if (prueba.length() == 9) {
            prueba = prueba.substring(0, prueba.length() - 1);

        }
        return prueba + GeneradorLetra(prueba);
    }

    private String GeneradorNIE(String nifnie) {

        String prueba = null;
        String devolver = null;
        if (nifnie.length() == 9) {
            prueba = nifnie.substring(0, nifnie.length() - 1);
            devolver = nifnie.substring(0, nifnie.length() - 1);

        }

        if (prueba.startsWith("X")) {

            prueba = prueba.replace('X', '0');

        } else if (prueba.startsWith("Y")) {
            prueba = prueba.replace('Y', '1');
        } else if (prueba.startsWith("Z")) {
            prueba = prueba.replace('Z', '2');
        }

        return devolver + GeneradorLetra(prueba);
    }

    /**
     * Calcula la letra correspodiente a una cadena que se le pasa, segun el
     * agoritmo de validacion del codigo fiscal.
     *
     * @param prueba
     * @return Letra generada del nif.
     */
    private char GeneradorLetra(String prueba) {
        return letrasNif.charAt(Integer.parseInt(prueba) % 23);
    }
}
