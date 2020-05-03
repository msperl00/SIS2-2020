package sis2.pkg2020.modelo.enums;

/**
 *
 * @author Marco Speranza López
 */
public enum TipoFilasHoja4 {

    CUOTA_OBRERA_GENERAL_TRABAJADOR("Cuota obrera generaltrabajador"),
    CUOTA_DESEMPLEO_TRABAJADOR("Cuota desemplo trabajador"),
    CUOTA_FORMACION_TRABAJADOR("Cuota formacion trabajador"),
    CONTINGENICIAS_COMUNES_EMPRESARIO(" CONTINGENCIAS COMUNES empresario"),
    FOGASA_EMPRESARIO("FOGASA empresario"),
    DESEMPLO_EMPRESARIO("DESEMPLEO empresario"),
    FORMACION_EMPRESARIO("FORMACION empresario"),
    ACCIDENTES_TRABAJO_EMPRESARIO("ACCIDENTES TRABAJO empresario");

    private String nombre;

    private TipoFilasHoja4(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre(){
        return nombre;
    }
}
