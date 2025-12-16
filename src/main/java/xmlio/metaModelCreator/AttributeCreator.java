package xmlio.metaModelCreator;

import metaModel.Attribute;
import metaModel.types.Type;
import org.w3c.dom.Element;

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
        return new Attribute(name, type);
    }
    
    @Override
    public void fillDetails(Attribute element, Element xmlElement, CreationContext context) {
        // Les attributs n'ont pas de détails à remplir après création
    }
}
