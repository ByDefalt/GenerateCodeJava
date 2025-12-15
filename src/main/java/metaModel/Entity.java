package metaModel;

import java.util.List;

public class Entity implements MinispecElement {
	private String name;
	private List<Attribute> attributes;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void accept(Visitor v) {
		v.visitEntity(this);
	};

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}
