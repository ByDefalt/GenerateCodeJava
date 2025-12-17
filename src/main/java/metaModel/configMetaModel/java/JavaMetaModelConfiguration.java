package metaModel.configMetaModel.java;

import metaModel.configMetaModel.ConfigElement;
import metaModel.configMetaModel.MetaModelConfiguration;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class JavaMetaModelConfiguration implements MetaModelConfiguration, ConfigElement {

    private final List<ModelConfig> modelConfigs;
    private final List<PrimitiveConfig> primitiveConfigs;



    public JavaMetaModelConfiguration() {
        this.modelConfigs = new ArrayList<>();
        this.primitiveConfigs = new ArrayList<>();
    }

    public List<ModelConfig> getModelConfigs() {
        return modelConfigs;
    }

    public List<PrimitiveConfig> getPrimitiveConfigs() {
        return primitiveConfigs;
    }

    @Override
    public void accept(Visitor v) {

    }


    @Override
    public String toString() {
        return "JavaMetaModelConfiguration{" +
                "modelConfigs=" + modelConfigs +
                ", primitiveConfigs=" + primitiveConfigs +
                '}';
    }
}
