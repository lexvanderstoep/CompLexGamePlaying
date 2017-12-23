package uk.co.complex.lvs.ggp;

import java.awt.*;

/**
 * Created by Lex van der Stoep on 12/12/2017.
 *
 * Represents a game state which can be printed to a graphical output.
 */
public interface GraphicalState {

    /**
     * Converts the game state into an image.
     * @param width the width of the image
     * @param height the height of the image
     * @return the graphical representation of the state
     */
    public Image toImage(int width, int height);
}
