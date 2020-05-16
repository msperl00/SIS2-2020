package sis2.pkg2020.modelo.generadores;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import sis2.pkg2020.controlador.ExcelCrud;
import sis2.pkg2020.modelo.Categorias;
import sis2.pkg2020.modelo.Nomina;
import sis2.pkg2020.modelo.Trabajadorbbdd;

/**
 *
 * Clase que generará en particular cada nomina del trabajador correspodiente.
 *
 * Aqui se dispondrá de todos los valores pertenecientes al trabajador, que sean
 * necesarios para generar la nómina.
 *
 * A lo largo de los calculos, iremos asignando los valores al objeto de la
 * clase Nomina, cuya finalidad tiene pasarle el objeto a la clase que genera la
 * nómina en pdf.
 *
 * @author Marco Speranza López
 */
public class GeneradorNomina {

    //Nomina
    Nomina nomina;
    // Nomina básci,a luego asignaremos más nominas si fuesen sin prorratear.
    private Trabajadorbbdd trabajador;
    private String fechaNomina;
    private Date fechaContratacion;
    private String mesContratacion;
    private String anioContratacion;
    private String mesNomina;
    private String anioNomina;
    private int trienios;
    private Categorias categoria;
    private double brutoAnual;
    private double brutoMensual;
    private double deducciones;
    private double devengos;
    private double salarioBase;
    private double complemento;
    private double prorrateoextra;
    private double antiguedad;

    public GeneradorNomina(Trabajadorbbdd trabajador, String fechaNomina) {

        this.trabajador = trabajador;
        this.fechaNomina = fechaNomina;

        //Parte trabajador
        this.fechaContratacion = this.trabajador.getFechaAlta();
        this.salarioBase = trabajador.getCategorias().getSalarioBaseCategoria();
        this.complemento = trabajador.getCategorias().getComplementoCategoria();
        this.prorrateoextra = 0.0;
        this.antiguedad = 0.0;
    }

    /**
     * Metodo controlador de la generación de la nomina por partes.
     *
     * 1º Comporbamos si se puede generar la nómina del siguiente trabajador. 2º
     * Calculamos la antiguedad del trabajdor -> Numero de trienios. Ya
     * correspodiente al primer metodo. 3º Calcular el bruto anual teniendo en
     * cuenta la opción de prorrateo.
     *
     */
    public boolean generarNomina() {

        if (!siGenerarNomina()) {
            return false;
        }

        this.devengos = calcularDevengos();
        this.brutoAnual = calcularBrutoAnual();
        //  Para calcular las deducciones debemos saber el bruto anual -> IRPF.
        this.deducciones = cacularDeduciones();

        return true;
    }

    /**
     * Metodo inicial sobre la nomina del trabajador, que calcula, si este
     * trabajador se ha dado de alta en el valor temporal que se ha
     * seleccionado. 1º Comprobaremos el año 2º El mes
     *
     * @return
     */
    private boolean siGenerarNomina() {
        DateFormat date = new SimpleDateFormat("MM/yyyy");
        String strDate = date.format(fechaContratacion);
        this.mesContratacion = strDate.substring(0, 2);
        this.anioContratacion = strDate.substring(3, 7);
        this.mesNomina = this.fechaNomina.substring(0, 2);
        this.anioNomina = this.fechaNomina.substring(3, 7);

        System.out.println("Fecha de contratación del trabajador: " + strDate);
        System.out.println("Fecha de recogida de nomina: " + fechaNomina);
        //Año nomina siempre tiene que ser mayor o igual
        int intanioContratacion = Integer.valueOf(anioContratacion);
        int intanioNomina = Integer.valueOf(anioNomina);

        int intmesContratacion = Integer.valueOf(mesContratacion);
        int intmesNomina = Integer.valueOf(mesNomina);

        int aniosEmpresa = intanioNomina - intanioContratacion;
        int mesEmpresa = intmesNomina - intmesContratacion;
        //Falso
        if (aniosEmpresa < 0) {
            return false;

            //Valido parcial
        } else if (aniosEmpresa == 0) {
            //Falso mensual
            if (mesEmpresa <= 0) {
                return false;
                //Valido mensual
            }
            //Valido total
        }

        //Este trienio solo vale si esta comprendido en un rango que no cruce con otro trienio
        trienios = getTrienios(intmesNomina, intmesContratacion, aniosEmpresa);
        //Recogemos datos calculados de la nomina.
        nomina = new Nomina();
        nomina.setMes(getMesNomina(mesNomina));
        nomina.setAnio(Integer.valueOf(anioNomina));
        nomina.setNumeroTrienios(trienios);
        return true;
    }

    /**
     * Cacula el bruto mensual mediante el calculo de los devengos,
     * pertenecientes a valores como:
     *
     * Salario base + prorrateo + complemento + antiguedad.
     *
     * El bruto mensual:
     *
     * 1.Sin prorrateo:
     *
     * 2.Con prorrateo 1.1 Comporbamos si exsite un cambio de trienio en el mes
     * que se tiene que calcular. 1.2 Si no existe un cambio de trienio ->
     * Calculo normal.
     *
     * @return Devolver el bruto mensual == devengos
     */
    private double calcularDevengos() {

        double devengos = 0.0;
        double salarioBaseMes = this.salarioBase;
        double complementoMes = this.complemento;
        complementoMes /= 14;
        salarioBaseMes /= 14;

        if (trienios != 0) {
            antiguedad = (Double) ExcelCrud.getMapTrienios().get(trienios);
        }

        //Suma a los devengos del prorrateo de la extra, que en cualquier mes será 1/6.
        if (siProrrateo()) {
            //Comprueba si existe cambio de trienio en la extra que tiene que recibir.
            //Comprobación del mes de diciembre.
            if (siCambioTrienioProrrata()) {
                antiguedad = getValorTrienio(trienios + 1);
            
            }
            prorrateoextra = salarioBaseMes / 6 + complementoMes / 6 + antiguedad / 6;
            devengos += prorrateoextra;
        }

        devengos += salarioBaseMes + complementoMes + antiguedad;
        nomina.setImporteComplementoMes(complementoMes);
        nomina.setImporteSalarioMes(salarioBaseMes);
        nomina.setImporteTrienios(antiguedad);
        nomina.setValorProrrateo(prorrateoextra);
        nomina.setNumeroTrienios(trienios);

        System.out.printf("Salario base mes: %.2f \n", salarioBaseMes);
        System.out.printf("Complemento mes: %.2f \n", complementoMes);
        System.out.printf("Antiguedad mes: %.2f \n", antiguedad);
        System.out.printf("Prorrateo extra: %.2f \n", prorrateoextra);
        System.out.printf("Devengos/Bruto mensual: %.2f \n", devengos);

        return devengos;
    }

    //TODO
    private double cacularDeduciones() {

        double contingenciasGenerales;
        double desempleo;
        double cuotaFormacion;
        double IRPF = calcularIRPF();

        return 0;
    }
    /**
     * Devuelve el valor del mes de la nomina correspondeinte a como es la entrada
     * del mes según la expresion regular mm/aaaa.
     * @param mesNomina
     * @return 
     */
    private int getMesNomina(String mesNomina) {

        int valor = 0;
        switch (mesNomina) {
            case "01":// System.out.println("Enero");
                valor = 1;
                break;

            case "02":// System.out.println("Febrero");
                valor = 2;
                break;

            case "03":// System.out.println("Marzo");
                valor = 3;

                break;

            case "04": //System.out.println("Abril");
                valor = 4;
                break;

            case "05": //System.out.println("Mayo");
                valor = 5;
                break;

            case "06":// System.out.println("Junio");
                valor = 6;
                break;

            case "07":// System.out.println("Julio");
                valor = 7;
                break;

            case "08":// System.out.println("Agosto");
                valor = 8;
                break;

            case "09":// System.out.println("Septiembre");
                valor = 9;
                break;

            case "10": //System.out.println("Octubre");
                valor = 10;

                break;

            case "11": //System.out.println("Noviembre");
                valor = 11;
                break;

            case "12": // System.out.println("Diciembre");
                valor = 12;
                break;
        }
        return valor;

    }

    /**
     * Metodo que calcula el bruto anual según su prorrateo o no.
     *
     * Existen diversas casuisticas:
     *
     * 1º Que el año sea completo, por lo tanto, el bruto anual sera:
     *
     *                  *El prorrateo no es relevante en este cuadro
     *
     * 1.1 Completo sin ningun cambio - SalarioBase + complementos + trienios
     * (Si lo tuviese)
     *
     *
     * 2.2 Completo pero con cambio de trienio
     *
     * - Nº Meses correspodientes trienioviejo * valorViejotrienio + Nº de meses
     * nuevo trienio valorNuevotrienio
     *
     * 2º Que el año este partido(Alta en año).
     *
     * -2.1 Con prorrateo:
     *
     * 2.1.1. Sin extra añadido (Ni en ese año, ni hasta mayor del año
     * siguiente, incluido) ((SalarioBase + complementos+trienios)/ 12) * Nºde
     * meses restantes
     *
     * -2.2 Sin prorrateo:
     *
     * 2.2.1. Nomina de los meses pertenecites a los primeros 6 meses restantes
     * (Si los hubiese) más la extra de Junio dividida en el mismo numero de
     * meses -1- + Nomina perteneciente a los siguientes 6 meses restantes más
     * la extra de Diciembre dividida por el mismo numero de meses. (La extra en
     * este caso, podria ser completa, si se dio de alta en febrero)
     *
     *
     * @return
     */
    private double calcularBrutoAnual() {

        //Caso base -> Año completo sin cambio de trienio.
        double brutoanual = salarioBase + complemento + (antiguedad) * 14;
        System.out.println("Año completo: " + siAnioCompleto());
        System.out.println(brutoanual);
        //Caso 1 -> Año completo pero con cambio de trienio.

        if (siAnioCompleto()) {
            System.out.println("Si cambio trienio: " + siCambioTrienioEseAnio());
            //Caso 1.1 Cambio de trienio durante el año.
            if (siCambioTrienioEseAnio()) {
                int nMesesViejos = getNumeroMesContratacion();
                int mesNomina = getValorMesNomina();
                double antiguedadNueva = getValorTrienio(trienios + 1);
                int aux = 12 - nMesesViejos;
                brutoanual = salarioBase + complemento + (antiguedad * nMesesViejos) + (antiguedadNueva * aux);
             
            //Caso 1.2 Prorrata de diciembre -> SI existe un cambio de trienio en el año siguiente, la nomina de diciembre incluirá 1/6 de la nomina extra de Junio.
            }else if(siCambioTrienioAnioSiguiente() && siProrrateo()){
                
               brutoanual += getValorTrienio(trienios+1)/6;
                
            }
            //Caso 2 -> Trabajador recien contratado -> No influye la antiguedad.
        } else {
                int nNominas = 12 - (getNumeroMesContratacion()-1);
                //Con prorrateo, indica que la nomina se divide en el nº de meses que este el trabajador ese año en la empresa.
            if (siProrrateo()) {
                
                        brutoanual = salarioBase + complemento;
                        brutoanual /= 12;
                        // Lo multiplicamos por el nº de meses que esta en la empresa ese año.
                        brutoanual *=nNominas;
                       System.out.printf("Bruto anual con prorrateo: %.2f \n", brutoanual);

            } else {
                
                //Si tiene más de 6 meses de nomina, implica que l nomina de diciembre se cobra integra.
                int nExtrasCompletas = (nNominas >= 6) ? 1 : 0;
                //Si es 0 tendremos que calcular el valor  de nomina que le corresponde.
                double nExtrasIncompletas = 0;
                //EXTRA DE JUNIO O DE DICIEMBRE.
                if (nExtrasCompletas == 0) {
                    nExtrasIncompletas = nNominas / 6;
                } 

                //14 Nominas en total -> Vamos restando.
                int nNominasCompletas = (nNominas + nExtrasCompletas);
                
                //Suma de nominas completas
                brutoanual = (salarioBase + complemento) / 14;
                brutoanual *= nNominasCompletas;
                

                //Suma de nominas parciales
                brutoanual += ((salarioBase + complemento) / 14) * (nExtrasIncompletas);
            }
            System.out.println("Año no completo");

        }
       System.out.printf("Bruto anual sin prorrateo: %.2f \n", brutoanual);
        return brutoanual;
    }

    /**
     * Se calcula según el bruto anual de dicho trabajador.
     *
     * @return
     */
    private double calcularIRPF() {

        double irpf = 0;

        return irpf;
    }

    /**
     * Devuelve el boolean corespodiente al si o no del prorrateo.
     *
     * @return true -> SI | false -> NO
     */
    private boolean siProrrateo() {

        String valor = trabajador.getProrrata();
        return valor.equals("SI");

    }

    /**
     * Devuelve true o false según pertenezca la nomina al año en el que el
     * trabajador es dado de alta o no.
     *
     * 1. Existe la posibilidad, de que esten en distitnos años, pero que el
     * valor de el mes de nomina requerido sea 12, por lo tanto, se valorará
     * como si no fuese el año completo, ya que ese mes contará para el valor de
     * la prorrata del extra del año siguiente.
     *
     * @return true -> nomina en otro año | false -> nomina en mismo año que
     * dado de alta.
     */
    private boolean siAnioCompleto() {

        int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);

        if (anioNomina > anioContratacion) {
            
            return true;

        } else if (anioNomina == anioContratacion) {
            if (getNumeroMesContratacion() == 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Busca si existe en una nomina prorrateada un cambio de trienio. Esto
     * quiere decir, que si yo cobro la nomina en Octubre, y en noviembre hay un
     * cambio de trienio, se cobre la prorrata extra con la nueva antiguedad.
     *
     * Si el mes del cambio esta comprendio dentro de los proximos 6 meses, esto
     * quiere decir que tiene que recoger una extra de antiguedad nueva. Sin
     * embargo, hay que tener cuenta en que meses estamos.
     *
     * @return
     */
    private boolean siCambioTrienioProrrata() {

        int mesCambio = Integer.valueOf(mesContratacion) + 1;
        int mesNomina = Integer.valueOf(this.mesNomina);
        int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);
        int primerExtra[] = {1, 2, 3, 4, 5, 12};
        int segundoExtra[] = {6, 7, 8, 9, 10, 11};
        System.out.println("CAMBIO DE TRIENIO?");
        //Compruba si esta en el año del trienio.
        if ((anioNomina - anioContratacion) % 3 == 0) {
            //Si el mes del cambio y el de la nomina coinciden signficia que ya puede empezar a cobrar el trienio.
            if (mesCambio <= mesNomina) {
                //Comprueba si es de la primera o segunda extra.
                if (siPrimerExtra(mesNomina)) {
                    //Comprueba si es un mes de cambio
                    if (dentroSemestre(segundoExtra, mesCambio)) {
                        return true;
                    }
                } else {

                    if (dentroSemestre(primerExtra, mesCambio)) {
                        return true;
                    }
                }
            }
        }else if(siCambioTrienioAnioSiguiente()){
            System.out.println("Cambio de trienio al año suguiente.");
                    return true;
        }

        return false;
    }

    /**
     * Devuelve el número de trienios reales de un trabajador.
     *
     * @param mesNomina
     * @param mesContratacion
     * @param aniosEmpresa
     * @return
     */
    private int getTrienios(int mesNomina, int mesContratacion, int aniosEmpresa) {

        int trienios = aniosEmpresa / 3;
        if (mesNomina <= mesContratacion && trienios != 0) {
            trienios -= 1;
        }

        return trienios;
    }

    /**
     * Comprubea si esta dentro del primer semestre o no, y por ende dentro del
     * segundo
     *
     * @param mesNomina
     * @return
     */
    private boolean siPrimerExtra(int mesNomina) {
        return mesNomina >= 6 && mesNomina <= 11;
    }

    /**
     * Comlemento la comprobación del semestra.
     *
     * @param semestre
     * @param mesCambio
     * @return
     */
    private boolean dentroSemestre(int[] semestre, int mesCambio) {
        for (int i : semestre) {

            if (i == mesCambio) {
                return true;
            }

        }
        return false;
    }

    /**
     * Comprueba si dentro del año del bruto, hay un cambio de trienio.
     *
     * @return
     */
    private boolean siCambioTrienioEseAnio() {

        int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);

        return (anioNomina - anioContratacion) % 3 == 0;
    }

    /**
     * Devuelve del excel el valor del trienio con respecto al del trabajador.
     *
     * @param trienios
     * @return
     */
    private double getValorTrienio(int trienios) {

        return (Double) ExcelCrud.getMapTrienios().get(trienios);
    }

    /**
     * Devuelve en forma de integer el valor de la nomina.
     *
     * @return
     */
    private int getValorMesNomina() {
        return Integer.valueOf(this.mesNomina);
    }

    /**
     * Devuelve como integer el mes de contratación del trabajador.
     *
     * @return
     */
    private int getNumeroMesContratacion() {
        return Integer.valueOf(this.mesContratacion);
    }

    private boolean siCambioTrienioAnioSiguiente() {
         int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);

       anioNomina++;
       
        return (anioNomina - anioContratacion) % 3 == 0;
        
    }

}
