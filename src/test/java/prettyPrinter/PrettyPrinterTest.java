package prettyPrinter;


import org.junit.jupiter.api.Test;

import xmlio.XMLAnalyser;
import metaModel.Model;

class PrettyPrinterTest {

	@Test
	void test() {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
		PrettyPrinter pp = new PrettyPrinter();
		model.accept(pp);
		System.out.println(pp.result());
	}

}
