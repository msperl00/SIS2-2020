package sis2.pkg2020.modelo.operaciones;

import java.awt.BorderLayout;
import java.math.BigInteger;
import sis2.pkg2020.modelo.Trabajadorbbdd;
import sis2.pkg2020.modelo.enums.LetrasIBAN;

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
                
        return entidad + idOficina + parControlcalculado + cuenta;
    }

    public boolean parControlIsValido() {
        if (this.parControlcalculado == null) {
            parControlcalculado = getParControlCalculado();
        }

        return compararParCtrl(parControlcalculado, parControl);

    }
   /**
    * Este metodo reproduce la operacion del calculo del IBAN, el cual se obtiene
    * con la cuenta bancaria del trabajador, y con el pais de origen de la misma.
    * 
    *           1º El codigo IBAN comienza por 2 letras segun -> PAIS de origen
    *           2º Le sigue el par de digitos de control calculado anteriormente.
    *           3º Le siguenn los 20 digitos del ccc de la cuenta bancaria.
    * 
    *           Conclusion: 24 posiciones, solo para la realizacion de cuentas bancarias con 20 digitos.
    * 
    *   Proceso:
    * 
    *       1º Crear codigo
    *               ES + "00" + CCC
    *       2º Transfromamos la cadena.
    *           CCC + ES00
    *       3º Transofrmamos las letras de los paises en valores numericos.
    * 
    *           E = 14 y S = 28
    * 
    *       4º n mod 97 -> resto de la division de la cadena en valor numerico
    * 
    *       5º Restamos el valor
    * 
    *       6ºAsí obtenemos el valor de 2 cifras que seguira al ES.
    *           
    *               -Si el valor es de 1 cifra, le anteponemos un 0.
    * 
    * 
    * @return String que contiene el numero IBAN correcto segun la cuenta bancaria.
    */
    public String generarIBAN(String codigoCuenta){
        
        String paisCCC = trabajador.getPaisCCC();
        
        String transformada = codigoCuenta+ pesoLetra(paisCCC.charAt(0))+pesoLetra(paisCCC.charAt(1))+"00";
        
         BigInteger ccc = new BigInteger(transformada);
        BigInteger valormodulo = new BigInteger("97");
        ccc = ccc.mod(valormodulo);
        int calculoresta = 98 - ccc.intValue();
        
        String solucion = null;
        if(calculoresta <= 9){
            solucion = paisCCC+"0"+Integer.toString(calculoresta)+codigoCuenta;
        }else{
            solucion = paisCCC+Integer.toString(calculoresta)+codigoCuenta;
        }
        
        return solucion ;
    }

    private String pesoLetra(char letra) {
        String peso = null;
      switch(letra){
          case 'A': peso = "10";
                break;
            case 'B': peso = "11";
                break;
            case 'C': peso = "12";
                break;
            case 'D': peso = "13";
                break;
            case 'E': peso = "14";
                break;
            case 'F': peso = "15";
                break;
            case 'G': peso = "16";
                break;
            case 'H': peso = "17";
                break;
            case 'I': peso = "18";
                break;
            case 'J': peso = "19";
                break;
            case 'K': peso = "20";
                break;
            case 'L': peso = "21";
                break;
            case 'M': peso = "22";
                break;
            case 'N': peso = "23";
                break;
            case 'O': peso = "24";
                break;
            case 'P': peso = "25";
                break;
            case 'Q': peso = "26";
                break;
            case 'R': peso = "27";
                break;
            case 'S': peso = "28";
                break;
            case 'T': peso = "29";
                break;
            case 'U': peso = "30";
                break;
            case 'V': peso = "31";
                break;
            case 'W': peso = "32";
                break;
            case 'X': peso = "33";
                break;
            case 'Y': peso = "34";
                break;
            case 'Z': peso = "35";
      }

        return peso;
    }
    

  

}
