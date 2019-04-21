import kotlin.Unit;
import osu.framework.bindables.Bindable;
import osu.framework.bindables.BindableInt;

public class BindableExample {

    public static void main(String[] args) {
        initialization();
        valueChanged();
        bindValueChanged();
        intBindables();
    }

    private static void initialization() {
        Bindable<String> bindable = new Bindable<>("Hello", String.class);

        bindable.setValue(bindable.getValue() + " World!");

        System.out.println(bindable.getValue().toUpperCase());
    }

    private static void valueChanged() {
        Bindable<String> bindable = new Bindable<>("", String.class);

        bindable.getValueChangedEvent().addListener(evt -> {
            System.out.println(evt.getNewValue());
            return Unit.INSTANCE;
        });

        bindable.setValue("Hello World!");
    }

    private static void bindValueChanged() {
        Bindable<String> bindable = new Bindable<>("", String.class);

        bindable.bindValueChanged(false, evt -> {
            System.out.println(evt.getNewValue());
            return Unit.INSTANCE;
        });

        bindable.setValue("Hello World!");
    }

    private static void intBindables() {
        BindableInt bindable1 = new BindableInt(1);
        BindableInt bindable2 = new BindableInt();
        bindable2.bindTo(bindable1);

        bindable1.setDisabled(true);

        try {
            bindable2.setValue(3);
        } catch (UnsupportedOperationException e) {
            System.out.println("Working as intended");
        }
    }
}
