package visitor.java;

import visitor.CodeGenVisitor;

public class JavaVisitor extends CodeGenVisitor {

    public JavaVisitor() {
        super(
                new ModelJavaCodeGenDelegator(),
                new EntityJavaCodeGenDelegator(),
                new AttributeJavaCodeGenDelegator(),
                new TypeJavaCodeGenDelegator(),
                new CollectionJavaCodeGenDelegator(),
                new SimpleTypeJavaCodeGenDelegator(),
                new ResolvedReferenceJavaCodeGenDelegator(),
                new UnresolvedReferenceJavaCodeGenDelegator(),
                new ArrayTypeJavaCodeGenDelegator(),
                new ListTypeJavaCodeGenDelegator(),
                new SetTypeJavaCodeGenDelegator(),
                new BagTypeJavaCodeGenDelegator()
        );
    }
}
