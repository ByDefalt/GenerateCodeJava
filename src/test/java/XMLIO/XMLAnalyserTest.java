package XMLIO;




import org.junit.jupiter.api.Test;

import metaModel.Model;

import static org.junit.jupiter.api.Assertions.assertTrue;

class XMLAnalyserTest {

	@Test
	void test1() {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("Exemple1.xml");
		assertTrue(model != null);
		assertTrue(model.getEntities().size() == 0);
	}
	@Test
	void test2() {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("Exemple2.xml");
		assertTrue(model != null);
		System.out.println(model.getEntities().size());
		assertTrue(model.getEntities().size() == 2);
	}
	
	@Test
	void test3() {
		String src = "<Root model=\"3\"> <Model id=\"3\" /> </Root>";
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromString(src);
		assertTrue(model != null);
		assertTrue(model.getEntities().size() == 0);
	}

}