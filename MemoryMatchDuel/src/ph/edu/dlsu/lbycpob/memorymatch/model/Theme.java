package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.List;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * Each theme provides up to 10 pair symbols (enough for HARD difficulty,
 * which needs 20 cards / 10 pairs). Emoji are used as card faces so no
 * external image assets are needed for the cartoony look.
 */
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
