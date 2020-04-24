package sis2.pkg2020.modelo;

import com.sun.xml.internal.ws.developer.Serialization;

/**
 * Clase que se encarga de la validación y correción de los errores en el nif y
 * en el nie del trabajador
 *
 * @author Marco Speranza López
 */
public class CalcularNIFNIE {

    private final String letrasNie = "XYZ";
    private final String letrasNif = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String numeros = "0123456789";
    private ModeloXML modelo;
    private String nifnie;
    private Trabajadorbbdd trabajador;
    public CalcularNIFNIE(Trabajadorbbdd trabajador, ModeloXML modelo) {

        this.trabajador = new Trabajadorbbdd(trabajador);
        this.nifnie = this.trabajador.getNifnie();
        this.modelo = modelo;
       
        
        
    }
    /**
     * Validamos el código del trabajador que hemos recibido en el constructor.
     * Si es vacio -> Añadir a en blanco
     * Si es duplicado -> Añadimos a duplicados
     * Si no es ni vacio ni duplicado -> Pasamos a realizar las comporbaciones pertinentes.
     * @return boolean
     */
    public boolean validar() {
        
        if (!nifnie.equals("")) {
            
            if(isNIE(nifnie.substring(0, 1))) {
                System.out.println("Es NIE");
                 String correcto = calcularNIE(nifnie);
                if(correcto.equals(nifnie)){
                    System.out.println("Correcto");
                }else{
                    System.out.println("Incorrecto");

                }
                
            }else if(isNIF(nifnie.substring(0, 1))){
                System.out.println("ES NIF");
                 String correcto = calcularNIF(nifnie);
                 if(correcto.equals(nifnie)){
                    System.out.println("Correcto");
                }else{
                    System.out.println("Incorrecto");

                }
            }else{
                System.out.println("Ninguno de los dos");
            }
               
            

        }else{
            //Si es vacio, lo añado a errores de nif vacios.
            
            modelo.addBlanco(trabajador);
            System.out.println("Añadiendo vacio en fila "+ trabajador.getFilaExcel());
        }

        return false;
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


    private String calcularNIF(String nifnie) {
        String prueba = nifnie;
        if (prueba.length() == 9) {
            prueba = prueba.substring(0, prueba.length() - 1);

        }
        return prueba + calcularLetra(prueba);
    }

    private String calcularNIE(String nifnie) {
        
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

        return devolver + calcularLetra(prueba);
    }

    /**
     * Calcula la letra correspodiente a una cadena que se le pasa, segun el
     * agoritmo de validacion del codigo fiscal
     *
     * @param prueba
     * @return
     */
    private char calcularLetra(String prueba) {
        return letrasNif.charAt(Integer.parseInt(prueba) % 23);
    }
}
