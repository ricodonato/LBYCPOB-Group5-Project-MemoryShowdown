package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.List;

// UNDERSTAND: Enum of the four visual themes, each holding the list of emoji symbols used to build
// cards for that theme.
// DECISION: List.of(...) was used instead of a mutable ArrayList because the symbol set for a theme is
// fixed and should never change at runtime — an immutable list prevents accidental edits.
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