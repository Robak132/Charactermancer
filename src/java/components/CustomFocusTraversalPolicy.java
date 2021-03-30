package components;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import java.util.List;

public class CustomFocusTraversalPolicy extends FocusTraversalPolicy {
    private final List<Component> order;

    public CustomFocusTraversalPolicy(List<Component> order) {
        this.order = order;
    }

    @Override
    public Component getComponentAfter(Container container, Component component) {
        int index = (order.indexOf(component) + 1) % order.size();
        Component after = order.get(index);
        while (index < order.size() && !(after.isEnabled() && after.isVisible())) {
            after = order.get(index);
            index++;
        }
        return after;
    }
    @Override
    public Component getComponentBefore(Container container, Component component) {
        int index = (order.indexOf(component) - 1);
        if (index < 0) {
            index = order.size() - 1;
        }
        Component before = order.get(index);
        while (index >= 0 && !(before.isEnabled() && before.isVisible())) {
            before = order.get(index);
            index --;
        }
        return before;
    }
    @Override
    public Component getFirstComponent(Container container) {
        int index = 0;
        Component first = order.get(index);
        while (index < order.size() && !(first.isEnabled() && first.isVisible())) {
            first = order.get(index);
            index++;
        }
        return first;
    }
    @Override
    public Component getLastComponent(Container container) {
        int index = order.size() - 1;
        Component last = order.get(index);
        while (index >= 0 && !(last.isEnabled() && last.isVisible())) {
            last = order.get(index);
            index--;
        }
        return last;
    }
    @Override
    public Component getDefaultComponent(Container container) {
        return getFirstComponent(container);
    }
}