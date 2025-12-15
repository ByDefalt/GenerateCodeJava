package metaModel;

public class Entity implements MinispecElement {
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void accept(Visitor v) {
		v.visitEntity(this);
	};

	
}
