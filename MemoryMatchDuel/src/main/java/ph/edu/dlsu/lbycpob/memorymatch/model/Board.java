package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// UNDERSTAND: Represents the full grid of cards for one round — built once from a Difficulty (how many
// cards) and a Theme (which symbols to use).
// DECISION: Difficulty and Theme were passed in as separate enum objects instead of hardcoding card
// counts and symbol lists here, so Board doesn't need to change if new difficulties/themes are added.
public class Board {

    private final List<Card> cards;
    private final Difficulty difficulty;
    private final Theme theme;

    public Board(Difficulty difficulty, Theme theme) {
        this.difficulty = difficulty;
        this.theme = theme;
        this.cards = generateCards(difficulty.getCardCount(), theme);
    }

    // UNDERSTAND: generateCards() builds pairs of cards from the theme's symbol list, then shuffles them
    // so the layout is different every round.
    // DECISION: Collections.shuffle() was used instead of manually randomizing positions because it's a
    // built-in, well-tested way to randomize a list in place.
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

    // UNDERSTAND: getCardById() searches the card list for a matching id and returns null if not found.
    // DECISION: Java Streams (filter + findFirst) were used instead of a manual for-loop because it reads
    // more clearly as "find the card whose id matches" in one line.
    public Card getCardById(int id) {
        return cards.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    // UNDERSTAND: isFullyMatched() checks whether every card on the board has been matched, which is how
    // the game knows a round is over.
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