package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class PrettyPrinter extends Visitor {
	String result = "";
	
	public String result() {
		return result;
	}
	
	public void visitModel(Model e) {
		result = "model ;\n\n";
		
		for (Entity n : e.getEntities()) {
			n.accept(this);
		}
		result = result + "end model\n";
	}
	
	public void visitEntity(Entity e) {
		result = result + "entity " + e.getName();
		for (Attribute a : e.getAttributes()) {
			a.accept(this);
		}
		result = result + "\nend entity;\n";
	}

	@Override
	public void visitAttribute(Attribute e) {
		result = "\n" + e.getName() + " : " + e.getType();
	}


}
