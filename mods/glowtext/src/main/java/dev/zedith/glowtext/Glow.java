package dev.zedith.glowtext;

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import org.checkerframework.checker.nullness.compatqual.NullableType;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

/**
 * Glow helps parse from human-readable text to Hytale's Message.
 */
public class Glow {

    /**
     * F_BOLD bolds the text.
     */
    public static final int F_BOLD = 1;

    /**
     * F_ITALIC italics the text.
     */
    public static final int F_ITALIC = 1 << 1;

    /**
     * F_UNDERLINE underlines the text.
     */
    public static final int F_UNDERLINE = 1 << 2;

    /**
     * F_MONO makes the text monospaced.
     */
    public static final int F_MONO = 1 << 3;

    private final String text;

    protected State defaultState = State.DEFAULT;
    protected char openingStyleChar = '{';
    protected char closingStyleChar = '}';
    protected Option.Colors colors = this::parseColor;
    protected boolean fallbackToDefaultColors = false;

    private Glow(String text, Option... options) {
        this.text = text;
        for (Option option : options) {
            option.apply(this);
        }

        // Parse the default state's color. This ensures custom colors can be used with the default state.
        if (defaultState.color != null) {
            defaultState = defaultState.withColor(colors.parse(defaultState.color));
        }
    }


    /**
     * Parses the given text.
     *
     * @param text    The text to parse. This should be marked up with valid style formatting. The default style
     *                formatting is like "{red}Hello{italic} world!{/red} This is text{/italic}".
     * @param options Options modify the behavior of the parser. These range from changing the style characters, the
     *                colors, or the default state of text that will be parsed.
     * @return A formatted Hytale Message that represents the given text.
     */
    public static Message parse(String text, Option... options) {
        return new Glow(text, options).parse();
    }

    private Message parse() {
        if (text == null) throw new NullPointerException("text");

        Message message = Message.empty();
        Deque<StateChange> changes = new ArrayDeque<>();

        int i = 0;
        int flushed = 0;

        while (i < text.length()) {
            char ch = text.charAt(i);
            if (ch != openingStyleChar) {
                i++;
                continue;
            }

            int close = text.indexOf(closingStyleChar, i + 1);
            if (close < 0) {
                break;
            }

            if (i > flushed) {
                message.insert(flush(flushed, i, changes));
                flushed = i;
            }

            Optional<StateChange> optionalStateChange = parseStateChange(text.substring(i + 1, close));
            if (optionalStateChange.isEmpty()) {
                message.insert(flush(flushed, ++i, changes));
                flushed = i;
                continue;
            }

            StateChange stateChange = optionalStateChange.get();
            if (stateChange.openingTag) {
                if (stateChange.stateChange != null) {
                    flushed = i = close + 1;
                    changes.push(stateChange);
                } else {
                    ++i;
                }
                continue;
            }

            boolean removed = false;
            Iterator<StateChange> each = changes.iterator();
            while (each.hasNext()) {
                String nextTag = each.next().tag;
                if (!nextTag.equals(State.defaultTag) && nextTag.equals(stateChange.tag)) {
                    each.remove();
                    removed = true;
                    break;
                }
            }
            if (removed) {
                flushed = i = close + 1;
                continue;
            }

            ++i;
        }

        if (flushed < text.length()) {
            message.insert(flush(flushed, text.length(), changes));
        }

        return message;
    }

    private Message flush(int flushed, int current, Deque<StateChange> changes) {
        State state = defaultState;
        for (StateChange stateChange : changes) {
            state = stateChange.stateChange().apply(state);
        }

        return state.applyTo(Message.raw(text.substring(flushed, current)));
    }

    private Optional<StateChange> parseStateChange(String rawStateText) {
        boolean openingTag = rawStateText.charAt(0) != '/';
        String stateText = rawStateText.substring(!openingTag ? 1 : 0);
        String tag = stateText;
        Function<State, State> stateChange;
        switch (stateText) {
            case "bold":
            case "b":
                stateChange = (old) -> old.withFlags(old.flags + F_BOLD);
                tag = "bold";
                break;
            case "italic":
            case "i":
                stateChange = (old) -> old.withFlags(old.flags + F_ITALIC);
                tag = "italic";
                break;
            case "underline":
            case "u":
                stateChange = (old) -> old.withFlags(old.flags + F_UNDERLINE);
                tag = "underline";
                break;
            case "monospace":
            case "m":
                stateChange = (old) -> old.withFlags(old.flags + F_MONO);
                tag = "monospace";
                break;
            default:
                String color = colors.parse(stateText);
                if (color != null) {
                    stateChange = (old) -> old.withColor(color);
                    break;
                }

                if (fallbackToDefaultColors) {
                    String fallbackColor = parseColor(stateText);
                    if (fallbackColor != null) {
                        stateChange = (old) -> old.withColor(fallbackColor);
                        break;
                    }
                }

                if (stateText.startsWith("link:")) {
                    String url = stateText.substring("link:".length());
                    stateChange = (old) -> old.withLink(url);
                    tag = "link";
                    break;
                }

                stateChange = null;
        }

        return Optional.of(new StateChange(openingTag, tag, stateChange));
    }

    private String parseColor(String color) {
        if (color == null || color.startsWith("#")) {
            return color;
        }

        Color namedColor = namedColor(color);
        if (namedColor != null) {
            return ColorParseUtil.colorToHex(namedColor);
        }

        return null;
    }

    private Color namedColor(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "black" -> Color.BLACK;
            case "blue" -> Color.BLUE;
            case "cyan", "aqua" -> Color.CYAN;
            case "dark_gray" -> Color.DARK_GRAY;
            case "gray" -> Color.GRAY;
            case "green" -> Color.GREEN;
            case "light_gray" -> Color.LIGHT_GRAY;
            case "magenta", "purple" -> Color.MAGENTA;
            case "orange" -> Color.ORANGE;
            case "pink" -> Color.PINK;
            case "red" -> Color.RED;
            case "white" -> Color.WHITE;
            case "yellow" -> Color.YELLOW;
            default -> null;
        };
    }

    private record StateChange(boolean openingTag, String tag, Function<State, State> stateChange) {

    }

    private record State(String tag, String color, int flags, String link) {

        private static final String defaultTag = "default";
        private static final State DEFAULT = new State(defaultTag, null, 0, null);

        private State withColor(String color) {
            return new State(tag, color, flags, link);
        }

        private State withFlags(int flags) {
            return new State(tag, color, flags, link);
        }

        private State withLink(String link) {
            return new State(tag, color, flags, link);
        }

        private Message applyTo(Message message) {
            if (color != null) {
                message.color(color);
            }
            if (link != null) {
                message.link(link);
            }
            message.bold((flags & F_BOLD) != 0);
            message.italic((flags & F_ITALIC) != 0);
            message.monospace((flags & F_MONO) != 0);
            if ((flags & F_UNDERLINE) != 0) {
                message.getFormattedMessage().underlined = MaybeBool.True;
            }
            return message;
        }
    }

    /**
     * DefaultState represents the base level of styles to apply to text.
     *
     * @param color The default color.
     * @param flags The default flags. You can combine flags by adding them, e.g. F_BOLD + F_ITALIC.
     * @param link  The default link.
     */
    public record DefaultState(@NullableType String color, int flags, @NullableType String link) {
        State toState() {
            return new State(State.defaultTag, color, flags, link);
        }
    }
}
