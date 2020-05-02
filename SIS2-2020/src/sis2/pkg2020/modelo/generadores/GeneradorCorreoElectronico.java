package sis2.pkg2020.modelo.generadores;

import java.text.Normalizer;
import java.util.ArrayList;
import sis2.pkg2020.modelo.Trabajadorbbdd;

/**
 * Esta es la clase con la que se generará el correo electronico para los
 * distintos trabajadores, con el fin de hacerles llegar la nomina de manera
 * regular a su correo.
 *
 * Proceso:
 *
 * 1º Letra inicial del primer apellido 2º Letra inicial del segundo apellido
 * (En caso de tenerlo) 3º Letra inicial del nombre 4º Numero de repeticion
 * (Comenzando por el 00) 5º @ + nombre Empresa.
 *
 * @author Marco Speranza López
 */
public class GeneradorCorreoElectronico {

    private Trabajadorbbdd trabajador;
    private ArrayList<Trabajadorbbdd> lista = new ArrayList<Trabajadorbbdd>();
    private String inicialApellido1;
    private String inicialApellido2;
    private String inicialNombre;
    private String empresa;

    public GeneradorCorreoElectronico(Trabajadorbbdd trabajador,ArrayList<Trabajadorbbdd> lista ) {

        this.trabajador = trabajador;
        this.lista = lista;
        this.inicialNombre =  limpiarDeTildes(String.valueOf(trabajador.getNombre().subSequence(0, 1))).toLowerCase() ;
        this.inicialApellido1 = limpiarDeTildes(String.valueOf( trabajador.getApellido1().subSequence(0, 1))).toLowerCase();
        this.inicialApellido2 = limpiarDeTildes(recogerApellido2().toLowerCase());
        this.empresa = trabajador.getEmpresas().getNombre().toLowerCase();

    }
    /**
     * Metodo principal del correo, por el cual se genera este.
     *          -Recoge el metodo numeroDeIguales que devuelve cuantos correos son igual al que estoy generando.
     * @return email
     */
    public String generarCorreo() {
        
        String correoElectronico = null;
        String numeroiguales = numeroDeIguales();
        correoElectronico = inicialApellido1 + inicialApellido2 + inicialNombre + numeroiguales + "@" + empresa + ".es";
        trabajador.setEmail(correoElectronico);
        return correoElectronico;
    }
/**
 * Devuelve la cadena perteneciente al segundo apellido del trabajador, comprobando si este tiene ese parametro vacio.
 * 
 * @return ""|apellido2
 */
    private String recogerApellido2() {

        if (trabajador.getApellido2().equals("")) {
            return "";
        }
        String apellido = trabajador.getApellido2();
        return apellido.substring(0, 1);
    }
    
    /**
     * Metodo privado que recorre la lista de trabajadores que ya tiene un correo, y observa si tiene o no más correos iguales
     *          -En tal caso, se sumará en 1, un apartado del nombre del correo.
     *          - Si no es el caso, el resultado que devolvera será 00.
     *          -Si es mayot que 9 el numero de correo igual, se reocge un 0 menos.
     * @return 
     */
    private String numeroDeIguales() {
        int contador = -1;

        for (Trabajadorbbdd trabajadorbbdd : lista) {
            if (trabajador.getNombre().equals(trabajadorbbdd.getNombre()) && trabajador.getApellido1().equals(trabajadorbbdd.getApellido1()) && trabajador.getApellido2().equals(trabajadorbbdd.getApellido2())) {
                contador++;
            }
        }
        System.out.println(contador);
        if (contador <= 9) {
            return "0" + String.valueOf(contador);
        }

        return String.valueOf(contador);
    }
    
    /**
     * Metodo que reocge una caden, y que la limpia de caracteres extraños, como pueden ser las tildes
     * @param s
     * @return Cadena sin tildes
     */
    public  String limpiarDeTildes(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    return s;
}

}
