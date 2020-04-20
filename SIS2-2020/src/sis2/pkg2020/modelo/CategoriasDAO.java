/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.hql.internal.ast.QuerySyntaxException;

/**
 *
 * @author Usuario
 */
public class CategoriasDAO {
    
     List<Categorias> categorias;
     
   public CategoriasDAO(){
        categorias = new ArrayList<Categorias>();
    }
/**
 * Este metodo recoge como valores prinicipales la categoria que no se va a subir de sueldo, y el incremento de sueldo
 * que hay que subir para el resto de categorias.La recogida de session es necesaria, para
 * acutalizar el objeto correspodiente en la base de datos
 * 
 * @param session
 * @param categoria
 * @param subida 
 */
    public void subidaSalarioBase(Session session, Categorias categoria, int subida) {
            try{
             Query query= session.createQuery("from Categorias o where o not in (select o from Categorias o where o.nombreCategoria='"+categoria.getNombreCategoria()+"') ");
              List<Categorias> categorias = query.list();
              for(Categorias c: categorias){
                  
                  System.out.println(c.toString());
                  c.setSalarioBaseCategoria(c.getSalarioBaseCategoria()+subida);
                  session.update(c);
                  System.out.println(c.toString());
              }
            }catch(QuerySyntaxException e){
                System.err.println("Error en la consulta de Categorias");
            }
    }        
   
  
}
