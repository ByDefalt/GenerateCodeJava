package metaModel.configMetaModel.java;

import metaModel.configMetaModel.ConfigElement;
import visitor.Visitor;

public class PrimitiveConfig implements ConfigElement {

    private final String name;
    private final String type;
    private final String packageName;

    public PrimitiveConfig(String name, String type, String packageName) {
        this.name = name;
        this.type = type;
        this.packageName = packageName;
    }

    @Override
    public void accept(Visitor v) {

    }
}
