package main;

import visitor.java.JavaMetaModelConfiguration;
import metaModel.minispec.Model;
import prettyPrinter.PrettyPrinter;
import visitor.CodeGenVisitor;
import visitor.java.JavaVisitor;
import xmlio.metaModelCreator.XMLAnalyser;

public class Main {
    public static void main(String[] args){
        XMLAnalyser analyser = new XMLAnalyser();

        Model model = (Model) analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
        JavaMetaModelConfiguration modelConfig = (JavaMetaModelConfiguration) analyser.getModelFromFilenamed("src/main/resources/javaConfig.xml");

        if (model != null) {
            CodeGenVisitor javaVisitor = new JavaVisitor();
            javaVisitor.setMetaModelConfiguration(modelConfig);
            model.accept(javaVisitor);
            System.out.println(javaVisitor.getResult());
            PrettyPrinter prettyPrinter = new PrettyPrinter();
            model.accept(prettyPrinter);
            System.out.println(prettyPrinter.result());
        } else {
            System.out.println("Erreur: impossible de charger le modèle");
        }
    }
}
