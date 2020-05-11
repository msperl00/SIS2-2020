
package sis2.pkg2020.modelo.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import sis2.pkg2020.controlador.HibernateUtil;
import sis2.pkg2020.modelo.Categorias;
import sis2.pkg2020.modelo.Empresas;
import sis2.pkg2020.modelo.Trabajadorbbdd;

/**
 * Clase utilizada para realizar todas las acciones correspodientes con una 
 * bbdd.
 * @author Marco Speranza López
 */
public class TrabajadorDAO {

    /**
     * Recorremos las celdas
     * @param row
     * @param trabajador
     * @param hashEmpresas
     * @param categoria 
     */
    public static void recogidaTrabajadorExel(Row row, Trabajadorbbdd trabajador, EmpresasDAO empresasDAO, CategoriasDAO categoriaDAO ) {
        
        Empresas empresa = new Empresas();
        Categorias categoria = new Categorias();
        for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        
                        
                        switch (i) {
                            //CIF EMPRESA
                            case 0:
                                
                                empresa.setCif(cell.getStringCellValue());
                                
                                break;
                            //NOMBRE EMPRESA
                            case 1:
                              
                                empresa.setNombre(cell.getStringCellValue());
                              

                                break;
                            //CATEGORIA
                            case 2:
                                categoria.setNombreCategoria(cell.getStringCellValue());
                               

                                break;
                            //FECHA ALTA EMPRESA
                            case 3:
                                trabajador.setFechaAlta(new Date(cell.getDateCellValue().getTime()));
                              
                                break;
                            //NOMBRE
                            case 4:
                                trabajador.setNombre(cell.getStringCellValue());
                                break;

                            //APELLIDO 1
                            case 5:
                                trabajador.setApellido1(cell.getStringCellValue());

                                break;
                            //APELLIDO 2
                            case 6:
                                trabajador.setApellido2(cell.getStringCellValue());

                                break;
                            //NIF/NIE
                            case 7:
                                trabajador.setNifnie(cell.getStringCellValue());
                                break;
                            //EMAIL
                            case 8:
                                trabajador.setEmail(cell.getStringCellValue());
                                break;
                            //CODIGO CUENTA
                            case 9:
                                trabajador.setCodigoCuenta(cell.getStringCellValue());
                                break;
                            //PAIS ORIGEN CUENTA BANCARIA
                            case 10:
                                trabajador.setPaisCCC(cell.getStringCellValue());
                                break;
                            //IBAN
                            case 11:
                                trabajador.setIban(cell.getStringCellValue());
                                break;
                            //PRORRATA
                            case 12:
                                trabajador.setProrrata(cell.getStringCellValue());
                                break;
                            //EXTRA
                            case 13:
                                trabajador.setExtra(cell.getStringCellValue());
                                break;
                        }
                       
                       
                    }
                     //Asingar empresa y categoria respectivamente
                     empresasDAO.asignarIdEmpresa(empresa);
                     trabajador.setEmpresas(empresa);
                      
                     categoriaDAO.asignarIdCategorias(categoria);
                     trabajador.setCategorias(categoria);
     
    }
    
   
    
    public TrabajadorDAO(){
        
    }
    
    
    /**
     * Hacemos la consulta a base de datos con hql, y devolvemos el trabajador con el correponiente NIF elegido
     * @param session
     * @param trabajador
     * @param nif
     * @return trabajador 
     */
    public Trabajadorbbdd recogidaTrabajadorNIF(Session session, Trabajadorbbdd trabajador,String nif) {
                try{
                //09741138V
                    Query query = session.createQuery("from Trabajadorbbdd t where nifnie=:nif");
                    query.setParameter("nif", nif);
                    trabajador = (Trabajadorbbdd) query.uniqueResult();
                    System.out.println(trabajador.toStringHQL());
                
                
                }catch(QuerySyntaxException e){
                    System.out.println("La consulta de trabajador es erronea, intentelo de nuevo!");
                }
                
                return trabajador;
    }
    /**
     * Recogemos la lista de trabajadores de la base de datos, y ,edinate createCriteria
     * filtraremos los resultados por una clase especifica. Este metodo puede restringir
     * mediante atributios.
     * @return List
     */
   
    public List listarTrabajadores(){
        
        return HibernateUtil.abrirConexionHibernate().createCriteria(Trabajadorbbdd.class).list();
    }

    /**
     * Eliminaremos el trabajador y la nomina correspodiente a la empresa objeto recogido por argumentos en el trabajdor correspodiente.
     * @param trabajador 
     */
    public void eliminarTrabajador(Session session,Trabajadorbbdd trabajador) {
        try{
            
            session.delete(trabajador);
            session.flush();
        
        }catch(QuerySyntaxException e){
            System.err.println("Error en la consulta DELETE de trabajador.");
        }
    }
    
        
  

     
}
