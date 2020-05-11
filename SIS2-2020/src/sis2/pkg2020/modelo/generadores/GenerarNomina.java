
package sis2.pkg2020.modelo.generadores;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import sis2.pkg2020.modelo.Trabajadorbbdd;

/**
 *
 * Clase que generará en particular cada nomina del trabajador correspodiente.
 * 
 *  Aqui se dispondrá de todos los valores pertenecientes al trabajador, que sean necesarios
 *  para generar la nómina.
 * @author Marco Speranza López
 */
public class GenerarNomina {

    private Trabajadorbbdd trabajador;
    private String fechaNomina;
    private Date fechaContratacion;
    private String mesContratacion;
    private String anioContratacion;
    private String mesNomina;
    private String anioNomina;
    public GenerarNomina(Trabajadorbbdd trabajador, String fechaNomina) {
        this.trabajador = trabajador;
        this.fechaNomina = fechaNomina;
        //Parte trabajador
        this.fechaContratacion = this.trabajador.getFechaAlta();
                
        System.out.println(siGenerarNomina());
    }
    
    /**
     * Metodo inicial sobre la nomina del trabajador, que calcula, si este trabajador
     * se ha dado de alta en el valor temporal que se ha seleccionado.
     *      1º Comprobaremos el año
     *      2º El mes
     * @return 
     */
    private boolean siGenerarNomina(){
        DateFormat date = new SimpleDateFormat("MM/yyyy");
        String strDate = date.format(fechaContratacion);
        this.mesContratacion = strDate.substring(0, 2);
        this.anioContratacion = strDate.substring(3, 7);
        this.mesNomina = this.fechaNomina.substring(0, 2);
        this.anioNomina = this.fechaNomina.substring(3, 7);
        
        System.out.println("Fecha de contratación: "+strDate);
        System.out.println("Fecha de recogida de nomina: "+fechaNomina);
        //Año nomina siempre tiene que ser mayor o igual
        int intanioContratacion = Integer.valueOf(anioContratacion);
        int intanioNomina = Integer.valueOf(anioNomina);
        
        int intmesContratacion = Integer.valueOf(mesContratacion);
        int intmesNomina = Integer.valueOf(mesNomina);
        
        int aniosEmpresa = intanioNomina-intanioContratacion;
        int mesEmpresa = intmesNomina - intmesContratacion;
        //Falso
        if(aniosEmpresa < 0){
            return false;
         
            //Valido parcial
        }else if(aniosEmpresa == 0){
            //Falso mensual
            if(mesEmpresa <= 0){
                return false;
            //Valido mensual
            }
         //Valido total
        }
 
        return true;
    }
    
}
