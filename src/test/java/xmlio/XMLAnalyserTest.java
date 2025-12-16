package xmlio;




import org.junit.jupiter.api.Test;

import metaModel.Model;

import static org.junit.jupiter.api.Assertions.*;

class XMLAnalyserTest {
	/**
	 * Test de l'analyseur XML sur un fichier exemple
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <Root model="#1">
	 *     <!-- Modèle -->
	 *     <Model id="#1" name="ExempleWithCollections"/>
	 *
	 *     <!-- Entité Flotte avec collection de Satellites -->
	 *     <Entity id="#2" model="#1" name="Flotte" />
	 *     <Attribute id="#3" entity="#2" name="nom" type="String" />
	 *     <Attribute id="#4" entity="#2" name="satellites" type="List" of="Satellite" min="1" max="10" />
	 *
	 *     <!-- Entité Satellite avec référence à Flotte et collection de PanneauSolaire -->
	 *     <Entity id="#5" model="#1" name="Satellite" />
	 *     <Attribute id="#6" entity="#5" name="nom" type="String" />
	 *     <Attribute id="#7" entity="#5" name="id" type="Integer" />
	 *     <Attribute id="#8" entity="#5" name="parent" type="Flotte" />
	 *     <Attribute id="#9" entity="#5" name="panneaux" type="Array" of="PanneauSolaire" size="2" />
	 *
	 *     <!-- Entité PanneauSolaire -->
	 *     <Entity id="#10" model="#1" name="PanneauSolaire" />
	 *     <Attribute id="#11" entity="#10" name="puissance" type="Double" />
	 *     <Attribute id="#12" entity="#10" name="etat" type="String" />
	 * </Root>
	 */
	@Test
	void test1() {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
        assertNotNull(model);
        assertEquals(3, model.getEntities().size());
		assertEquals(2, model.getEntities().get(0).getAttributes().size());
		assertEquals(4, model.getEntities().get(1).getAttributes().size());
		assertEquals(2, model.getEntities().get(2).getAttributes().size());
		assertEquals("Flotte", model.getEntities().get(0).getName());
		assertEquals("Satellite", model.getEntities().get(1).getName());
		assertEquals("PanneauSolaire", model.getEntities().get(2).getName());
		assertEquals("Array", model.getEntities().get(1).getAttributes().get(3).getType().getCollectionType());
	}
	
	@Test
	void test3() {
		String src = "<Root model=\"3\"> <Model id=\"3\" /> </Root>";
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromString(src);
        assertNotNull(model);
        assertEquals(0, model.getEntities().size());
	}

}