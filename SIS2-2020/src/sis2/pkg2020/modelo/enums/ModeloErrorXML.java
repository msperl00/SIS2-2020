/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo.enums;

/**
 *
 * @author Marco Speranza López
 */
public enum ModeloErrorXML {

    NIF_NIE("Errores.xml"), IBAN("erroresCCC.xml");

    private final String nombre_archivo;

    

    private ModeloErrorXML(String nombre){
        this.nombre_archivo = nombre;
        
    }
    
    public String getNombreFicheroError(){
        
        return nombre_archivo;
    }
    
}
