/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.controlador;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import sis2.pkg2020.modelo.Categorias;
import sis2.pkg2020.modelo.CategoriasDAO;
import sis2.pkg2020.modelo.TrabajadorDAO;
import sis2.pkg2020.modelo.Trabajadorbbdd;


/**
 *
 * @author Marco Speranza López
 */

public class Controlador {

    static Scanner teclado = new Scanner(System.in);
    
    public Controlador(){
        pruebaHQL();

    }

    /**
     * Entrega de la primera práctica de las inserciones de HQL.
     * El sistema pedira el NIF del trabajador que se encuentra en la
     * tabla trabajadoresbbdd. 
     * Si el trabajador no se encuentra en la bbdd, se devolverá un
     * mensaje indicando que no lo hemos encontrado.
     * Si es en caso contrario:
     * 1. Mostramos por pantalla, su nombre apellido, nif, categoria, y nombre empresa
     * 2. Incrementar el salario base de todas las categorias, excepto la categoria que no pertenece al trabajador
     * 3. Eliminar todas las nominas y trabajdores que pertenezcan a la misma empresa que el trabajador es introducido.
     */
    public static void pruebaHQL(){
      
            System.out.println("¡Bienvenidos a la practica correspodiente a HQL!\n");
            System.out.println("Deme el NIF correspodiente al trabajador que quiere buscar.");
            String nif = teclado.nextLine();
            System.out.println(nif);
                    
            Session session = HibernateUtil.abrirConexionHibernate();
            Transaction t = HibernateUtil.abrirConexionBbdd(session);
           
            //1. Recogida del trabajador.
              Trabajadorbbdd trabajador = new Trabajadorbbdd();
              TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
              trabajador= trabajadorDAO.recogidaTrabajadorNIF(session, trabajador,nif);
           
              
            //2.Incremeentar 200 categorias menos la del trabajador.
            Categorias categoria = trabajador.getCategorias();
            CategoriasDAO categoriaDAO = new CategoriasDAO();
            categoriaDAO.subidaSalarioBase(session,categoria,200);
           

        
           HibernateUtil.cerrarConexiones(t, session);
           

    }
    
}
