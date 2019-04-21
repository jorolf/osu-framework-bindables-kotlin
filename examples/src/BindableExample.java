import osu.framework.bindables.Bindable;

public class BindableExample {

    public static void main(String[] args) {
        initialization();
    }

    private static void initialization() {
        Bindable<String> bindable = new Bindable<>("Hello", String.class);

        bindable.setValue(bindable.getValue() + " World!");

        System.out.println(bindable.getValue().toUpperCase());
    }
}
