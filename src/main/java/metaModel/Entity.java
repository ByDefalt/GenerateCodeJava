package metaModel;

import visitor.Visitor;

import java.util.List;

public class Entity implements MinispecElement {
	private final String name;
	private final Entity superEntity;
	private final List<Attribute> attributes;

    public Entity(String name, Entity superEntity, List<Attribute> attributes) {
        this.name = name;
        this.superEntity = superEntity;
        this.attributes = attributes;
    }

	public Entity getSuperEntity() {
		return superEntity;
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
