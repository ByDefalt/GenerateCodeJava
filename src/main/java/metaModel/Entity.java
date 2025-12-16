package metaModel;

import visitor.Visitor;

import java.util.List;

public class Entity implements MinispecElement {
	private final String name;
	private final List<Attribute> attributes;

    public Entity(String name, List<Attribute> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
		return name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void accept(Visitor v) {
		v.visitEntity(this);
	}

}
