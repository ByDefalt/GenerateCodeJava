package xmlio.metaModelCreator;

import metaModel.MetaModelElement;
import metaModel.minispec.MinispecElement;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.minispec.CircularDependencyDetector;
import xmlio.metaModelCreator.minispec.DoubleNameExtendsDetector;
import xmlio.metaModelCreator.minispec.TypeResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation concrète du contexte de création utilisé par XMLAnalyser.
 */
public class XMLAnalyserContext implements CreationContext {

    private final Map<String, MetaModelElement> minispecIndex;
    private final Map<String, Element> xmlElementIndex;
    private final CircularDependencyDetector cycleDetector;
    private final TypeResolver typeResolver;
    private final ElementCreatorRegistry creatorRegistry;
    private final DoubleNameExtendsDetector doubleNameExtendsDetector;

    public XMLAnalyserContext() {
        this.minispecIndex = new HashMap<>();
        this.xmlElementIndex = new HashMap<>();
        this.cycleDetector = new CircularDependencyDetector();
        this.typeResolver = new TypeResolver(this);
        this.creatorRegistry = new ElementCreatorRegistry();
        this.doubleNameExtendsDetector = new DoubleNameExtendsDetector();
    }

    @Override
    public MetaModelElement getOrCreateElement(String id) {
        if (id == null || !id.startsWith("#")) {
            return null;
        }
        // 1. Si déjà en cache, on retourne
        if (minispecIndex.containsKey(id)) {
            return minispecIndex.get(id);
        }

        // 2. Détection de cycle
        if (!cycleDetector.startCreating(id)) {
            reportError("CYCLE DÉTECTÉ : L'élément " + id + " dépend de lui-même. "
                    + "Chemin: " + cycleDetector.getCreationPath() + " -> " + id);
            return null;
        }

        try {
            // 3. Recherche de l'élément XML
            Element xmlElement = xmlElementIndex.get(id);
            if (xmlElement == null) {
                reportError("ID introuvable dans le XML : " + id);
                return null;
            }

            // 4. Création de l'élément
            MetaModelElement createdObject = creatorRegistry.createElement(xmlElement, this);

            // 5. Enregistrement et remplissage
            if (createdObject != null) {
                minispecIndex.put(id, createdObject);
                creatorRegistry.fillElementDetails(createdObject, xmlElement, this);
            }

            return createdObject;

        } finally {
            // 6. Libération du marqueur de création
            cycleDetector.finishCreating(id);
        }
    }

    @Override
    public Element getXmlElement(String id) {
        return xmlElementIndex.get(id);
    }

    @Override
    public void registerElement(String id, MetaModelElement element) {
        minispecIndex.put(id, element);
    }

    @Override
    public void reportError(String message) {
        System.err.println("[XML ERREUR] " + message);
    }

    @Override
    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    @Override
    public DoubleNameExtendsDetector getDoubleNameDetector() {
        return doubleNameExtendsDetector;
    }

    @Override
    public List<Element> findChildElements(String parentId, String childTagName, String parentAttribute) {
        List<Element> result = new ArrayList<>();
        for (Map.Entry<String, Element> entry : xmlElementIndex.entrySet()) {
            Element childEl = entry.getValue();
            if (childTagName.equals(childEl.getTagName())
                    && parentId.equals(childEl.getAttribute(parentAttribute))) {
                result.add(childEl);
            }
        }
        return result;
    }

    @Override
    public List<Element> findChildElements(String parenTagName) {
        List<Element> result = new ArrayList<>();
        for (Map.Entry<String, Element> entry : xmlElementIndex.entrySet()) {
            Element childEl = entry.getValue();
            if (parenTagName.equals(childEl.getTagName())) {
                result.add(childEl);
            }
        }
        return result;
    }

    /**
     * Ajoute un élément XML à l'index
     */
    public void indexXmlElement(String id, Element xmlElement) {
        xmlElementIndex.put(id, xmlElement);
    }

    /**
     * Récupère un élément déjà créé du cache
     */
    public MetaModelElement getCachedElement(String id) {
        return minispecIndex.get(id);
    }

    /**
     * Accès à l'index XML complet (pour les créateurs qui en ont besoin)
     */
    public Map<String, Element> getXmlElementIndex() {
        return xmlElementIndex;
    }

    /**
     * Accès au registre de créateurs (permet d'ajouter des créateurs personnalisés)
     */
    public ElementCreatorRegistry getCreatorRegistry() {
        return creatorRegistry;
    }

    /**
     * Réinitialise le contexte pour une nouvelle analyse
     */
    public void reset() {
        minispecIndex.clear();
        xmlElementIndex.clear();
        cycleDetector.reset();
        doubleNameExtendsDetector.reset();
    }
}