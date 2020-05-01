/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.vista;

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
import sis2.pkg2020.modelo.Trabajadorbbdd;
import sis2.pkg2020.modelo.enums.ModeloErrorXML;

/**
 * CLase para la creacion de XML
 *
 * Existiran difrentes modelos segun el tipo de error que se quiera exportar.
 *
 * @author Marco Speranza López
 */
public class ModeloXML {

    private ArrayList<Trabajadorbbdd> erroresduplicados;
    private ArrayList<Trabajadorbbdd> erroresblanco;
    private ArrayList<Trabajadorbbdd> erroresCCC;

    private String nombrefichero;
    private ModeloErrorXML tipoModelo;

    /**
     * Constructor segun el tipo enum que se le pase.
     *
     * @param modelo
     */
    public ModeloXML(ModeloErrorXML modelo) {

        this.tipoModelo = modelo;
        switch (tipoModelo) {

            case IBAN:
                this.nombrefichero = modelo.IBAN.getNombreFicheroError();
                this.erroresCCC = new ArrayList<Trabajadorbbdd>();
                break;

            case NIF_NIE:
                this.nombrefichero = modelo.NIF_NIE.getNombreFicheroError();
                this.erroresduplicados = new ArrayList<Trabajadorbbdd>();
                this.erroresblanco = new ArrayList<Trabajadorbbdd>();

                break;

            default:

                System.out.println("Error en el modelo de exportacion de errores\n");

        }

    }

    public void addDuplicados(Trabajadorbbdd trabajador) {
        erroresduplicados.add(trabajador);
    }

    public void addBlanco(Trabajadorbbdd trabajador) {
        erroresblanco.add(trabajador);
    }

    public void listarStrings() {

        System.out.println("Errores duplicados " + erroresduplicados.toString());
        System.out.println("Errores blanco " + erroresblanco.toString());
    }

    public void exportarErroresXML(ModeloErrorXML modelo) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Trabajadores");
            doc.appendChild(rootElement);

            switch (tipoModelo) {

                case IBAN:
                    recorrerTrabajadoresXML(erroresCCC, doc, rootElement, "CCC");

                    break;

                case NIF_NIE:
                    recorrerTrabajadoresXML(erroresblanco, doc, rootElement, "blanco");
                    recorrerTrabajadoresXML(erroresduplicados, doc, rootElement, "duplicado");
                    break;
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StreamResult result = new StreamResult(new File("resources/" + nombrefichero));

            transformer.transform(source, result);
            System.out.println("Fichero " + nombrefichero + " creado!");

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    private void recorrerTrabajadoresXML(ArrayList<Trabajadorbbdd> errores, Document doc, Element rootElement, String tipoerror) {

        if (tipoerror != "CCC") {
            for (Trabajadorbbdd t : errores) {
                Element empleado = doc.createElement("Trabajador");
                rootElement.appendChild(empleado);

                Attr attr = doc.createAttribute("Fila");
                attr.setValue(String.valueOf(t.getIdTrabajador()));
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
        } else {
            for (Trabajadorbbdd t : errores) {

                Element empleado = doc.createElement("Cuentas");
                rootElement.appendChild(empleado);

                Element attr = doc.createElement("Fila");
                attr.appendChild(doc.createTextNode(String.valueOf(t.getIdTrabajador())));
                empleado.appendChild(attr);

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

    /**
     * Metodo que recoge un arraylist de los duplicados.
     *
     * @param duplicados
     */
    public void recogerDuplicados(ArrayList<Trabajadorbbdd> duplicados) {
        this.erroresduplicados = duplicados;
    }

    public void addErroresCCC(Trabajadorbbdd trabajador) {

        this.erroresCCC.add(trabajador);
    }
}
