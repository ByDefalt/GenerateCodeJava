package prettyPrinter;


import metaModel.MetaModelElement;
import org.junit.jupiter.api.Test;

import metaModel.minispec.Model;
import xmlio.metaModelCreator.XMLAnalyser;

class PrettyPrinterTest {

	@Test
	void test() {
		XMLAnalyser analyser = new XMLAnalyser();
		MetaModelElement model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
		PrettyPrinter pp = new PrettyPrinter();
		model.accept(pp);
		System.out.println(pp.result());
	}

}
