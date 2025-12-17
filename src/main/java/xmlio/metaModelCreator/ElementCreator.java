package xmlio.metaModelCreator;

import metaModel.MetaModelElement;
import org.w3c.dom.Element;

/**
 * Interface pour la création d'éléments du métamodèle à partir d'éléments XML.
 * Utilise des génériques pour éviter les casts.
 */
public interface ElementCreator<T extends MetaModelElement> {
    
    /**
     * Vérifie si ce créateur peut gérer l'élément XML donné
     */
    boolean canHandle(Element xmlElement);
    
    /**
     * Crée une instance typée à partir de l'élément XML
     */
    T create(Element xmlElement, CreationContext context);
    
    /**
     * Remplit les détails de l'objet typé après sa création initiale
     */
    void fillDetails(T element, Element xmlElement, CreationContext context);
}
