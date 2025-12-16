package xmlio.metaModelCreator;

import metaModel.MinispecElement;
import org.w3c.dom.Element;
import java.util.List;

/**
 * Contexte de création qui fournit l'accès aux éléments déjà créés
 * et aux services nécessaires pendant la création.
 */
public interface CreationContext {
    
    /**
     * Récupère ou crée un élément à partir de son ID
     */
    MinispecElement getOrCreateElement(String id);
    
    /**
     * Récupère un élément XML à partir de son ID
     */
    Element getXmlElement(String id);
    
    /**
     * Enregistre un élément créé dans le cache
     */
    void registerElement(String id, MinispecElement element);
    
    /**
     * Signale une erreur pendant la création
     */
    void reportError(String message);
    
    /**
     * Accès au résolveur de types
     */
    TypeResolver getTypeResolver();
    
    /**
     * Trouve tous les éléments XML enfants d'un parent donné
     */
    List<Element> findChildElements(String parentId, String childTagName, String parentAttribute);
}
