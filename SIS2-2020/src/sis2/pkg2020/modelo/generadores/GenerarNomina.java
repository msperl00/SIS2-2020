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
public class GenerarNomina {

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

    public GenerarNomina(Trabajadorbbdd trabajador, String fechaNomina) {

        this.trabajador = trabajador;
        this.fechaNomina = fechaNomina;

        //Parte trabajador
        this.fechaContratacion = this.trabajador.getFechaAlta();

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
        double prorrateoextra = 0.0;
        double salarioBaseMes = trabajador.getCategorias().getSalarioBaseCategoria();
        double complementoMes = trabajador.getCategorias().getComplementoCategoria();
        double antiguedad = 0.0;

        complementoMes /= 14;
        salarioBaseMes /= 14;

        if (trienios != 0) {
            antiguedad = (Double) ExcelCrud.getMapTrienios().get(trienios);
        }
         System.out.println("PRORRATEO"+ isProrrateo());
        //Suma a los devengos del prorrateo de la extra, que en cualquier mes será 1/6.
        if (isProrrateo()) {
           
            System.out.println("trienios "+trienios);
            if (siCambioTrienioProrrateo()) { //Existe cambio de trienio en la extra que tiene que recibir.
                antiguedad = (Double) ExcelCrud.getMapTrienios().get(trienios + 1);
            }
            prorrateoextra = salarioBaseMes / 6 + complementoMes / 6 + antiguedad / 6;
            devengos += prorrateoextra;
        }

        devengos += salarioBaseMes + complementoMes + antiguedad;
        nomina.setImporteComplementoMes(complementoMes);
        nomina.setImporteSalarioMes(salarioBaseMes);
        nomina.setImporteTrienios(antiguedad);

        System.out.printf("Salario base mes: %.2f \n", salarioBaseMes);
        System.out.printf("Complemento mes: %.2f \n", complementoMes);
        System.out.printf("Antiguedad mes: %.2f \n", antiguedad);
        System.out.printf("Prorrateo extra: %.2f \n", prorrateoextra);
        System.out.printf("Bruto mensual: %.2f \n", devengos);

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
     * 1.1 Completo sin ningun cambio - SalarioBase + complementos + trienios si
     * lo tuviese-
     *
     * 2.2 Completo pero con cambio de trienio - Nº Meses correspodientes a
     * trienioviejo * valorViejotrienio + Nº de meses nuevo trienio *
     * valorNuevotrienio
     *
     * 2º Que el año este partido(Alta en año). -2.1 Con prorrateo:
     *
     * 2.1.1. Sin cambio de trienio. (Ni en ese año, ni hasta mayor del año
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

        double brutoanual;
        System.out.println("Año completo: " + siAnioCompleto());
        if (siAnioCompleto()) {
            System.out.println("Cambio de trienio" + siCambioTrienio());
            if (siCambioTrienio()) {
                    
            } else {
                brutoanual = this.brutoAnual;
            }
        }

        return 0;
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
    private boolean isProrrateo() {
/*************************************************************************/
        String valor = trabajador.getProrrata();
       // return valor.equals("SI");
       return true;
    }

    /**
     * Devuelve true o false según pertenezca la nomina al año en el que el
     * trabajador es dado de alta o no.
     *
     * @return true -> nomina en otro año | false -> nomina en mismo año que
     * dado de alta.
     */
    private boolean siAnioCompleto() {

        int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);

        return anioNomina > anioContratacion;
    }

    /**
     * Calcula si el año en el que se solicita la nomina, tiene el cambio de
     * trienio.
     *
     * @return
     */
    private boolean siCambioTrienio() {

        return true;
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
    private boolean siCambioTrienioProrrateo() {

        int mesCambio = Integer.valueOf(mesContratacion) + 1;
        int mesNomina = Integer.valueOf(this.mesNomina);
        int anioNomina = Integer.valueOf(this.anioNomina);
        int anioContratacion = Integer.valueOf(this.anioContratacion);
        int primerExtra[] = {1, 2, 3, 4, 5, 12};
        int segundoExtra[] = {6, 7, 8, 9, 10, 11};
        System.out.println("CAMBIO DE TRIENIO?");
        //Compruba si esta en el año del trienio.
        if ((anioNomina - anioContratacion) % 3 == 0) {

            //Comprueba si es de la primera o seguna extra.
            if (mesNomina >= 6 && mesNomina <= 11) {

                for (int i : segundoExtra) {

                    if (i == mesCambio) 
                        return true;
                    
                }
            } else {
                for (int i : primerExtra) {

                    if (i == mesCambio) 
                        return true;
                    
                }
            }

        }

        return false;
    }
    
    /**
     * Devuelve el número de trienios reales de un trabajador.
     * @param mesNomina
     * @param mesContratacion
     * @param aniosEmpresa
     * @return 
     */
    private int getTrienios(int mesNomina, int mesContratacion, int aniosEmpresa) {

        int trienios = aniosEmpresa / 3;
        if(mesNomina <= mesContratacion && trienios != 0){
            trienios -= 1;
        }
        
        return trienios;
    }

}
