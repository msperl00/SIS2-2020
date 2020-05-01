package sis2.pkg2020.modelo.operaciones;

import java.awt.BorderLayout;
import sis2.pkg2020.modelo.Trabajadorbbdd;

/**
 *
 * @author Marco Speranza López
 *
 * Esta clase será la encargada de de verificar el codigo de control de una
 * cuenta bancaria. Recogera el IBAN y el pais procedente de la cuenta bancria.
 *
 * Si la cuenta es invalida, sera necesario actualizar su valor. Se generará un
 * error XML "erroresCCC.xml" si se encuentra un error. -Nodo cuentas -Elementos
 * cuenta, con atributo id numero. -Nombre, apelldios, empresa, codigo de cuenta
 * erroneo e IBAN correcto.
 *
 * CCC -> 20 Digitos en 4 bloques distintos EEEE OOOO DD NNNNNNNNNN.
 *
 * 1PT -> 4 Digitos que representan la entidad (Nº de registro de entidades del
 * primero de España) 2PT -> 4 Digitos que representan la sucursal bancaria. 3PT
 * -> 2 Digitos que representan el control. 4PT -> 10 Digitos que identifican la
 * cuenta univocamente
 *
 */
public class GeneradorIBAN {

    private String entidad;
    private String idOficina;
    private String parControl;
    private String parControlcalculado;
    private String cuenta;
    private String ccc;

    Trabajadorbbdd trabajador;

    public GeneradorIBAN(Trabajadorbbdd trabajador) {

        this.trabajador = trabajador;
        this.ccc = trabajador.getCodigoCuenta();
        //   System.out.println(toString());

    }

    public GeneradorIBAN(String ccc) {
        this.ccc = ccc;

    }

    /**
     * Recogemos los valores en posiciones 9 y 10, los cuales se generan
     * pormedio de los demas codigos del ccc -> Así comprobaremos la validez del
     * mismo. 1º Entidad y Oficina 2º Numero de cuenta
     *
     * Mismo algoritmo ejecutado por separado.
     *
     * 1ºNecesario añadir dos 00 al principio de la entidad + oficina, para
     * tener 10 valores como el numero de cuenta.
     *
     * 2º Mutiplicacion de los valores segun la posicion de izq a derecha que
     * tengan. 1,2,4,8,5,10,9,7,3,6 -> 2^n mod(11) entre 0 y 10.
     *
     * 3º Estos prodcutos son sumados, y depues se obtiene el resto de este
     * sumando, dividido entre 11, que serie el modulo, por tanto de este.
     *
     * 4º El resto obtenido se resta por 11, para obtener el digito de control.
     *
     * -Si este valor fuese 10 -> 1 y si fuese 11 -> 0
     *
     * @return parDeControlCorregido
     */
    private String calcularParCtl() {

        String primero = "00" + entidad + idOficina;
        String parControl = null;
        //Primer ctl
        int calculo = operacionSumaCtl(primero);
        int auxctl = 11 - (calculo % 11);
        parControl = digitoControl(auxctl);
        //Segundo ctl
        calculo = operacionSumaCtl(cuenta);
        auxctl = 11 - (calculo % 11);
        parControl += digitoControl(auxctl);

        return parControl;
    }

    @Override
    public String toString() {
        return trabajador.getNombre() + " {" + "entidad=" + entidad + ", idOficina=" + idOficina + ", parControl=" + parControl + ", cuenta=" + cuenta + '}';
    }

    /**
     * Comporbamos que la cuenta bancaria este comprendida en el tamaño
     * correspodiente a las cuentas bancarias ES -> 20 digitos.
     *
     * -Si lo es seteamos los valores correspodientes.
     *
     * Es el primero paso para seguir con el algoritmo.
     *
     * @return true|false
     */
    public boolean cccIsValida() {

        if (!(ccc.length() == 20)) {
            return false;
        }

        this.entidad = ccc.substring(0, 4);
        this.idOficina = ccc.substring(4, 8);
        this.parControl = ccc.substring(8, 10);
        this.cuenta = ccc.substring(10);

        return true;
    }

    /**
     * Metodo que devuelve en forma de string el valor correspdiente al
     * resultado despues de aplicar el algoritmo el digito de Control
     *
     * @param auxctl
     * @return
     */
    private String digitoControl(int auxctl) {

        if (auxctl == 11) {
            return "0";
        } else if (auxctl == 10) {

            return "1";
        }

        return Integer.toString(auxctl);

    }

    /**
     * Devuelve el valor de la suma del metodo de cuenta Española para 20
     * digitos
     *
     * @param primero
     * @return
     */
    private int operacionSumaCtl(String primero) {

        return (Integer.parseInt(primero.substring(0, 1))
                + (Integer.parseInt(primero.substring(1, 2)) * 2)
                + (Integer.parseInt(primero.substring(2, 3)) * 4)
                + (Integer.parseInt(primero.substring(3, 4)) * 8)
                + (Integer.parseInt(primero.substring(4, 5)) * 5)
                + (Integer.parseInt(primero.substring(5, 6)) * 10)
                + (Integer.parseInt(primero.substring(6, 7)) * 9)
                + (Integer.parseInt(primero.substring(7, 8)) * 7)
                + (Integer.parseInt(primero.substring(8, 9)) * 3)
                + (Integer.parseInt(primero.substring(9, 10)) * 6));
    }

    /**
     * Metodo publico que devuelve el par de control calculado
     *
     * @return
     */
    public String getParControlCalculado() {

        if (parControlcalculado == null) {
            return calcularParCtl();
        }
        return parControlcalculado;
    }

    /**
     * Metodo public que devueve el parde control sin calcular
     *
     * @return
     */
    public String getParControlSinCalcular() {

        if (parControl != null) {
            return parControl;
        }

        return null;
    }

    /**
     * Compara los dos pares de control, para comporbar su verificacion de par
     * de controls
     *
     * @param parCalculado
     * @param parSinCalcular
     * @return true|false segun la comparación.
     */
    private boolean compararParCtrl(String parCalculado, String parSinCalcular) {

        //System.out.println(parCalculado + " VS " + parSinCalcular);

        if (parCalculado.equals(parSinCalcular)) {
            return true;
        }
        return false;
    }

    /**
     * Devuelve el CCC comprobado y calculado.
     *
     * @return el ccc con los digitos de control correctos.
     */
    public String cccValidado() {
       // System.out.println("Antiguo: "+ccc+" VS "+ "Nuevo: "+entidad + idOficina + parControlcalculado + cuenta);
                
        return entidad + idOficina + parControlcalculado + cuenta;
    }

    public boolean parControlIsValido() {
        if (this.parControlcalculado == null) {
            parControlcalculado = getParControlCalculado();
        }

        return compararParCtrl(parControlcalculado, parControl);

    }
   
    private String generarIBAN(){
        
        
        return null;
    }
    
    /**
     * Metodo publico que devuelve el IBAN generado correctamente, tras verificar
     * previamente que el digito de control es erroneo.
     * @return 
     */
    public String getNewIBAN(){
        
        return generarIBAN();
    }

}
