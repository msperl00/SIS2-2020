/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo;

import org.hibernate.Session;

/**
 *
 * @author Usuario
 */
public class EmpresasDAO {
    
    public EmpresasDAO(){
        
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
    
}
