package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.List;


public enum Theme {
    ANIMALS(List.of("🐶", "🐱", "🐵", "🦊", "🐸", "🐷", "🐼", "🦁", "🐻", "🐰")),
    SPORTS(List.of("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏓", "🥊", "🎳", "🏸")),
    FOOD(List.of("🍕", "🍔", "🌮", "🍩", "🍦", "🍉", "🍓", "🍿", "🧁", "🍭")),
    EMOJIS(List.of("😀", "😂", "😎", "🥳", "😱", "🤔", "😴", "🤩", "🥶", "🤯"));

    private final List<String> symbols;

    Theme(List<String> symbols) {
        this.symbols = symbols;
    }

    public List<String> getSymbols() {
        return symbols;
    }
}
