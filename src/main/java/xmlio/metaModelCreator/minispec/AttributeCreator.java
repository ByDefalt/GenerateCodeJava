package xmlio.metaModelCreator.minispec;

import metaModel.minispec.Attribute;
import metaModel.minispec.types.Type;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

/**
 * Créateur spécialisé pour les éléments Attribute
 */
public class AttributeCreator implements ElementCreator<Attribute> {
    
    @Override
    public boolean canHandle(Element xmlElement) {
        return "Attribute".equals(xmlElement.getTagName());
    }
    
    @Override
    public Attribute create(Element xmlElement, CreationContext context) {
        String name = xmlElement.getAttribute("name");
        Type type = context.getTypeResolver().resolveType(xmlElement.getAttribute("type"));
        String initialValue = xmlElement.getAttribute("init");
        if(!initialValue.isEmpty()) {
            return new Attribute(name, type, initialValue);
        }
        return new Attribute(name, type);
    }
    
    @Override
    public void fillDetails(Attribute element, Element xmlElement, CreationContext context) {
        // Les attributs n'ont pas de détails à remplir après création
    }
}
