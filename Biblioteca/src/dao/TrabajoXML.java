/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dominio.Categoria;
import dominio.DifinicionAutor;
import dominio.Glosario;
import dominio.Termino;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.ExemptionMechanismException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.SAXException;
import util.TerminoUtil;

/**
 *
 * @author Eduardo
 */
public class TrabajoXML implements Dao {

    private Document crearDocumentoXML() throws Exception {
        DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Exception("La configuración del parser es incorrecta -- TrabajoXML.crearDocumentoXML--");
        }


        Document document = docBuilder.newDocument();


        return document;

    }

    public void annadirGlosario(Glosario glosa) throws Exception {
        Document miDocumento = crearDocumentoXMLCargar();
        Element raiz = miDocumento.getDocumentElement();
        Element glossary = miDocumento.createElement("GLOSSARY");
        Element name = miDocumento.createElement("NAME");
        Element intro = miDocumento.createElement("INTRO");
        Text textName = miDocumento.createTextNode(glosa.getNombre());
        Text textIntro = miDocumento.createTextNode(glosa.getDescip());
        //Element entrys = miDocumento.createElement("ENTRIES");

        raiz.appendChild(glossary);
        glossary.appendChild(name);
        glossary.appendChild(intro);
        //glossary.appendChild(entrys);
        name.appendChild(textName);
        intro.appendChild(textIntro);



        salvarXML(miDocumento);
    }

    public void annadirTermino(Termino termi) throws Exception {
        Document miDocumento = crearDocumentoXMLCargar();
        Element raiz = miDocumento.getDocumentElement();
        
        System.out.print(raiz.getNodeName());
        Element glosarioSeleccionado = null;
        Element entrys;
        //Busco todos los glosarios y dentro de ellos el que conicide con el glosario del termino que se quiere insertar
        NodeList nlGlosarios = raiz.getElementsByTagName("GLOSSARY");
        for (int i = 0; i < nlGlosarios.getLength(); i++) {
            if (getTagValue("NAME", (Element) nlGlosarios.item(i)).equals(termi.getGlosa().getNombre())) {
                glosarioSeleccionado = (Element) nlGlosarios.item(i);
            }

        }

        //Selecciono en entries para adjuntar el termino o lo creo en caso que no que el glosario no tenga terminos
        if (glosarioSeleccionado.getElementsByTagName("ENTRIES").getLength() != 0) {
            entrys = (Element) glosarioSeleccionado.getElementsByTagName("ENTRIES").item(0);
            System.out.print(entrys.getNodeName());
        } else {
            entrys = miDocumento.createElement("ENTRIES");
            glosarioSeleccionado.appendChild(entrys);
        }



        //Agrego el nombre del termino
        Element termino = miDocumento.createElement("ENTRY");
        Element terminoName = miDocumento.createElement("CONCEPT");
        Text textTerminoName = miDocumento.createTextNode(termi.getNombre());
        terminoName.appendChild(textTerminoName);
        termino.appendChild(terminoName);

        //Agrego los alias al termino
        Element palabrasClaves = miDocumento.createElement("ALIASES");

        for (int i = 0; i < termi.getPalabrasClaves().size(); i++) {
            Element alias = miDocumento.createElement("ALIAS");
            Text textAlias = miDocumento.createTextNode(termi.getPalabrasClaves().get(i));
            alias.appendChild(textAlias);
            palabrasClaves.appendChild(alias);

        }
        termino.appendChild(palabrasClaves);

        //Agrego las categorias
        Element categorias = miDocumento.createElement("CATEGORIES");
        Element categoria = miDocumento.createElement("CATEGORY");
        Element nameCategoria = miDocumento.createElement("NAME");
        Text textNameCategoria = miDocumento.createTextNode(termi.getCategoria().getNombre());

        nameCategoria.appendChild(textNameCategoria);
        categoria.appendChild(nameCategoria);
        categorias.appendChild(categoria);

        termino.appendChild(categorias);



        //Agrego las definiciones.
        Element definiciones = miDocumento.createElement("DEFINITIONS");

        for (int i = 0; i < termi.getDefiniciones().size(); i++) {
            Element definicion = miDocumento.createElement("DEFINITION");
            Element introDefinicion = miDocumento.createElement("INTRO");
            Element nombreDocumento = miDocumento.createElement("ARCHNAME");
            Element autorDocumento = miDocumento.createElement("AUTOR");
            Element pagDocumento = miDocumento.createElement("PAG");
            Element archivo = miDocumento.createElement("ARCH");
            Element localizacion = miDocumento.createElement("LOCA");
            
            Element fecha = miDocumento.createElement("FECHA");
            Element dia = miDocumento.createElement("DIA");
            Element mes = miDocumento.createElement("MES");
            Element anno = miDocumento.createElement("ANNO");
            
            fecha.appendChild(dia);
            fecha.appendChild(mes);
            fecha.appendChild(anno);
            
            Text textDiaFecha = miDocumento.createTextNode(termi.getDefiniciones().get(i).getDia());
            Text textMesFecha = miDocumento.createTextNode(termi.getDefiniciones().get(i).getMes());
            Text textAnnoFecha = miDocumento.createTextNode(termi.getDefiniciones().get(i).getAnno());
            
            dia.appendChild(textDiaFecha);
            mes.appendChild(textMesFecha);
            anno.appendChild(textAnnoFecha);
            
            definicion.appendChild(fecha);
            

            definiciones.appendChild(definicion);

            definicion.appendChild(introDefinicion);
            definicion.appendChild(nombreDocumento);
            definicion.appendChild(autorDocumento);
            definicion.appendChild(pagDocumento);
            definicion.appendChild(archivo);
            definicion.appendChild(localizacion);


            //textos
            Text textIntroDefinicion = miDocumento.createTextNode(termi.getDefiniciones().get(i).getDefinición());
            Text textNombreDocumentoDefinicion = miDocumento.createTextNode(termi.getDefiniciones().get(i).getNombreArticulo());
            Text textAutorDocumentoDefinicion = miDocumento.createTextNode(termi.getDefiniciones().get(i).getAutor());
            Text textPagDocumentoDefinicion = miDocumento.createTextNode(String.valueOf(termi.getDefiniciones().get(i).getPagDefinicion()));
            Text textArchivoDocumentoDefinicion = miDocumento.createTextNode(termi.getDefiniciones().get(i).getNombreArchivo());
            Text textLocalizacionDocumentoDefinicion = miDocumento.createTextNode(termi.getDefiniciones().get(i).getLocalizacionPC());

            introDefinicion.appendChild(textIntroDefinicion);
            nombreDocumento.appendChild(textNombreDocumentoDefinicion);
            autorDocumento.appendChild(textAutorDocumentoDefinicion);
            pagDocumento.appendChild(textPagDocumentoDefinicion);
            archivo.appendChild(textArchivoDocumentoDefinicion);
            localizacion.appendChild(textLocalizacionDocumentoDefinicion);


        }
        //guardo las definiciones
        termino.appendChild(definiciones);


        //me da un error aqui
        entrys.appendChild(termino);

        salvarXML(miDocumento);
    }

    /* public void exportarXML() throws Exception {
    
    //Document miDocumento = crearDocumentoXML();
    Document miDocumento = crearDocumentoXMLCargar();
    //Element raiz = miDocumento.createElement("Correos");
    Element raiz = miDocumento.getDocumentElement();
    System.out.println(raiz.getNodeName());
    Element elemento = miDocumento.createElement("Correo");
    elemento.setAttribute("direccion", "Ciudad Habana");
    
    Text text = miDocumento.createTextNode("ealfonso@uci.cu");
    
    
    // miDocumento.appendChild(raiz);
    raiz.appendChild(elemento);
    elemento.appendChild(text);
    
    salvarXML(miDocumento);
    
    
    }
     */
    private void salvarXML(Document miDocumento) {
        Source source = new DOMSource(miDocumento);
        Result result = new StreamResult(new java.io.File("biblioteca.xml"));
        Result console = new StreamResult(System.out);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            transformer.transform(source, console);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private Document crearDocumentoXMLCargar() throws Exception {

        DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Exception("La configuración del parser es incorrecta -- TrabajoXML.crearDocumentoXMLCargar--");
        }

        Document doc = null;

        try {
            doc = docBuilder.parse(new File("biblioteca.xml"));
            System.out.println("Documento cargado con exito");
        } catch (SAXException ex) {
            throw new Exception("Error al parsear el archivo -- TrabajoXML.crearDocumentoXMLCargar--");
        } catch (IOException ex) {
            throw new Exception("Error de entrada salida -- TrabajoXML.crearDocumentoXMLCargar--");
        }
        doc.getDocumentElement().normalize();
        doc.setXmlStandalone(true);


        return doc;
    }

    public List<String> obtenerGlosarios() throws Exception {

        Document doc = crearDocumentoXMLCargar();
        List<String> glosarios = new ArrayList<String>();

        NodeList listaGlosarios = doc.getElementsByTagName("GLOSSARY");

        for (int i = 0; i < listaGlosarios.getLength(); i++) {
            Node glosario = listaGlosarios.item(i);


            if (glosario.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) glosario;
                glosarios.add(getTagValue("NAME", elemento));
            }
        }
        return glosarios;

    }
    public List<String> obtenerTerminos() throws Exception {

        Document doc = crearDocumentoXMLCargar();
        List<String> terminos = new ArrayList<String>();

        NodeList listaTerminos = doc.getElementsByTagName("CONCEPT");

        for (int i = 0; i < listaTerminos.getLength(); i++) {
            Node termino = listaTerminos.item(i);


            if (termino.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) termino;
                terminos.add(termino.getFirstChild().getNodeValue());
            }
        }
        return terminos;

    }

    public List<String> obtenerCategorias() throws Exception {
        Document doc = crearDocumentoXMLCargar();
        List<String> catego = new ArrayList<String>();

        NodeList listaCategorias = doc.getElementsByTagName("CATEGORY");


        for (int i = 0; i < listaCategorias.getLength(); i++) {
            Node cate = listaCategorias.item(i);


            if (cate.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) cate;
                catego.add(getTagValue("NAME", elemento));
            }
        }
        List<String> cateSinRepetir = new ArrayList<String>();
        if (catego.size() > 0) {
            
            for(int i = 0; i < catego.size(); i++)
                 if(!cateSinRepetir.contains(catego.get(i)))
                     cateSinRepetir.add(catego.get(i));
            
        }
        return cateSinRepetir;
    }
    
    public List<String> obtenerAutores() throws Exception {
        Document doc = crearDocumentoXMLCargar();
        List<String> auto = new ArrayList<String>();

        NodeList listaAutores = doc.getElementsByTagName("AUTOR");


        for (int i = 0; i < listaAutores.getLength(); i++) {
            Node autoNode = listaAutores.item(i);


            if (autoNode.getNodeType() == Node.ELEMENT_NODE) {
                //obtengo el nodo texto del autor usando este metodo getFirstChild ya que se que tiene un solo hijo: el texo
                auto.add(autoNode.getFirstChild().getNodeValue());  
                
            }
        }
        List<String> autorSinRepetir = new ArrayList<String>();
        if (auto.size() > 0) {
            
            for(int i = 0; i < auto.size(); i++)
                 if(!autorSinRepetir.contains(auto.get(i)))
                     autorSinRepetir.add(auto.get(i));
            
        }
        return autorSinRepetir;
    }
    public String obtenerNombreDocumento(String nombreArchivo) throws Exception{
        String nombreDocumento = null;
        
        Document doc = crearDocumentoXMLCargar();
        List<String> arch = new ArrayList<String>();
        
       
        Node nombreArchivoNode = null;
        
        
        //obtengo todos los nodos de archivo
        NodeList listaArchiv = doc.getElementsByTagName("ARCH");
        
        for(int i = 0; i < listaArchiv.getLength(); i++){
            if(listaArchiv.item(i).getFirstChild().getNodeValue().equals(nombreArchivo)){
                 //el primer nodo que encuentro con esa valor me sirve
                nombreArchivoNode = listaArchiv.item(i).getFirstChild();
            }
                
        }
 
        //busco al nodo "nombre del documento" que es tio del nodo nombreArchivo
        
        //Me paro en el nodo <ARCH> y me muevo hacia el hermano <ARCHNAME> QUE ES LO QUE QUIERO
        Node nombreDocumentoNode =  nombreArchivoNode.getParentNode().getPreviousSibling().getPreviousSibling().getPreviousSibling();
        
        //me muevo hacia el unico nodo texto y selecciono el nombre del documento y fin
        nombreDocumento = nombreDocumentoNode.getFirstChild().getNodeValue();
        
        return nombreDocumento;
        
    }
    public String obtenerLocalizacionDocumento(String nombreArchivo) throws Exception{
        String locaDocumento = null;
        
        Document doc = crearDocumentoXMLCargar();
        List<String> arch = new ArrayList<String>();
        
       
        Node nombreArchivoNode = null;
        
        
        //obtengo todos los nodos de archivo
        NodeList listaArchiv = doc.getElementsByTagName("ARCH");
        
        for(int i = 0; i < listaArchiv.getLength(); i++){
            if(listaArchiv.item(i).getFirstChild().getNodeValue().equals(nombreArchivo)){
                 //el primer nodo que encuentro con esa valor me sirve
                nombreArchivoNode = listaArchiv.item(i).getFirstChild();
            }
                
        }
 
        //busco al nodo "localizacion del documento" que es tio del nodo nombreArchivo
        
        //Me paro en el nodo <ARCH> y me muevo hacia el hermano <LOCA> QUE ES LO QUE QUIERO
        Node locaDocumentoNode =  nombreArchivoNode.getParentNode().getNextSibling();
        
        //me muevo hacia el unico nodo texto y selecciono el nombre del documento y fin
        locaDocumento = locaDocumentoNode.getFirstChild().getNodeValue();
        
        return locaDocumento;
        
    }
    public List<String> obtenerArchivos() throws Exception {
        Document doc = crearDocumentoXMLCargar();
        List<String> arch = new ArrayList<String>();

        NodeList listaArchiv = doc.getElementsByTagName("ARCH");


        for (int i = 0; i < listaArchiv.getLength(); i++) {
            Node archNode = listaArchiv.item(i);


            if (archNode.getNodeType() == Node.ELEMENT_NODE) {
                //obtengo el nodo texto del archivo usando este metodo getFirstChild ya que se que tiene un solo hijo: el texo
                arch.add(archNode.getFirstChild().getNodeValue());  
                
            }
        }
        List<String> archSinRepetir = new ArrayList<String>();
        if (arch.size() > 0) {
            
            for(int i = 0; i < arch.size(); i++)
                 if(!archSinRepetir.contains(arch.get(i)))
                     archSinRepetir.add(arch.get(i));
            
        }
        return archSinRepetir;
    }
    private String getTagValue(String sTag, Element eElement) {
        /* Este metodo se utiliza cuando dentro del eElement solo hay un tag del tipo buscado en 
         * este caso solo tien un NAME y dentro de NAME solo existe el texto.
         */
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();

    }

    /**
     * Este método devuelve los términos con su nombre y sus palabras claves.
     * @param glosa
     * @return List<Termino>
     */
    public List<TerminoUtil> obtenerTerminosUnGlosario(Glosario glosa) throws Exception {
       
        Document miDocumento = crearDocumentoXMLCargar();
        
        Element raiz = miDocumento.getDocumentElement();
        Element glosarioSeleccionado = null;
        List<TerminoUtil> listTerminos = new ArrayList<TerminoUtil>();
        
        //Busco el glosario pasado por parametro
        NodeList nlGlosarios = raiz.getElementsByTagName("GLOSSARY");
        for (int i = 0; i < nlGlosarios.getLength(); i++) {
            if (getTagValue("NAME", (Element) nlGlosarios.item(i)).equals(glosa.getNombre())) {
                glosarioSeleccionado = (Element) nlGlosarios.item(i);
            }

        }
        
          
         TerminoUtil ter = null;
         Element terminoSeleccionado = null;
         
         //obtengo todos los términos del glosario seleccionado.
         NodeList nlTerminos = glosarioSeleccionado.getElementsByTagName("ENTRY");
        
         for (int i = 0; i < nlTerminos.getLength(); i++) {
              
             ter = new TerminoUtil();
              Termino termin = new Termino();
              terminoSeleccionado = (Element) nlTerminos.item(i);
              //Cargo el nombre
              Node concep =  terminoSeleccionado.getElementsByTagName("CONCEPT").item(0);
              termin.setNombre(concep.getChildNodes().item(0).getNodeValue());
              
              ter.setTermino(termin);
              listTerminos.add(ter);
              
              
              //Cargo las categorias
              Element catego = (Element) terminoSeleccionado.getElementsByTagName("CATEGORY").item(0);
              Categoria nuevaCat = new Categoria();
              nuevaCat.setNombre(catego.getElementsByTagName("NAME").item(0).getChildNodes().item(0).getNodeValue());
              termin.setCategoria(nuevaCat);
              
              
              //Cargo el número de definiciones
              Integer numeroDefini = terminoSeleccionado.getElementsByTagName("DEFINITION").getLength();
              ter.setNumeroDefiniciones(numeroDefini);
              
              
              //Cargo las palabras claves
              List<String> palabras = new ArrayList<String>();
              NodeList nlPalabrasClaves = terminoSeleccionado.getElementsByTagName("ALIAS");
                          
              for(int j = 0; j < nlPalabrasClaves.getLength(); j++){
                 palabras.add(nlPalabrasClaves.item(j).getChildNodes().item(0).getNodeValue());   
                  
              }
              termin.setPalabrasClaves(palabras);
              termin.setGlosa(glosa);
        }
         
         
         //Ahora cargo las palabras claves.
         
         
        
        return listTerminos;
    }

    public Termino obtenerTerminoDadoSuNombre(Termino term) throws Exception {
        Document miDocumento = crearDocumentoXMLCargar();
        
        Element raiz = miDocumento.getDocumentElement();
        NodeList nlTerminos = raiz.getElementsByTagName("ENTRY");
        Element terminoSeleccionado = null;
        
        for (int i = 0; i < nlTerminos.getLength(); i++) {
              
             
              
              terminoSeleccionado = (Element) nlTerminos.item(i);
             
              //Cargo el nombre
              Node concep =  terminoSeleccionado.getElementsByTagName("CONCEPT").item(0);
             
              if(term.getNombre().equals(concep.getChildNodes().item(0).getNodeValue())){
                  
                    //Cargo las categorias
                   Element catego = (Element) terminoSeleccionado.getElementsByTagName("CATEGORY").item(0);
                   Categoria nuevaCat = new Categoria();
                   nuevaCat.setNombre(catego.getElementsByTagName("NAME").item(0).getChildNodes().item(0).getNodeValue());
                   term.setCategoria(nuevaCat);
                   
                   //Cargo las palabras claves
                   List<String> palabras = new ArrayList<String>();
                   NodeList nlPalabrasClaves = terminoSeleccionado.getElementsByTagName("ALIAS");
                          
                   for(int j = 0; j < nlPalabrasClaves.getLength(); j++){
                      palabras.add(nlPalabrasClaves.item(j).getChildNodes().item(0).getNodeValue());   
                  
                   }                   
                   term.setPalabrasClaves(palabras);
                   
                   
                   //Cargo las Definiciones
                   NodeList nlDefiniciones = terminoSeleccionado.getElementsByTagName("DEFINITION");
                   for(int m = 0; m < nlDefiniciones.getLength(); m++){
                       
                       DifinicionAutor difiAutor = new DifinicionAutor();
                       difiAutor.setDia(((Element)nlDefiniciones.item(m)).getElementsByTagName("DIA").item(0).getFirstChild().getNodeValue());
                       difiAutor.setMes(((Element)nlDefiniciones.item(m)).getElementsByTagName("MES").item(0).getFirstChild().getNodeValue());
                       difiAutor.setAnno(((Element)nlDefiniciones.item(m)).getElementsByTagName("ANNO").item(0).getFirstChild().getNodeValue());
                       difiAutor.setAutor(((Element)nlDefiniciones.item(m)).getElementsByTagName("AUTOR").item(0).getFirstChild().getNodeValue());
                       difiAutor.setDefinición(((Element)nlDefiniciones.item(m)).getElementsByTagName("INTRO").item(0).getFirstChild().getNodeValue());
                       difiAutor.setNombreArchivo(((Element)nlDefiniciones.item(m)).getElementsByTagName("ARCH").item(0).getFirstChild().getNodeValue());
                       difiAutor.setNombreArticulo(((Element)nlDefiniciones.item(m)).getElementsByTagName("ARCHNAME").item(0).getFirstChild().getNodeValue());
                       difiAutor.setLocalizacionPC(((Element)nlDefiniciones.item(m)).getElementsByTagName("LOCA").item(0).getFirstChild().getNodeValue());
                       difiAutor.setPagDefinicion(Integer.parseInt(((Element)nlDefiniciones.item(m)).getElementsByTagName("PAG").item(0).getFirstChild().getNodeValue()));
                       term.getDefiniciones().add(difiAutor);
                   }
                   
              }
              
              
                       
              
              
              
              
              
        }
        return term;
    }

    public void annadirDifinicion(Termino term) throws Exception {
        Document miDocumento = crearDocumentoXMLCargar();
        Element raiz = miDocumento.getDocumentElement();
        
        NodeList nlTerminos = raiz.getElementsByTagName("ENTRY");
        Element terminoSeleccionado = null;
        
        
        for (int i = 0; i < nlTerminos.getLength(); i++) {
              
              terminoSeleccionado = (Element) nlTerminos.item(i);
             
              //Cargo el nombre
              Node concep =  terminoSeleccionado.getElementsByTagName("CONCEPT").item(0);
             
              if(term.getNombre().equals(concep.getChildNodes().item(0).getNodeValue())){
                  
                  Element nlDefinitions = (Element) terminoSeleccionado.getElementsByTagName("DEFINITIONS").item(0);
                  
                  //Creo la difincion a insertar
                  Element definicion = miDocumento.createElement("DEFINITION");
                  Element introDefinicion = miDocumento.createElement("INTRO");
                  Element nombreDocumento = miDocumento.createElement("ARCHNAME");
                  Element autorDocumento = miDocumento.createElement("AUTOR");
                  Element pagDocumento = miDocumento.createElement("PAG");
                  Element archivo = miDocumento.createElement("ARCH");
                  Element localizacion = miDocumento.createElement("LOCA");
            
                  Element fecha = miDocumento.createElement("FECHA");
                  Element dia = miDocumento.createElement("DIA");
                  Element mes = miDocumento.createElement("MES");
                  Element anno = miDocumento.createElement("ANNO");
            
                  fecha.appendChild(dia);
                  fecha.appendChild(mes);
                  fecha.appendChild(anno);
            
                  Text textDiaFecha = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getDia());
                  Text textMesFecha = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getMes());
                  Text textAnnoFecha = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getAnno());
            
                  dia.appendChild(textDiaFecha);
                  mes.appendChild(textMesFecha);
                  anno.appendChild(textAnnoFecha);
            
                  definicion.appendChild(fecha);
                  
                  definicion.appendChild(introDefinicion);
                  definicion.appendChild(nombreDocumento);
                  definicion.appendChild(autorDocumento);
                  definicion.appendChild(pagDocumento);
                  definicion.appendChild(archivo);
                  definicion.appendChild(localizacion);


                  //textos
                  Text textIntroDefinicion = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getDefinición());
                  Text textNombreDocumentoDefinicion = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getNombreArticulo());
                  Text textAutorDocumentoDefinicion = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getAutor());
                  Text textPagDocumentoDefinicion = miDocumento.createTextNode(String.valueOf(term.getDefiniciones().get(term.getDefiniciones().size()-1).getPagDefinicion()));
                  Text textArchivoDocumentoDefinicion = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getNombreArchivo());
                  Text textLocalizacionDocumentoDefinicion = miDocumento.createTextNode(term.getDefiniciones().get(term.getDefiniciones().size()-1).getLocalizacionPC());

                  introDefinicion.appendChild(textIntroDefinicion);
                  nombreDocumento.appendChild(textNombreDocumentoDefinicion);
                  autorDocumento.appendChild(textAutorDocumentoDefinicion);
                  pagDocumento.appendChild(textPagDocumentoDefinicion);
                  archivo.appendChild(textArchivoDocumentoDefinicion);
                  localizacion.appendChild(textLocalizacionDocumentoDefinicion);
                  
                  nlDefinitions.appendChild(definicion);
              }
        }
        
        
        salvarXML(miDocumento);
        
    }

   
}
