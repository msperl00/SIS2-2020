/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * CLase para la creacion de XML
 *
 * @author Marco Speranza López
 */
public class ModeloXML {

    private ArrayList<Trabajadorbbdd> erroresduplicados;
    private ArrayList<Trabajadorbbdd> erroresblanco;
    private ArrayList<Trabajadorbbdd> listaTrabajadores;

    public ModeloXML(ArrayList<Trabajadorbbdd> erroresduplicados, ArrayList<Trabajadorbbdd> erroresblanco) {

        this.erroresduplicados = erroresduplicados;
        this.erroresblanco = erroresblanco;

    }

    public ModeloXML() {

        this.erroresduplicados = new ArrayList<Trabajadorbbdd>();
        this.erroresblanco = new ArrayList<Trabajadorbbdd>();
        this.listaTrabajadores = new ArrayList<Trabajadorbbdd>();

    }

    public void addDuplicados(Trabajadorbbdd trabajador) {
        erroresduplicados.add(trabajador);
    }

    public void addBlanco(Trabajadorbbdd trabajador) {
        erroresblanco.add(trabajador);
    }
    
    public void listarStrings(){
         System.out.println("Unicos "+listaTrabajadores.toString());
        System.out.println("Errores duplicados " +erroresduplicados.toString());
        System.out.println("Errores blanco " +erroresblanco.toString());
    }

    public void addListaSinDuplicados(Trabajadorbbdd trabajador) {

        if (listaTrabajadores.isEmpty()) {
            System.out.println("Añadiendo primer trabajador");
            listaTrabajadores.add(trabajador);
            System.out.println(listaTrabajadores.size());
        } else {
                boolean unico = true;
            for (Trabajadorbbdd trabajadoresunicos : listaTrabajadores) {
                    System.out.println(trabajadoresunicos.getNombre()+ " vs "+ trabajador.getNombre());

                if(trabajadoresunicos.equals(trabajador) && unico){
                    System.out.println("Iguales");
                    unico = false;
                }
                
            }
            //SI no es unico
            if(!unico){
                erroresduplicados.add(trabajador);
            }else{
                listaTrabajadores.add(trabajador);
                
            }
            
        
      
        }
       
    }

}
