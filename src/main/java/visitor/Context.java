package visitor;

public class Context {
    public final StringBuilder result = new StringBuilder();
    public final StringBuilder fields = new StringBuilder();
    public final StringBuilder methods = new StringBuilder();
    public final StringBuilder packageName = new StringBuilder();
    public final StringBuilder imports = new StringBuilder();
    public String currentType = "";
}
