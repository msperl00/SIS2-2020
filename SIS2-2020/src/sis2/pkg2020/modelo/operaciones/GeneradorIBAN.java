
package sis2.pkg2020.modelo.operaciones;

import sis2.pkg2020.modelo.Trabajadorbbdd;

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
 * 
 * CCC -> 20 Digitos en 4 bloques distintos
 * EEEE OOOO DD NNNNNNNNNN.
 * 
 * 1PT -> 4 Digitos que representan la entidad (Nº de registro de entidades del Banco de España)
 * 2PT -> 4 Digitos que representan la sucursal bancaria.
 * 3PT -> 2 Digitos que representan el control.
 * 4PT -> 10 Digitos que identifican la cuenta univocamente
 * 
 */
public class GeneradorIBAN {
    
    private String entidad;
    private String idOficina;
    private String parControl;
    private String cuenta;
    
    private String ccc;
    Trabajadorbbdd trabajador;
    public GeneradorIBAN(Trabajadorbbdd trabajador){
        
        this.trabajador = trabajador;
        this.ccc = trabajador.getCodigoCuenta();
        this.entidad = ccc.substring(0, 4);
        this.parControl = ccc.substring(8, 10);
        this.idOficina = ccc.substring(4,8);
        this.cuenta = ccc.substring(10);
        System.out.println(toString());
        
    }
    
    /**
     * Recogemos los valores en posiciones 9 y 10, los cuales se generan pormedio
     * de los demas codigos del ccc -> Así comprobaremos la validez del mismo.
     *      1º Entidad y Oficina
     *      2º Numero de cuenta
     * 
     * Mismo algoritmo ejecutado por separado.
     * @return parDeControlCorregido
     */
    public String calcularParCtl(){
        
       return null;
    }

    @Override
    public String toString() {
        return trabajador.getNombre()+ " {" + "entidad=" + entidad + ", idOficina=" + idOficina + ", parControl=" + parControl + ", cuenta=" + cuenta + '}';
    }
    
    
    
}
