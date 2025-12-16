package visitor;

public interface CodeGenDelegator {

    void delegate(Object element, Visitor visitor);
}
