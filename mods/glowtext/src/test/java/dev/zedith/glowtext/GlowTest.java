package dev.zedith.glowtext;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class GlowTest {

    @Tag("hytale")
    @Test
    void withNoFormatting() {
        Message msg = Glow.parse("Hello World!");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("Hello World!", fm.children[0].rawText);
        assertNull(fm.children[0].color);
    }

    @Tag("hytale")
    @Test
    void withSimpleColor() {
        Message msg = Glow.parse("{blue}hello{/blue}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("hello", fm.children[0].rawText);
        assertEquals(ColorParseUtil.colorToHex(Color.BLUE), fm.children[0].color);
    }

    @Tag("hytale")
    @Test
    void withComplexColor() {
        Message msg = Glow.parse("{#453}hello{/#453}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("hello", fm.children[0].rawText);
        assertEquals("#453", fm.children[0].color);
    }

    @Tag("hytale")
    @Test
    void withCustomColors() {
        Message msg = Glow.parse(
                "{customColor}hello{/customColor}{unmatched}{/unmatched}", Option.withColors(
                        color -> {
                            if (color.equals("customColor")) {
                                return "#11";
                            }
                            return null;
                        }, false
                )
        );
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(3, fm.children.length);

        assertEquals("hello", fm.children[0].rawText);
        assertEquals("#11", fm.children[0].color);

        // These tags are separate children because we flush
        assertEquals("{unmatched}", fm.children[1].rawText);
        assertNull(fm.children[1].color);

        assertEquals("{/unmatched}", fm.children[2].rawText);
        assertNull(fm.children[1].color);
    }

    @Tag("hytale")
    @Test
    void withLink() {
        Message msg = Glow.parse("{#453}hello this is a {link:https://google.com}link{/link}{/#453}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(2, fm.children.length);

        assertEquals("hello this is a ", fm.children[0].rawText);
        assertEquals("#453", fm.children[0].color);
        assertNull(fm.children[0].link);

        assertEquals("link", fm.children[1].rawText);
        assertEquals("#453", fm.children[1].color);
        assertEquals("https://google.com", fm.children[1].link);
    }

    @Tag("hytale")
    @Test
    void withDifferentStyleChars() {
        Message msg = Glow.parse(
                "Hello >bold[World>/bold[!",
                Option.withOpeningStyleChar('>'),
                Option.withClosingStyleChar('[')
        );
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(3, fm.children.length);

        assertEquals("Hello ", fm.children[0].rawText);
        assertNull(fm.children[0].color);
        assertEquals(MaybeBool.False, fm.children[0].bold);
        assertEquals(MaybeBool.False, fm.children[0].italic);

        assertEquals("World", fm.children[1].rawText);
        assertNull(fm.children[1].color);
        assertEquals(MaybeBool.True, fm.children[1].bold);
        assertEquals(MaybeBool.False, fm.children[1].italic);

        assertEquals("!", fm.children[2].rawText);
        assertNull(fm.children[2].color);
        assertEquals(MaybeBool.False, fm.children[2].bold);
        assertEquals(MaybeBool.False, fm.children[2].italic);
    }

    @Tag("hytale")
    @Test
    void withDefaultState() {
        Message msg = Glow.parse(
                "This is some text {italic}italic{/italic} {red}finale{/red}.",
                Option.withDefaultState(
                        new Glow.DefaultState("#00ff00", Glow.F_BOLD, null)
                )
        );
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(5, fm.children.length);

        assertEquals("This is some text ", fm.children[0].rawText);
        assertEquals("#00ff00", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.False, fm.children[0].italic);

        assertEquals("italic", fm.children[1].rawText);
        assertEquals("#00ff00", fm.children[1].color);
        assertEquals(MaybeBool.True, fm.children[1].bold);
        assertEquals(MaybeBool.True, fm.children[1].italic);

        assertEquals(" ", fm.children[2].rawText);
        assertEquals("#00ff00", fm.children[2].color);
        assertEquals(MaybeBool.True, fm.children[2].bold);
        assertEquals(MaybeBool.False, fm.children[2].italic);

        assertEquals("finale", fm.children[3].rawText);
        assertEquals("#ff0000", fm.children[3].color);
        assertEquals(MaybeBool.True, fm.children[3].bold);
        assertEquals(MaybeBool.False, fm.children[3].italic);

        assertEquals(".", fm.children[4].rawText);
        assertEquals("#00ff00", fm.children[4].color);
        assertEquals(MaybeBool.True, fm.children[4].bold);
        assertEquals(MaybeBool.False, fm.children[4].italic);
    }

    @Tag("hytale")
    @Test
    void withCustomColorsAndDefaultState() {
        Message msg = Glow.parse(
                "{italic}heyo{/italic}",
                Option.withColors(
                        color -> {
                            if (color.equals("customColor")) {
                                return "#42";
                            }
                            return null;
                        }, false
                ),
                Option.withDefaultState(
                        new Glow.DefaultState("customColor", Glow.F_BOLD, null)
                )
        );
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("heyo", fm.children[0].rawText);
        assertEquals("#42", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.True, fm.children[0].italic);
    }

    @Tag("hytale")
    @Test
    void withShorthandStyles() {
        Message msg = Glow.parse("{m}{b}hello{/b}{/m}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("hello", fm.children[0].rawText);
        assertNull(fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.True, fm.children[0].monospace);
    }

    @Tag("hytale")
    @Test
    void withMultipleStyles() {
        Message msg = Glow.parse("{#453}{bold}hello{/bold}{/#453}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(1, fm.children.length);

        assertEquals("hello", fm.children[0].rawText);
        assertEquals("#453", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
    }

    @Tag("hytale")
    @Test
    void withMultipleChildren() {
        Message msg = Glow.parse("{#453}{bold}he{italic}ll{/italic}o{/bold}{/#453}");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(3, fm.children.length);

        assertEquals("he", fm.children[0].rawText);
        assertEquals("#453", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.False, fm.children[0].italic);

        assertEquals("ll", fm.children[1].rawText);
        assertEquals("#453", fm.children[1].color);
        assertEquals(MaybeBool.True, fm.children[1].bold);
        assertEquals(MaybeBool.True, fm.children[1].italic);

        assertEquals("o", fm.children[2].rawText);
        assertEquals("#453", fm.children[2].color);
        assertEquals(MaybeBool.True, fm.children[2].bold);
        assertEquals(MaybeBool.False, fm.children[2].italic);
    }

    @Tag("hytale")
    @Test
    void withMixedChildren() {
        Message msg = Glow.parse("{#789}{bold}BOLD{italic}BOTH{/bold}ITALIC{/italic}{/#789}Nothing");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(4, fm.children.length);

        assertEquals("BOLD", fm.children[0].rawText);
        assertEquals("#789", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.False, fm.children[0].italic);

        assertEquals("BOTH", fm.children[1].rawText);
        assertEquals("#789", fm.children[1].color);
        assertEquals(MaybeBool.True, fm.children[1].bold);
        assertEquals(MaybeBool.True, fm.children[1].italic);

        assertEquals("ITALIC", fm.children[2].rawText);
        assertEquals("#789", fm.children[2].color);
        assertEquals(MaybeBool.False, fm.children[2].bold);
        assertEquals(MaybeBool.True, fm.children[2].italic);

        assertEquals("Nothing", fm.children[3].rawText);
        assertNull(fm.children[3].color);
        assertEquals(MaybeBool.False, fm.children[3].bold);
        assertEquals(MaybeBool.False, fm.children[3].italic);
    }

    @Tag("hytale")
    @Test
    void withMixedLinkAndStyle() {
        Message msg = Glow.parse("{bold}{red}Another {link:https://drive.google.com}li{/red}n{/bold}k{/link}. Fin");
        assertNotNull(msg);

        FormattedMessage fm = msg.getFormattedMessage();
        assertNotNull(fm.children);
        assertEquals(5, fm.children.length);

        assertEquals("Another ", fm.children[0].rawText);
        assertEquals("#ff0000", fm.children[0].color);
        assertEquals(MaybeBool.True, fm.children[0].bold);
        assertEquals(MaybeBool.False, fm.children[0].italic);
        assertNull(fm.children[0].link);

        assertEquals("li", fm.children[1].rawText);
        assertEquals("#ff0000", fm.children[1].color);
        assertEquals(MaybeBool.True, fm.children[1].bold);
        assertEquals(MaybeBool.False, fm.children[1].italic);
        assertEquals("https://drive.google.com", fm.children[1].link);

        assertEquals("n", fm.children[2].rawText);
        assertNull(fm.children[2].color);
        assertEquals(MaybeBool.True, fm.children[2].bold);
        assertEquals(MaybeBool.False, fm.children[2].italic);
        assertEquals("https://drive.google.com", fm.children[2].link);

        assertEquals("k", fm.children[3].rawText);
        assertNull(fm.children[3].color);
        assertEquals(MaybeBool.False, fm.children[3].bold);
        assertEquals(MaybeBool.False, fm.children[3].italic);
        assertEquals("https://drive.google.com", fm.children[3].link);

        assertEquals(". Fin", fm.children[4].rawText);
        assertNull(fm.children[4].color);
        assertEquals(MaybeBool.False, fm.children[4].bold);
        assertEquals(MaybeBool.False, fm.children[4].italic);
        assertNull(fm.children[4].link);
    }
}
