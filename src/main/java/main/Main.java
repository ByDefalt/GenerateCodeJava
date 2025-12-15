package main;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import prettyPrinter.PrettyPrinter;
import visitor.JavaVisitor;

public class Main {
    public static void main(String[] args){
        XMLAnalyser analyser = new XMLAnalyser();

        Model model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");

        if (model != null) {
            JavaVisitor javaVisitor = new JavaVisitor();
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
