package metaModel;

import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Model implements MinispecElement {

	private final List<Entity> entities;
    private final String name;

    public Model (String name) {
		this.entities = new ArrayList<>();
        this.name = name;
	}


    public String getName() {
        return name;
    }

	public List<Entity> getEntities() {
		return entities;
	}

    public void accept(Visitor v) {
        v.visitModel(this);
    }
	
}
