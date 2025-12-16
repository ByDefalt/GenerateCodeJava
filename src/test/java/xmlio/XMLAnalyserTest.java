package xmlio;




import metaModel.types.ArrayType;
import org.junit.jupiter.api.Test;

import metaModel.Model;
import xmlio.metaModelCreator.XMLAnalyser;

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
        assertEquals(2, model.getEntities().size());
	}


}