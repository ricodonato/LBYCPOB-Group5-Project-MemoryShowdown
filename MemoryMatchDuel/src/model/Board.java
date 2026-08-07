package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * Holds the set of cards for one round, sized by Difficulty and skinned by Theme.
 */
public class Board {

    private final List<Card> cards;
    private final Difficulty difficulty;
    private final Theme theme;

    public Board(Difficulty difficulty, Theme theme) {
        this.difficulty = difficulty;
        this.theme = theme;
        this.cards = generateCards(difficulty.getCardCount(), theme);
    }

    private List<Card> generateCards(int size, Theme theme) {
        List<Card> generated = new ArrayList<>();
        List<String> symbols = theme.getSymbols();
        int pairsNeeded = size / 2;
        int id = 0;
        for (int i = 0; i < pairsNeeded; i++) {
            String symbol = symbols.get(i % symbols.size());
            generated.add(new Card(id++, symbol, theme.name()));
            generated.add(new Card(id++, symbol, theme.name()));
        }
        Collections.shuffle(generated);
        return generated;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card getCardById(int id) {
        return cards.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public boolean isFullyMatched() {
        return cards.stream().allMatch(Card::isMatched);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Theme getTheme() {
        return theme;
    }
}
