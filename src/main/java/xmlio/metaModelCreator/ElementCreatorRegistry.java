package xmlio.metaModelCreator;

import metaModel.MinispecElement;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Registre des créateurs d'éléments.
 * Permet d'ajouter dynamiquement de nouveaux créateurs sans modifier le code existant (OCP).
 */
public class ElementCreatorRegistry {
    
    private final List<ElementCreator<?>> creators;
    
    public ElementCreatorRegistry() {
        this.creators = new ArrayList<>();
        registerDefaultCreators();
    }
    
    /**
     * Enregistre les créateurs par défaut
     */
    private void registerDefaultCreators() {
        register(new ModelCreator());
        register(new EntityCreator());
        register(new AttributeCreator());
        register(new ReferenceCreator());
        register(new CollectionTypeCreator());
    }
    
    /**
     * Enregistre un nouveau créateur
     */
    public void register(ElementCreator<?> creator) {
        creators.add(creator);
    }
    
    /**
     * Trouve le créateur approprié pour un élément XML donné
     */
    public ElementCreator<?> findCreator(Element xmlElement) {
        for (ElementCreator<?> creator : creators) {
            if (creator.canHandle(xmlElement)) {
                return creator;
            }
        }
        return null;
    }
    
    /**
     * Crée un élément en utilisant le créateur approprié
     */
    public MinispecElement createElement(Element xmlElement, CreationContext context) {
        ElementCreator creator = findCreator(xmlElement);
        if (creator != null) {
            return creator.create(xmlElement, context);
        }
        return null;
    }
    
    /**
     * Remplit les détails d'un élément en utilisant le créateur approprié
     */
    @SuppressWarnings("unchecked")
    public void fillElementDetails(MinispecElement element, Element xmlElement, CreationContext context) {
        ElementCreator creator = findCreator(xmlElement);
        if (creator != null) {
            creator.fillDetails(element, xmlElement, context);
        }
    }
}
