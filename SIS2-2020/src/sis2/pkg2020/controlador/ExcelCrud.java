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
            HashMap<String, String> empresas = new HashMap<String, String>();

            //Recorremos por filas el excel
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                //Subdividimos por celdas los valores de cada trabajador.
                if (row.getRowNum() != 0) {

                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        trabajador = new Trabajadorbbdd();
                        
                        switch (i) {
                            //CIF EMPRESA
                            case 1:
                                trabajador.setCifEmpresa(cell.getStringCellValue()); 
                                break;
                            //NOMBRE EMPRESA
                            case 2:
                                   trabajdor.setCif
                                break;
                            //CATEGORIA
                            case 3:

                                break;
                            //FECHA ALTA EMPRESA
                            case 4:
                                trabajador.setFechaAlta(new Date(cell.getDateCellValue().getTime()));
                                break;
                            //NOMBRE
                            case 5:
                                trabajador.setNombre(cell.getStringCellValue());
                                break;

                            //APELLIDO 1
                            case 6:
                                trabajador.setApellido1(cell.getStringCellValue());

                                break;
                            //APELLIDO 2
                            case 7:
                                trabajador.setApellido2(cell.getStringCellValue());

                                break;
                            //NIF/NIE
                            case 8:
                                trabajador.setNifnie(cell.getStringCellValue());
                                break;
                            //EMAIL
                            case 9:
                                trabajador.setEmail(cell.getStringCellValue()); 
                                break;
                            //CODIGO CUENTA
                            case 10:
                                trabajador.setCodigoCuenta(cell.getStringCellValue());
                                break;
                            //PAIS ORIGEN CUENTA BANCARIA
                            case 11:
                                
                                break;
                            //IBAN
                            case 12:

                                break;
                            //PRORRATA
                            case 13:

                                break;
                            //EXTRA
                            case 14:

                                break;
                        }

                        /**
                         * switch(cell.getCellType()){ case BLANK:
                         * System.out.println("Celda en blanco"); break; case
                         * STRING:
                         * System.out.println(cell.getStringCellValue());
                         *
                         * break;
                         *
                         * case NUMERIC: DataFormatter dataFormatter = new
                         * DataFormatter(); String cellValue =
                         * dataFormatter.formatCellValue(cell);
                         * System.out.println(cellValue);
                         *
                         * break; case FORMULA: System.out.println("valor
                         * formula"); System.out.println(i); case BOOLEAN:
                         * System.out.println("valor boolean");
                         * System.out.println(i);
                         *
                         *
                         * }
                         */
                    }

                    //Descartamos la fila 0, ya que es en la que se nos presenta el titulo
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Fallo en el acceso de los valores");
            e.printStackTrace();
        }

        System.out.println("Fichero leido");
        return null;
    }

}
