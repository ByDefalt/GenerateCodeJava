package xmlio.metaModelCreator;

import metaModel.MetaModelElement;
import metaModel.minispec.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * XMLAnalyser refactorisé selon les principes SOLID.
 * 
 * Changements principaux :
 * - Séparation des responsabilités en classes distinctes
 * - Utilisation du pattern Strategy pour les créateurs d'éléments
 * - Détection de cycles isolée dans sa propre classe
 * - Résolution de types dans une classe dédiée
 * - Pattern Registry pour l'extensibilité (OCP)
 * <p>
 * Pour étendre le système avec de nouveaux types d'éléments :
 * 1. Créer une classe implémentant ElementCreator
 * 2. L'enregistrer via getContext().getCreatorRegistry().register(...)
 * <p>
 * Exemple :
 * <code>
 * XMLAnalyser analyser = new XMLAnalyser();
 * analyser.getContext().getCreatorRegistry().register(new MyCustomCreator());
 * </code>
 */
public class XMLAnalyser {
    
    private final XMLAnalyserContext context;
    private final XMLIndexer indexer;
    
    public XMLAnalyser() {
        this.context = new XMLAnalyserContext();
        this.indexer = new XMLIndexer(context);
    }
    
    /**
     * Charge un modèle à partir d'un flux d'entrée
     */
    public MetaModelElement getModelFromInputStream(InputStream stream) {
        try {
            // Réinitialiser le contexte pour une nouvelle analyse
            context.reset();
            
            // Parser le document XML
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(stream);
            Element root = doc.getDocumentElement();

            // Indexer tous les éléments XML
            indexer.indexElements(root);

            // Trouver et créer le modèle principal
            return findAndCreateModel(root);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Trouve et crée le modèle à partir de l'élément racine
     */
    private MetaModelElement findAndCreateModel(Element root) {
        String modelId = null;
        if (root.getTagName().contains("-code")) {
            modelId = root.getAttribute("id");
        }
        else{
            modelId = root.getAttribute("model");
        }
        if (!modelId.isEmpty()) {
            return context.getOrCreateElement(modelId);
        }

        // Méthode 2 : Chercher un élément Model dans l'index
        for (Element el : context.getXmlElementIndex().values()) {
            if ("Model".equals(el.getTagName())) {
                return context.getOrCreateElement(el.getAttribute("id"));
            }
        }
        
        return null;
    }
    
    /**
     * Charge un modèle à partir d'une chaîne de caractères
     */
    public MetaModelElement getModelFromString(String content) {
        return getModelFromInputStream(new ByteArrayInputStream(content.getBytes()));
    }
    
    /**
     * Charge un modèle à partir d'un fichier
     */
    public MetaModelElement getModelFromFile(File file) {
        try {
            return getModelFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            context.reportError("Fichier introuvable : " + file.getAbsolutePath());
            return null;
        }
    }
    
    /**
     * Charge un modèle à partir du nom d'un fichier
     */
    public MetaModelElement getModelFromFilenamed(String filename) {
        return getModelFromFile(new File(filename));
    }
    
    /**
     * Donne accès au contexte pour une configuration avancée
     * (par exemple, pour ajouter des créateurs personnalisés)
     */
    public XMLAnalyserContext getContext() {
        return context;
    }
}
