package xmlio.metaModelCreator.ConfigSpec;

import metaModel.configMetaModel.java.ModelConfig;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

public class ModelConfigCreator implements ElementCreator<ModelConfig> {
    @Override
    public boolean canHandle(Element xmlElement) {
        return "model".equals(xmlElement.getTagName());
    }

    @Override
    public ModelConfig create(Element xmlElement, CreationContext context) {
        String name = xmlElement.getAttribute("name");
        String packageName = xmlElement.getAttribute("package");
        return new ModelConfig(name, packageName);
    }

    @Override
    public void fillDetails(ModelConfig element, Element xmlElement, CreationContext context) {
        //rien à faire pour l'instant
    }
}
