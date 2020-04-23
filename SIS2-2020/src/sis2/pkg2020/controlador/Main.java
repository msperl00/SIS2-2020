/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.controlador;
import sis2.pkg2020.modelo.*;

/**
 *
 * @author Marco Speranza López
 */
public class Main {
    
    public static void main (String [ ] args) throws Exception {
    
           Controlador controladorPractica = new Controlador();
          //Correspodiente a la práctica 1 HQL
         // practicaHQL(controladorPractica);
           
          //Correspodiente a la práctica 2
          practicaNIF(controladorPractica);
    }

    private static void practicaHQL(Controlador controladorPractica) {
        controladorPractica.pruebaHQL();
    }

    private static void practicaNIF(Controlador controladorPractica) throws Exception {
        controladorPractica.pruebaNIF();
    }

    
    
}
