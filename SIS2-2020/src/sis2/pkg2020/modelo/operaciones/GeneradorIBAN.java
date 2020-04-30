
package sis2.pkg2020.modelo.operaciones;

/**
 *
 * @author Marco Speranza López
 * 
 * Esta clase será la encargada de de verificar el codigo de control de una cuenta bancaria.
 * Recogera el IBAN  y el pais procedente de la cuenta bancria.
 * 
 * Si la cuenta es invalida, sera necesario actualizar su valor.
 * Se generará un error XML "erroresCCC.xml" si se encuentra un error.
 *          -Nodo cuentas
 *          -Elementos  cuenta, con atributo id numero.
 *          -Nombre, apelldios, empresa, codigo de cuenta erroneo e IBAN correcto.
 */
public class GeneradorIBAN {
    
    public GeneradorIBAN(){
        
    }
    
}
