/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.controlador;

import sis2.pkg2020.modelo.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
     * Leemos el fichero y recogemos a los trabajadores de la hoja de excel
     *
     * @param excelFile
     * @return
     */
    public ArrayList<Trabajadorbbdd> readExcelFile(File excelFile) throws Exception {

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
                System.out.println("Numero de fila: " + row.getRowNum());
                //Descartamos la fila 0, ya que es en la que se nos presenta el titulo y las filas vacias.
                if (row.getRowNum() != 0 && !isRowEmpty(row)) {

                    trabajador = new Trabajadorbbdd();
                    empresa = new Empresas();
                    categoria = new Categorias();

                    TrabajadorDAO.recogidaTrabajadorExel(row, trabajador, empresasDAO, categoriasDAO);
                    System.out.println(trabajador.toString());
                    trabajadores.add(trabajador);
                }

            }

            //  empresasDAO.listarEmpresasExcel();
            //   categoriasDAO.listarCategoriasExcel();
        } catch (FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Fallo en el acceso de los valores");
            e.printStackTrace();
        }

        System.out.println("Fichero leido");
        return trabajadores;
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
    public void comprobarNIF_NIE(ArrayList<Trabajadorbbdd> trabajadores) {
        
        CalcularNIFNIE dni = null;
        for (Iterator<Trabajadorbbdd> iterator = trabajadores.iterator(); iterator.hasNext();) {
            Trabajadorbbdd trabajador = iterator.next();
             dni = new CalcularNIFNIE(trabajador.getNifnie());
            System.out.println("En validacion -> "+trabajador.getNombre()+ trabajador.getNifnie());
            System.out.println(dni.validar());
             
            //TODO 
            //COLECCION Y CORRECION DE ERRORES
            //XML

        }
    }
 
}
