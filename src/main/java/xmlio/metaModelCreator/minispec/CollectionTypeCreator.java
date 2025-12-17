package xmlio.metaModelCreator.minispec;

import metaModel.minispec.types.Type;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

/**
 * Créateur spécialisé pour les types de collection (Array, List, Set, Bag)
 */
public class CollectionTypeCreator implements ElementCreator<Type> {
    
    @Override
    public boolean canHandle(Element xmlElement) {
        String tagName = xmlElement.getTagName();
        return "Array".equals(tagName) 
            || "List".equals(tagName) 
            || "Set".equals(tagName) 
            || "Bag".equals(tagName);
    }
    
    @Override
    public Type create(Element xmlElement, CreationContext context) {
        String tagName = xmlElement.getTagName();
        return context.getTypeResolver().createCollectionType(xmlElement, tagName);
    }
    
    @Override
    public void fillDetails(Type element, Element xmlElement, CreationContext context) {
        // Les types de collection n'ont pas de détails à remplir après création
    }
}
