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
import sis2.pkg2020.modelo.operaciones.CalcularNIFNIE;
import sis2.pkg2020.modelo.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase utilizada para las operaciones de las hojas Excel.
 *
 * @author Marco Speranza López
 */
public class ExcelCrud {

    private ArrayList<Trabajadorbbdd> trabajadores;
    private ArrayList<Categorias> categorias;
    private ArrayList<Empresas> empresas;


    public ExcelCrud() {

        System.out.println("Creacion del excel crud");
        trabajadores = new ArrayList<Trabajadorbbdd>();
        categorias = new ArrayList<Categorias>();
        empresas = new ArrayList<Empresas>();
       
    }

 

    /**
     * Comporbamos si la fila que estamos recogiendo esta vacia o no
     *
     * @param row
     * @return
     */
    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba si los valores NIE/NIF son correctos, y sino los actualiza.
     *
     * @param trabajadores
     */
    public void comprobarNIF_NIE(Trabajadorbbdd trabajador, ModeloXML modelo) {
        
        
        CalcularNIFNIE dni = new CalcularNIFNIE(trabajador, modelo);
        System.out.println("Trabajador con numero de fila "+ trabajador.getIdTrabajador());
     
     //Es false cuando es blanco
     
        System.out.println("En validacion -> "+trabajador.getNombre()+ " "+ trabajador.getNifnie());
        
        if(!dni.validar() && !modelo.isDuplicado(trabajador) ){
            //Añadiendo trabajdores
            
            trabajadores.add(trabajador);
        }
     
                

        

    }
    /**
     * Este metodo recoge lo datos del excel que no sean vacios, y salta el primer valor.
     * @param excelFile
     * @param modelo 
     */
    public void readExcelFile(File excelFile) {
        
        InputStream excelStream = null;
        
        try {

            excelStream = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(excelStream);
            //Cogemos la primera hoja del excel.
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Trabajadorbbdd trabajador = null;
            Empresas empresa = null;
            Categorias categoria = null;
            EmpresasDAO empresasDAO = new EmpresasDAO();
            CategoriasDAO categoriasDAO = new CategoriasDAO();

            //Recorremos por filas el excel
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int idempresas = 0;
                int numeroFila = row.getRowNum()+1;
              //  System.out.println("Numero de fila  según excel: " + numeroFila );
                //Descartamos la fila 0, ya que es en la que se nos presenta el titulo y las filas vacias.
                if (row.getRowNum() != 0 && !isRowEmpty(row)) {

                    trabajador = new Trabajadorbbdd();
                    empresa = new Empresas();
                    categoria = new Categorias();

                    TrabajadorDAO.recogidaTrabajadorExel(row, trabajador, empresasDAO, categoriasDAO);
                    
                   trabajador.setIdTrabajador(numeroFila);
                   trabajadores.add(trabajador);
                  
                }
                
                 
                    

            }
            System.out.println(trabajadores.toString());
          
        } catch (FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Fallo en el acceso de los valores");
            e.printStackTrace();
        }
        System.out.println("Fichero leido");
       
    }
    /**
     * Actualiza el valor de una celda correspodiente.
     * @param nifnie
     * @param row
     * @param col 
     */
    public static void actualizarCelda(String nifnie, Integer row, int col) {
        
            FileInputStream file;
        try {
            file = new FileInputStream("resources/SistemasInformacionII.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            
              Cell cell = sheet.getRow(row).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
              
              System.out.println("Nuevo valor en la columna"+col);
              cell.setCellValue(nifnie);
              
              file.close();
              
            FileOutputStream outFile =new FileOutputStream(new File("resources/SistemasInformacionII.xlsx"));
            workbook.write(outFile);
            outFile.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelCrud.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelCrud.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }
    /**
     * Metetodo por el cual pasar los distintos trabajadores, y se comporbara su NIF/NIE.
     * Si estos no son validos, se llama al modelo, para exportar sus errores.
     */
    public void comprobarNIFNIE(ModeloXML modelo) {
                
                //   comprobarNIF_NIE(trabajador, modelo);
    }

 
}
