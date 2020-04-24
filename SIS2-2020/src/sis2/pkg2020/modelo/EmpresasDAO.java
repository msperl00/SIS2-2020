/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo;

import org.hibernate.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author Marco Speranza López
 */
public class EmpresasDAO {
        
        //HashMap<CIF,ID>
        private HashMap<String, Integer> hashEmpresas ;
        
    /***
     * Comprobamos si existe la empresa segun el cif que tenga, ya que este numero es invariante. Y devolvemos
     * y seteamos el id de la empresa.
     * @param empresa
     * @param hashEmpresas 
     */
    public void asignarIdEmpresa(Empresas empresa) {
        
           if(!hashEmpresas.containsKey(empresa.getNombre())){
                            int id = hashEmpresas.size();
                            hashEmpresas.put(empresa.getNombre(),id);
                            empresa.setIdEmpresa(id);
                            
                        }else{
                            //Si la empresa ya existe
                            empresa.setIdEmpresa(hashEmpresas.get(empresa.getNombre()));
                        }
           
    }
    


   
    public EmpresasDAO(){
        hashEmpresas = new HashMap<String, Integer>();

    }
    
  
    /**
     * Eliminamos mediante el metodo delete objet nativo de hibernate, con el cual produce la acciones correspodientes,
     * en cacada, para eliminar todos los objetos que tengan relacion con este, como son nominas, y trabajadores.
     * @param session
     * @param empresa 
     */
    public void eliminarEmpresa(Session session, Empresas empresa) {
        session.delete(empresa);
        session.flush();
    }

    public void listarEmpresasExcel() {
        Iterator<Map.Entry<String,Integer>> it  = hashEmpresas.entrySet().iterator();
        
        while(it.hasNext()){
            Map.Entry<String,Integer> e = it.next();
            System.out.println(e.getKey()+ " " + e.getValue());
    }
                
    }
    
}
