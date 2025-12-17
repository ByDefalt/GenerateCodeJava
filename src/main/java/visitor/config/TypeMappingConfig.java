package visitor.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration des mappings de types chargée depuis un fichier XML.
 * Permet de paramétrer la génération de code Java sans modifier le code.
 * 
 * Format XML attendu:
 * <config>
 *   <primitive name="Integer" type="Integer" package="java.util.Array"/>
 *   <primitive name="String" type="String"/>
 *   <primitive name="List" type="List" package="java.util.List"/>
 *   <primitive name="Set" type="Set" package="java.util.HashSet"/>
 *   <primitive name="Bag" type="Bag" package="java.util.Array"/>
 * </config>
 */
public class TypeMappingConfig {
    
    private final Map<String, TypeMapping> typeMappings;
    private String defaultPackage;
    
    public TypeMappingConfig() {
        this.typeMappings = new HashMap<>();
        this.defaultPackage = "";
        initializeDefaultMappings();
    }
    
    /**
     * Initialise les mappings par défaut (pour compatibilité si pas de config)
     */
    private void initializeDefaultMappings() {
        // Types primitifs Java (pas d'import nécessaire)
        addMapping("Integer", "Integer");
        addMapping("String", "String");
        addMapping("Double", "Double");
        addMapping("Boolean", "Boolean");
        addMapping("Long", "Long");
        addMapping("Float", "Float");
        addMapping("int", "int");
        addMapping("double", "double");
        addMapping("boolean", "boolean");
        addMapping("long", "long");
        addMapping("float", "float");
    }
    
    /**
     * Ajoute un mapping simple sans import
     */
    public void addMapping(String primitiveType, String javaType) {
        typeMappings.put(primitiveType, new TypeMapping(primitiveType, javaType));
    }
    
    /**
     * Ajoute un mapping avec imports
     */
    public void addMapping(String primitiveType, String javaType, String... imports) {
        TypeMapping mapping = new TypeMapping(primitiveType, javaType);
        for (String imp : imports) {
            mapping.addImport(imp);
        }
        typeMappings.put(primitiveType, mapping);
    }
    
    /**
     * Récupère le mapping pour un type donné
     */
    public TypeMapping getMapping(String primitiveType) {
        return typeMappings.get(primitiveType);
    }
    
    /**
     * Vérifie si un mapping existe pour ce type
     */
    public boolean hasMapping(String primitiveType) {
        return typeMappings.containsKey(primitiveType);
    }
    
    /**
     * Récupère le type Java correspondant
     */
    public String getJavaType(String primitiveType) {
        TypeMapping mapping = typeMappings.get(primitiveType);
        return mapping != null ? mapping.getJavaType() : primitiveType;
    }
    
    /**
     * Charge la configuration depuis un fichier XML
     */
    public void loadFromXml(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            parseConfiguration(doc);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge la configuration depuis un flux
     */
    public void loadFromXml(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            parseConfiguration(doc);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge la configuration depuis le nom de fichier
     */
    public void loadFromXml(String filename) {
        loadFromXml(new File(filename));
    }
    
    /**
     * Parse le document XML de configuration
     */
    private void parseConfiguration(Document doc) {
        Element root = doc.getDocumentElement();
        
        // Lire le package par défaut si présent
        if (root.hasAttribute("package")) {
            defaultPackage = root.getAttribute("package");
        }
        
        // Parser les éléments <primitive>
        NodeList primitives = root.getElementsByTagName("primitive");
        for (int i = 0; i < primitives.getLength(); i++) {
            Element primitiveEl = (Element) primitives.item(i);
            parsePrimitiveMapping(primitiveEl);
        }
    }
    
    /**
     * Parse un élément <primitive> du XML
     */
    private void parsePrimitiveMapping(Element primitiveEl) {
        String name = primitiveEl.getAttribute("name");
        String type = primitiveEl.getAttribute("type");
        String packageAttr = primitiveEl.getAttribute("package");
        
        if (name.isEmpty() || type.isEmpty()) {
            System.err.println("Élément <primitive> invalide: name et type sont requis");
            return;
        }
        
        TypeMapping mapping = new TypeMapping(name, type);
        
        // Ajouter le package comme import si spécifié
        if (!packageAttr.isEmpty()) {
            mapping.addImport(packageAttr);
        }
        
        typeMappings.put(name, mapping);
    }
    
    /**
     * Récupère tous les mappings configurés
     */
    public Map<String, TypeMapping> getAllMappings() {
        return new HashMap<>(typeMappings);
    }
    
    /**
     * Affiche la configuration actuelle (pour debug)
     */
    public void printConfiguration() {
        System.out.println("=== Configuration des mappings de types ===");
        if (!defaultPackage.isEmpty()) {
            System.out.println("Package par défaut: " + defaultPackage);
        }
        System.out.println("Mappings:");
        typeMappings.values().forEach(mapping -> 
            System.out.println("  " + mapping)
        );
    }
}
