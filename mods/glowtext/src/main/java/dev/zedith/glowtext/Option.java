package dev.zedith.glowtext;

/**
 * Options to pass in to Glow#parse.
 */
public interface Option {

    /**
     * Changes the opening style character.
     *
     * @param c The new opening style character. The default is '{'.
     * @return The option to pass in to Glow#parse.
     */
    static Option withOpeningStyleChar(char c) {
        return glow -> glow.openingStyleChar = c;
    }

    /**
     * Changes the closing style character.
     *
     * @param c The new closing style character. The default is '}'.
     * @return The option to pass in to Glow#parse.
     */
    static Option withClosingStyleChar(char c) {
        return glow -> glow.closingStyleChar = c;
    }

    /**
     * Changes the default state.
     *
     * @param state The new default state. The default is white text.
     * @return The option to pass in to Glow#parse.
     */
    static Option withDefaultState(Glow.DefaultState state) {
        return glow -> glow.defaultState = state.toState();
    }

    /**
     * Appends or replaces the default colors.
     *
     * @param colors                  The colors to use when handling color tags.
     * @param fallbackToDefaultColors If the parser should fall back to using the default colors. If this is true,
     *                                this is appending colors to the existing set. If this is false, this is
     *                                replacing the colors.
     * @return The option to pass in to Glow#parse.
     */
    static Option withColors(Colors colors, boolean fallbackToDefaultColors) {
        return glow -> {
            glow.colors = colors;
            glow.fallbackToDefaultColors = fallbackToDefaultColors;
        };
    }

    /**
     * Apply the option to the given parser.
     *
     * @param glow The parser.
     */
    void apply(Glow glow);

    /**
     * Colors defines the transition from a user inputted color to a hexadecimal color. This should include the '#'
     * prefix.
     */
    interface Colors {
        /**
         * Parse the string to a valid hexadecimal color.
         *
         * @param color The user inputted color.
         * @return A hexadecimal color. This can be null if none match.
         */
        String parse(String color);
    }
}
