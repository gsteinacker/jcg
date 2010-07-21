package test.types;

/**
 * A comment
 */
public class Class03 {
    
    public final static class NestedClass {
        public NestedClass() {
            System.out.println("NestedClass");
        }
    }
    public enum NestedEnum { ONE, TWO }

    public interface NestedInterface {
        public void foo();
    }

}
