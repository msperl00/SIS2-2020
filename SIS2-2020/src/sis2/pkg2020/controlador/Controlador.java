/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.controlador;

import sis2.pkg2020.vista.ModeloXML;
import sis2.pkg2020.modelo.dao.EmpresasDAO;
import sis2.pkg2020.modelo.dao.CategoriasDAO;
import sis2.pkg2020.modelo.dao.TrabajadorDAO;
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
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sis2.pkg2020.modelo.generadores.GeneradorNomina;
import sis2.pkg2020.vista.ModeloPDF;

/**
 *
 * @author Marco Speranza López
 */
public class Controlador {

    static Scanner teclado = new Scanner(System.in);
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private String ANSI_RED = "\u001B[31m";

    public Controlador() {

    }

    /**
     * Entrega de la primera práctica de las inserciones de HQL. El sistema
     * pedira el NIF del trabajador que se encuentra en la tabla
     * trabajadoresbbdd. Si el trabajador no se encuentra en la bbdd, se
     * devolverá un mensaje indicando que no lo hemos encontrado. Si es en caso
     * contrario: 1. Mostramos por pantalla, su nombre apellido, nif, categoria,
     * y nombre empresa 2. Incrementar el salario base de todas las categorias,
     * excepto la categoria que no pertenece al trabajador 3. Eliminar todas las
     * nominas y trabajdores que pertenezcan a la misma empresa que el
     * trabajador es introducido.
     */
    public void practica1() {

        System.out.println("¡Bienvenidos a la practica correspodiente a HQL!\n");
        System.out.println("Deme el NIF correspodiente al trabajador que quiere buscar.");
        String nif = teclado.nextLine();
        System.out.println(nif);

        Session session = HibernateUtil.abrirConexionHibernate();
        Transaction t = HibernateUtil.abrirConexionBbdd(session);
        try {

            //1. Recogida del trabajador.
            Trabajadorbbdd trabajador = new Trabajadorbbdd();
            TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
            trabajador = trabajadorDAO.recogidaTrabajadorNIF(session, trabajador, nif);

            //2.Incremeentar 200 categorias menos la del trabajador.
            Categorias categoria = trabajador.getCategorias();
            CategoriasDAO categoriaDAO = new CategoriasDAO();
            categoriaDAO.subidaSalarioBase(session, categoria, 200);

            //3.Eliminar todas las nominas y trabajadores de dicha empresa.
            Empresas empresa = trabajador.getEmpresas();
            EmpresasDAO empresaDAO = new EmpresasDAO();
            empresaDAO.eliminarEmpresa(session, empresa);

            //Cerramos la conexion con hibernate y con la base de datos.
            HibernateUtil.cerrarConexiones(t, session);

        } catch (HibernateException e) {
            t.rollback();
            System.err.println("Error en Hibernate -> controlador.");

        }

    }

    /**
     * Metodo correspodiente a las práctica 2 y 3.
     *
     * @throws Exception
     */
    public void practicas() throws Exception {

        ExcelCrud excel = new ExcelCrud();
        File excelFile = new File("resources/SistemasInformacionII.xlsx");
        System.out.println("\n\t\t\t----------------BIENVENIDO AL PROGRAMA DE GENERADOR DE NOMINA--------------------\n");

        System.out.println("\n\t\t\t1. Solicitando la fecha de las nominas que se quieren generar");
        System.out.println("\n\t\t\t1.1. Continue con el siguiente formato mm/aaaa");
        System.out.println(ANSI_GREEN + "\t\t\t(Existe una expresión regular que comprueba la expresion...)" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "\t\t\t(Tampoco se recogerá ninguna expresion con meses menores que  0 ó mayores que 12 y con años fuera del un rango entre 1990 y 2070)" + ANSI_RESET+"\n\n");

        String fechaNomina = recogerFechaConExpresionRegular();
        System.out.println("\n\n\n");
        //practica1();
        practica2y3(excelFile, excel);
        practica4(excel, fechaNomina);
       // practica5();

    }

    /**
     * Trás la correción tanto del NIE, CCC e IBAN y el correo electronico. Se
     * carga los datos de las demás hojas del archivo excel.
     *
     * @param excelFile
     * @param excel
     * @return Se devuelve el objeto pertenecientes a los valores utilizados en
     * la hoja de excel.
     */
    private ExcelCrud practica2y3(File excelFile, ExcelCrud excel) throws IOException {
        excel.readExcelFile(excelFile);
        //Práctica 2
        excel.comprobarNIFNIE();
        //Práctica 3
        //Primera parte CCC e IBAN
        excel.comprobarCCC();
        //Segunda parte correoElectronico.
        excel.crearCorreoElectronico();
        //Tercera parte Carga de las demás estructuras de datos de las hojas posteriores.
        excel.cargarDatosHojas();

        //Cargar si trabajador necesita prorrateo
        return excel;
    }

    private void practica4(ExcelCrud excel, String fechaNomina) throws IOException{
        HashSet<Trabajadorbbdd> trabajadores = excel.getCleanTrabajadores();

        for (Iterator<Trabajadorbbdd> iterator = trabajadores.iterator(); iterator.hasNext();) {
            Trabajadorbbdd next = iterator.next();
            System.out.println(next.toString());
            GeneradorNomina nominaT = new GeneradorNomina(next, fechaNomina);
            System.out.println("Nomina generada: " + nominaT.generarNomina());
            practica5(next);
            break;
       }
        System.out.println("FIN DE LA EJECUCCION.");
    }

    /**
     * Este metodo comprobará si el formato de la fecha es valido, sino nos
     * permitira volver a intentarlo. He utilizado una expresión regular, creada
     * por mi mismo para hacer la comporbación.
     *
     * @return Fecha en la que queremos realizar la nomina.
     */
    private String recogerFechaConExpresionRegular() {
        boolean bandera = false;
        String fechaNomina = null;
        return "12/2019";
        
//        while (!bandera) {
//            fechaNomina = teclado.nextLine();
//            Pattern pat = Pattern.compile("\\d{2}/\\d{4}");
//            Matcher mat = pat.matcher(fechaNomina);
//            if (mat.matches()) {
//                int mes = Integer.valueOf(fechaNomina.substring(0, 2));
//                int anio = Integer.valueOf(fechaNomina.substring(3, 7));
//                if ((mes > 12 || mes < 1) || (anio < 1990 || anio > 2070)) {
//                    System.out.println(ANSI_RED + "Intentelo de nuevo..." + ANSI_RESET);
//                    return recogerFechaConExpresionRegular();
//
//                }
//                bandera = true;
//            } else {
//                System.out.println(ANSI_RED + "Intentelo de nuevo..." + ANSI_RESET);
//
//            }
//
//        }

       //return fechaNomina;
    }

    private void practica5(Trabajadorbbdd next) {
        
        Session session = HibernateUtil.abrirConexionHibernate();
        Transaction t = HibernateUtil.abrirConexionBbdd(session);
        try {

          session.save(next);

            //Cerramos la conexion con hibernate y con la base de datos.
            HibernateUtil.cerrarConexiones(t, session);

        } catch (HibernateException e) {
            t.rollback();
            System.err.println("Error en Hibernate -> controlador.");

        }
    }

}
