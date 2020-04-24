/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;



/**
 * CLase para la creacion de XML
 *
 * @author Marco Speranza López
 */
public class ModeloXML {

    private ArrayList<Trabajadorbbdd> erroresduplicados;
    private ArrayList<Trabajadorbbdd> erroresblanco;
    private ArrayList<Trabajadorbbdd> listaTrabajadores;

    public ModeloXML(ArrayList<Trabajadorbbdd> erroresduplicados, ArrayList<Trabajadorbbdd> erroresblanco) {

        this.erroresduplicados = erroresduplicados;
        this.erroresblanco = erroresblanco;

    }

    public ModeloXML() {

        this.erroresduplicados = new ArrayList<Trabajadorbbdd>();
        this.erroresblanco = new ArrayList<Trabajadorbbdd>();
        this.listaTrabajadores = new ArrayList<Trabajadorbbdd>();

    }

    public void addDuplicados(Trabajadorbbdd trabajador) {
        erroresduplicados.add(trabajador);
    }

    public void addBlanco(Trabajadorbbdd trabajador) {
        erroresblanco.add(trabajador);
    }
    
    public void listarStrings(){
         System.out.println("Unicos "+listaTrabajadores.toString());
        System.out.println("Errores duplicados " +erroresduplicados.toString());
        System.out.println("Errores blanco " +erroresblanco.toString());
    }

    public void addListaSinDuplicados(Trabajadorbbdd trabajador) {

        if (listaTrabajadores.isEmpty()) {
            System.out.println("Añadiendo primer trabajador");
            listaTrabajadores.add(trabajador);
            System.out.println(listaTrabajadores.size());
        } else {
                boolean unico = true;
            for (Trabajadorbbdd trabajadoresunicos : listaTrabajadores) {
                    System.out.println(trabajadoresunicos.getNombre()+ " vs "+ trabajador.getNombre());

                if(trabajadoresunicos.equals(trabajador) && unico){
                    System.out.println("Iguales");
                    unico = false;
                }
                
            }
            //SI no es unico
            if(!unico){
                erroresduplicados.add(trabajador);
            }else{
                listaTrabajadores.add(trabajador);
                
            }
            
        
      
        }
       
    }

    public void exportarErroresXML() {
        
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Trabajadores");
            doc.appendChild(rootElement);
            
            
            recorrerTrabajadoresXML(erroresblanco, doc, rootElement, "blanco");
            recorrerTrabajadoresXML(erroresduplicados, doc, rootElement, "duplicado");
            
            
             TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StreamResult result = new StreamResult(new File("resources/Errores.xml"));
            
            transformer.transform(source, result);
            
            System.out.println("Fichero sd Errores.xml creado!");
            
        }  catch (ParserConfigurationException | TransformerException pce) {
               pce.printStackTrace();
        }
        }

    private void recorrerTrabajadoresXML(ArrayList<Trabajadorbbdd> errores, Document doc, Element rootElement, String tipoerror) {
        
          for(Trabajadorbbdd t :errores){
                 Element empleado = doc.createElement("Trabajador");
                rootElement.appendChild(empleado);
                
                Attr attr = doc.createAttribute("Fila");
                attr.setValue(String.valueOf(t.getFilaExcel()));
                empleado.setAttributeNode(attr);
                
                Element tipoerror1 = doc.createElement("TipoError");
                tipoerror1.appendChild(doc.createTextNode(tipoerror));
                empleado.appendChild(tipoerror1);
                
                 Element nombre = doc.createElement("Nombre");
                nombre.appendChild(doc.createTextNode(t.getNombre()));
                empleado.appendChild(nombre);
                
                 Element primerApellido = doc.createElement("PrimerApellido");
                primerApellido.appendChild(doc.createTextNode(t.getApellido1()));
                empleado.appendChild(primerApellido);
                
                  Element segundoApellido = doc.createElement("SegundoApellido");
                segundoApellido.appendChild(doc.createTextNode(t.getApellido2()));
                empleado.appendChild(segundoApellido);
                
                 Element categoria = doc.createElement("Categoria");
                categoria.appendChild(doc.createTextNode(t.getCategorias().getNombreCategoria()));
                empleado.appendChild(categoria);
                
                Element empresa = doc.createElement("Empresa");
                empresa.appendChild(doc.createTextNode(t.getEmpresas().getNombre()));
                empleado.appendChild(empresa);
            }
    
    }

}
