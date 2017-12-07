package uk.co.complex.lvs.ggp.games.flip;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;

public class FlipMove extends Move {
    // The value (-1,-1) represents the null move
    final int x, y;

    public FlipMove(Player player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    public static FlipMove getNullMove(Player player) {
        return new FlipMove(player, -1, -1);
    }

    @Override
    public String toString() {
        return "(" + getPlayer().getName() + "): (" + x + ", " + y + ")";
    }
}
