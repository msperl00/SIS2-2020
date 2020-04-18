
package sis2.pkg2020.modelo;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import sis2.pkg2020.controlador.HibernateUtil;

/**
 *
 * @author Marco Speranza López
 */
public class TrabajadorDAO {
    
    private List<Trabajadorbbdd> trabajadores;
    
    public TrabajadorDAO(){
        
        trabajadores = new ArrayList<>();
      //  trabajadores = listarTrabajadores();
    }
    
    
    
    public Trabajadorbbdd recogidaTrabajadorNIF(Session session, Trabajadorbbdd trabajador,String nif) {
                try{
                //09741138V
                    Query query = session.createQuery("select t from Trabajadorbbdd t where nifnie=:nif");
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

     
}
