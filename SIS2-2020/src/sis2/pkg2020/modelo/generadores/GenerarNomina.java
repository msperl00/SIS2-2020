
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
    public GenerarNomina(Trabajadorbbdd trabajador, String fechaNomina) {
        this.trabajador = trabajador;
        this.fechaNomina = fechaNomina;
        //Parte trabajador
        this.fechaContratacion = this.trabajador.getFechaAlta();
        siGenerarNomina();
    }
    
    /**
     * Metodo inicial sobre la nomina del trabajador, que calcula, si este trabajador
     * se ha dado de alta en el valor temporal que se ha seleccionado.
     * @return 
     */
    private boolean siGenerarNomina(){
        DateFormat date = new SimpleDateFormat("MM/yyyy");
        String strDate = date.format(fechaContratacion);
        System.out.println("Fecha de contratación: "+strDate);
        System.out.println("Fecha de recogida: "+fechaNomina);

        
       
        return false;
    }
    
}
