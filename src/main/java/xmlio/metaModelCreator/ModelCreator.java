package xmlio.metaModelCreator;

import metaModel.Model;
import metaModel.MinispecElement;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Créateur spécialisé pour les éléments Model
 */
public class ModelCreator implements ElementCreator<Model> {
    
    @Override
    public boolean canHandle(Element xmlElement) {
        return "Model".equals(xmlElement.getTagName());
    }
    
    @Override
    public Model create(Element xmlElement, CreationContext context) {
        String name = xmlElement.getAttribute("name");
        return new Model(name);
    }
    
    @Override
    public void fillDetails(Model model, Element xmlElement, CreationContext context) {
        String modelId = xmlElement.getAttribute("id");
        
        List<Element> childElements = context.findChildElements(modelId, "Entity", "model");
        
        for (Element childEl : childElements) {
            String childId = childEl.getAttribute("id");
            MinispecElement childEntity = context.getOrCreateElement(childId);
            if (childEntity != null) {
                model.getEntities().add((metaModel.Entity) childEntity);
            }
        }
    }
}
