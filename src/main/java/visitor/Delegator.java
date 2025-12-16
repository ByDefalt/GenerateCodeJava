package visitor;

public interface Delegator {

    void delegate(Object element, Visitor visitor);
}
