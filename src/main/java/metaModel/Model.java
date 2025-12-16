package metaModel;

import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Model implements MinispecElement {

	List<Entity> entities;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public Model(){
        this.entities = new ArrayList<>();
    }

    public Model (String name) {
		this.entities = new ArrayList<>();
        this.name = name;
	}
	
	public void accept(Visitor v) {
		v.visitModel(this);
	}
	
	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	public List<Entity> getEntities() {
		return entities;
	}
	
}
