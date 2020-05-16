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
import sis2.pkg2020.modelo.generadores.GeneradorNIFNIE;
import sis2.pkg2020.modelo.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import sis2.pkg2020.modelo.Wrapper.WrapperBrutoRetencion;
import sis2.pkg2020.modelo.Wrapper.WrapperCategoria;
import sis2.pkg2020.modelo.enums.ModeloErrorXML;
import sis2.pkg2020.modelo.enums.TipoColumnasHoja1;
import sis2.pkg2020.modelo.enums.TipoColumnasHoja2;
import sis2.pkg2020.modelo.enums.TipoColumnasHoja3;
import sis2.pkg2020.modelo.generadores.GeneradorCorreoElectronico;
import sis2.pkg2020.modelo.generadores.GeneradorIBAN;

/**
 * Clase utilizada para las operaciones de las hojas Excel.
 *
 * @author Marco Speranza López
 */
public class ExcelCrud {

    //Todos los de la hoja excel.
    private HashSet<Trabajadorbbdd> trabajadores;
    private ArrayList<Categorias> categorias;
    private ArrayList<Empresas> empresas;
    //Trabajadores duplicados
    private ArrayList<Trabajadorbbdd> duplicados;
    //Trabajadores
    private ArrayList<Trabajadorbbdd> lista;

    private static FileInputStream file;
    private static XSSFWorkbook workbook;
    private HashMap<?, ?> mapCategorias;
    private static HashMap<?, ?> mapBrutoRetencion;
    private static HashMap<?, ?> mapCuotas;
    private static HashMap<?, ?> mapTrienios;

    public ExcelCrud() {

        System.out.println("Creacion del excel crud");
        trabajadores = new HashSet<Trabajadorbbdd>();
        categorias = new ArrayList<Categorias>();
        empresas = new ArrayList<Empresas>();
        duplicados = new ArrayList<Trabajadorbbdd>();
        lista = new ArrayList<Trabajadorbbdd>();

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
     * Este metodo recoge lo datos del excel que no sean vacios, y salta el
     * primer valor.
     *
     * 1º Quitamos los los duplicados de manera automatica con el hashSet 2º No
     * añadimos los elementos nulos a el hash
     *
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
                int numeroFila = row.getRowNum() + 1;
                //  System.out.println("Numero de fila  según excel: " + numeroFila );
                //Descartamos la fila 0, ya que es en la que se nos presenta el titulo y las filas vacias.
                if (row.getRowNum() != 0 && !isRowEmpty(row)) {

                    trabajador = new Trabajadorbbdd();
                    empresa = new Empresas();
                    categoria = new Categorias();
                    //Recogemos los trabajadores en el hashset
                    TrabajadorDAO.recogidaTrabajadorExel(row, trabajador, empresasDAO, categoriasDAO);
                    //Setteamos el id con el valor de la fila
                    trabajador.setIdTrabajador(numeroFila);
                    //ArrayList de todos los trabajadores.
                    lista.add(trabajador);
                    //Añadimos al hashMap, y si es un duplicado no se añade al hash pero si a duplicados.
                    if (!trabajadores.add(trabajador)) {
                        duplicados.add(trabajador);
                        System.out.println("Añadiendo a duplicados "
                                + trabajador.getNombre());
                    }

                }

            }
            //   System.out.println(trabajadores.toString());

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
     * Metodo estatico que permite la apertura de la conexion del excel,
     * teniendo como valores prinicpales el fichero de entrada, y el "libro de
     * trabajo" el cual contendrá el fichero.
     *
     * Este metodo es creado de esta manera, para recoger distintas hojas según
     * la necesidad de ese momento.
     *
     * - Este tipo de file que es FileInputStreamReader, leer el fichero de
     * forma binaria. - El fichero XSSFWorkbook es libro de trabajo sobre el que
     * podremos jugar/trabajar - Elemento basico perteneciente al libro del
     * trabajo, sobre el que se recogeran los datos.
     *
     * @param hoja
     * @return devolvemos la hoja de excel.
     */
    public static XSSFSheet abrirConexionExcel(int hoja) {

        try {
            ExcelCrud.file = new FileInputStream("resources/SistemasInformacionII.xlsx");
            ExcelCrud.workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(hoja);
            return sheet;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelCrud.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelCrud.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metodo complemetario al abrir fichero, el cual cierra todas las
     * conexiones posibles.
     *
     * EL fichero de entrada es cerrada, y el libro de trabajo es escrito por
     * los valores que hayamos acutalizado en ese momento.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void cerrarConexionExcel() throws FileNotFoundException, IOException {

        file.close();
        FileOutputStream outFile = new FileOutputStream(new File("resources/SistemasInformacionII.xlsx"));
        workbook.write(outFile);
        outFile.close();
    }

    /**
     * Actualiza el valor de una celda correspodiente asignando fila columna, y
     * el valor a settear.
     *
     * @param nifnie
     * @param row
     * @param col
     */
    public static void actualizarCelda(String nifnie, Integer row, int col, XSSFSheet sheet) {

        Cell cell = sheet.getRow(row).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(nifnie);

    }

    /**
     * Metodo por el cual pasar los distintos trabajadores, y se comporbara su
     * NIF/NIE. Si estos no son validos, se llama al modelo, para exportar sus
     * errores.
     *
     *
     * 1º Comprobamos si NIF vacio 2º Comrpobamos NIF 3º Exportamos los errores
     * XMLModelo
     *
     * TRABAJAMOS CON EL HASH, PARA EVITAR RECOGER TRABAJADORES REPETIDOS.
     *
     *
     */
    public void comprobarNIFNIE() throws IOException {

        ModeloXML modelo = new ModeloXML(ModeloErrorXML.NIF_NIE);
        XSSFSheet sheet = abrirConexionExcel(0);
        for (Trabajadorbbdd trabajador : trabajadores) {
            if (!trabajador.getNifnie().equals("")) {
                GeneradorNIFNIE calculonif = new GeneradorNIFNIE(trabajador);
                String correcto = calculonif.validar();
                if (!correcto.equals(trabajador.getNifnie())) {
                    System.out.println("Incorrecto NIF en trabajador: " + trabajador.getIdTrabajador());
                    trabajador.setNifnie(correcto);
                    System.out.println(correcto);
                    ExcelCrud.actualizarCelda(correcto, trabajador.getIdTrabajador() - 1, TipoColumnasHoja1.NIF_NIE.ordinal(), sheet);

                }

            } else {
                modelo.addBlanco(trabajador);
                System.out.println("Añadiendo vacio en fila " + trabajador.getIdTrabajador());
            }

        }
        cerrarConexionExcel();
        modelo.recogerDuplicados(duplicados);
        modelo.exportarErroresXML(ModeloErrorXML.NIF_NIE);

    }

    /**
     * Metodo que comprueba la validacion de los codigos de cuenta. Tras su
     * verficacion con la ayuda de la clase GeneradorIBAN, que recoge de uno en
     * uno los trabajadores. Se continuará a la exportacion de errores.
     *
     * 1ºComporbacion de CCC por cada trabajador 1.a Si es valido se deja como
     * esta 2.b Si no es valido modifica y se setea en el excel.
     *
     * 2º Exportacion de los errores correspodientes a los codigos ccc.
     *
     *
     * EN ESTE CASO UTILIZAMOS EL ARRAY LIST QUE CONTIENE TODOS MENOS LOS
     * VACIOS.
     *
     */
    public void comprobarCCC() throws IOException {

        String nombrefichero = "erroresCCC.xml";
        ModeloXML modelo = new ModeloXML(ModeloErrorXML.IBAN);
        //Abrimos conexion
        XSSFSheet sheet = abrirConexionExcel(0);
        for (Trabajadorbbdd trabajador : lista) {
            GeneradorIBAN iban = new GeneradorIBAN(trabajador);

            if (!(iban.cccIsValida() && iban.parControlIsValido())) {

                String correcto = iban.cccValidado();
                System.out.println("En la fila: " + trabajador.getIdTrabajador()
                        + " CCC erroneo:" + trabajador.getCodigoCuenta()
                        + " CCC valido: " + correcto
                );
                //Añadimos el ccc correcto a los trabajadores que no tienen el IBAN vacio.
                String incorrecto = trabajador.getCodigoCuenta();
                modelo.addErroresCCC(trabajador);
                trabajador.setCodigoCuenta(correcto);
                ExcelCrud.actualizarCelda(correcto, trabajador.getIdTrabajador() - 1, TipoColumnasHoja1.CODIGO_CUENTA.ordinal(), sheet);
                //Exportamos los errores

            }
            //Ahora crearemos el codigo IBAN parar todos los trabajadores que esten en la lista -> Incluso los repetidos.
            if (trabajador.getIban().equals("")) {
                //  System.out.println("VACIO");

                trabajador.setIban(iban.generarIBAN(trabajador.getCodigoCuenta()));
                ExcelCrud.actualizarCelda(trabajador.getIban(), trabajador.getIdTrabajador() - 1, TipoColumnasHoja1.IBAN.ordinal(), sheet);
            }
        }
        modelo.exportarErroresXML(ModeloErrorXML.IBAN);
        cerrarConexionExcel();
    }

    /**
     * Este metodo cumplira la tarea de asignar a los trabajadores el correo
     * electroonico.
     *
     * -1º Comprobaremos si tiene o no correo electronico. -2º Si no tiene lo
     * generaremos según unas directrices que están indicados en la clase
     * generadora de correo Electronico -3º Finalmente asignaremos los valores
     * correspodientes a las celdas del Excel.
     *
     *
     * EN ESTE CASO UTILIZAMOS EL ARRAY LIST QUE CONTIENE TODOS MENOS LOS
     * VACIOS.
     */
    public void crearCorreoElectronico() throws IOException {

        ArrayList<Trabajadorbbdd> auxiliar = new ArrayList<>();
        String correoTrabajador;
        XSSFSheet sheet = abrirConexionExcel(0);
        for (Trabajadorbbdd trabajador : lista) {
            auxiliar.add(trabajador);
            GeneradorCorreoElectronico correo = new GeneradorCorreoElectronico(trabajador, auxiliar);
            correoTrabajador = correo.generarCorreo();
            ExcelCrud.actualizarCelda(correoTrabajador, trabajador.getIdTrabajador() - 1, TipoColumnasHoja1.EMAIL.ordinal(), sheet);
        }

        cerrarConexionExcel();
        System.out.println("Terminada la generación de correos!\n");

    }

    /**
     * Este metodo contendrá las llamadas necesarias para la carga de los datos
     * contenidos en las hojas 2,3,4 y 5.
     *
     * Para ello, necesito crear una serie de clases de carga de los datos de
     * manera Wrapper para los diferentes datos de cada hoja.
     */
    public void cargarDatosHojas() {

        try {
            mapCategorias = cargarHoja2();
            mapBrutoRetencion = cargarHoja3();
            mapCuotas = cargarHoja4();
            mapTrienios = cargarHoja5();
            cargarDatosTrabajadores();
        } catch (IOException ex) {
            Logger.getLogger(ExcelCrud.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metodo que devuelve un HashMap de manera que utilizamos la interrogacion
     * para que se observe que es de cualquier tipo.
     *
     * @return HashMap que contiene el nombre de la categoria, y los atributos
     * pertenecientes a ella.
     */
    private HashMap<?, ?> cargarHoja2() throws IOException {

        HashMap<String, WrapperCategoria> map = new HashMap<>();
        XSSFSheet sheet = abrirConexionExcel(1);
        Iterator<Row> rowIterator = sheet.iterator();
        String nombreCategoria = null;
        WrapperCategoria categoria;
        Integer salarioBase = null;
        Integer complementos = null;
        Cell cell;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() != 0 && !isRowEmpty(row)) {
                //Iteramos según el numero de atributos que tengan la fila.
                for (TipoColumnasHoja2 tipoColumna : TipoColumnasHoja2.values()) {
                    cell = row.getCell(tipoColumna.ordinal(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Double aux;
                    switch (tipoColumna) {
                        case CATEGORIA:
                            nombreCategoria = cell.getStringCellValue();
                            break;
                        case SALARIOBASE:
                            aux = cell.getNumericCellValue();
                            salarioBase = aux.intValue();

                            break;
                        case COMPLEMENTOS:
                            aux = cell.getNumericCellValue();
                            complementos = aux.intValue();
                            break;

                    }

                }
                categoria = new WrapperCategoria(salarioBase, complementos);
                map.put(nombreCategoria, categoria);
            }
        }
        //   System.out.println(map.toString());
        cerrarConexionExcel();
          System.out.println("\nRealizada carga de la hoja 2!\n");
        return map;
    }

    /**
     * carga el bruto anual y su retencion.
     *
     * @return
     * @throws IOException
     */
    private HashMap<?, ?> cargarHoja3() throws IOException {
        HashMap<Double, Double> map = new HashMap<>();
        XSSFSheet sheet = abrirConexionExcel(2);
        Iterator<Row> rowIterator = sheet.iterator();
        String nombreBruto = null;
        WrapperBrutoRetencion brutoAnualRetencion;
        double brutoAnual = 0.0;
        double retencion = 0.0;
        Cell cell;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() != 0 && !isRowEmpty(row)) {
                for (TipoColumnasHoja3 tipoColumna : TipoColumnasHoja3.values()) {
                    cell = row.getCell(tipoColumna.ordinal(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Double aux;
                    switch (tipoColumna) {
                        case BRUTOANUAL:
                          brutoAnual = cell.getNumericCellValue();
                             
                            break;
                        case RETENCIONES:

                           retencion = cell.getNumericCellValue();
                            
                            break;
                    }

                }
                brutoAnualRetencion = new WrapperBrutoRetencion(brutoAnual, retencion);
                map.put(brutoAnual, retencion);
            }
        }
        //System.out.println(map.toString());
        cerrarConexionExcel();
        System.out.println("\nRealizada carga de la hoja 3!\n");
        return map;
    }

    /**
     * Metodo que carga:
     *
     * CUOTA_OBRERA_GENERAL_TRABAJADOR("Cuota obrera generaltrabajador"),
     * CUOTA_DESEMPLEO_TRABAJADOR("Cuota desemplo trabajador"),
     * CUOTA_FORMACION_TRABAJADOR("Cuota formacion trabajador"),
     * CONTINGENICIAS_COMUNES_EMPRESARIO(" CONTINGENCIAS COMUNES empresario"),
     * FOGASA_EMPRESARIO("FOGASA empresario"), DESEMPLO_EMPRESARIO("DESEMPLEO
     * empresario"), FORMACION_EMPRESARIO("FORMACION empresario"),
     * ACCIDENTES_TRABAJO_EMPRESARIO("ACCIDENTES TRABAJO empresario");
     *
     * @return hashMap con los valores pertenecientes a cutoras, contigencias,
     * etc.
     * @throws IOException
     */
    private HashMap<?, ?> cargarHoja4() throws IOException {
        HashMap<String, Double> map = new HashMap<>();
        XSSFSheet sheet = abrirConexionExcel(3);
        Iterator<Row> rowIterator = sheet.iterator();
        String nombre = null;
        Double valor = null;

        Cell cell;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (!isRowEmpty(row)) {
                cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                nombre = cell.getStringCellValue();

                cell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                valor = cell.getNumericCellValue();
                map.put(nombre, valor);
            }

        }
        //System.out.println(map.toString());
        System.out.println("\nRealizada la carga de la hoja 4ª\n");
        cerrarConexionExcel();

        return map;
    }

    /**
     * Metodo que carga el numero de trienios y el importe bruto.
     *
     * @return HasMap con los valores del numero de trienios y su correspodiente
     * importe.
     */
    private HashMap<?, ?> cargarHoja5() {
        HashMap<Integer, Double> map = new HashMap<>();
        XSSFSheet sheet = abrirConexionExcel(4);
        Iterator<Row> rowIterator = sheet.iterator();
        Integer numeroTrienio = null;
        Double valor = null;
        Cell cell;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() != 0 && !isRowEmpty(row)) {

                cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Double aux = cell.getNumericCellValue();
                numeroTrienio = aux.intValue();
                cell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                valor = cell.getNumericCellValue();
                
                map.put(numeroTrienio, valor);
            }

        }
         // System.out.println(map.toString());
          System.out.println("\nRealizada la carga de la hoja 5!\n");
        return map;
    }

    public HashSet<Trabajadorbbdd> getCleanTrabajadores() {

        return this.trabajadores;
    }

    /**
     * Cargar los datos pertenecientes a cada trabajador, según el valor que se
     * requiera para la solicitud de las nominas.
     */
    private void cargarDatosTrabajadores() {

        for (Iterator<Trabajadorbbdd> iterator = trabajadores.iterator(); iterator.hasNext();) {
            Trabajadorbbdd next = iterator.next();
            
            //Categorias
            WrapperCategoria caux = (WrapperCategoria) this.mapCategorias.get(next.getCategorias().getNombreCategoria());
            Categorias categoria = next.getCategorias();
            categoria.setSalarioBaseCategoria(caux.getSalarioBase());
            categoria.setComplementoCategoria(caux.getComplementos());
           

        }

    }

    public static HashMap<?, ?> getMapBrutoRetencion() {
        return mapBrutoRetencion;
    }

    public static HashMap<?, ?> getMapCuotas() {
        return mapCuotas;
    }

    public static HashMap<?, ?> getMapTrienios() {
        return mapTrienios;
    }

}
