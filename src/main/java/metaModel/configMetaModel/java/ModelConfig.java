package metaModel.configMetaModel.java;

import metaModel.configMetaModel.ConfigElement;
import visitor.Visitor;

public class ModelConfig implements ConfigElement {

    private final String name;
    private final String packageName;

    public ModelConfig(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }
    @Override
    public void accept(Visitor v) {

    }



    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }


    @Override
    public String toString() {
        return "ModelConfig{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
