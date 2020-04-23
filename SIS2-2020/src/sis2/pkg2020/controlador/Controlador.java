/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.controlador;
import java.util.List;
import java.util.Scanner;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import sis2.pkg2020.modelo.*;
import java.io.File;
import java.util.*;


/**
 *
 * @author Marco Speranza López
 */

public class Controlador {

    static Scanner teclado = new Scanner(System.in);
    
    public Controlador(){
       

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
    public void pruebaHQL(){
      
            System.out.println("¡Bienvenidos a la practica correspodiente a HQL!\n");
            System.out.println("Deme el NIF correspodiente al trabajador que quiere buscar.");
            String nif = teclado.nextLine();
            System.out.println(nif);
                   
               Session session = HibernateUtil.abrirConexionHibernate();
               Transaction t = HibernateUtil.abrirConexionBbdd(session);
            try{
         
           
            //1. Recogida del trabajador.
              Trabajadorbbdd trabajador = new Trabajadorbbdd();
              TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
              trabajador= trabajadorDAO.recogidaTrabajadorNIF(session, trabajador,nif);
           
              
            //2.Incremeentar 200 categorias menos la del trabajador.
            Categorias categoria = trabajador.getCategorias();
            CategoriasDAO categoriaDAO = new CategoriasDAO();
            categoriaDAO.subidaSalarioBase(session,categoria,200);
            
            
            //3.Eliminar todas las nominas y trabajadores de dicha empresa.
            Empresas empresa = trabajador.getEmpresas();
           EmpresasDAO empresaDAO = new EmpresasDAO();
            empresaDAO.eliminarEmpresa(session,empresa);
            
            
            //Cerramos la conexion con hibernate y con la base de datos.
             HibernateUtil.cerrarConexiones(t, session);
             
            }catch( HibernateException e){
                t.rollback();
                System.err.println("Error en Hibernate -> controlador.");
              
            }

        
          
           

    }
    /**
     * Vamos a recoger el acceso a la hoja excel
     */
    public void pruebaNIF() throws Exception {
        
       ExcelCrud excel = new ExcelCrud();
       File excelFile = new File("resources/SistemasInformacionII.xlsx");
       ArrayList<Trabajadorbbdd> trabajadores = excel.readExcelFile(excelFile);
       excel.comprobarNIF_NIE(trabajadores);
       //TODO CREACION DE XML ERRORES
       
       
       
    }
    
}
