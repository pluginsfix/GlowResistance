package pluginsfix.glowresistance.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesFormattingTest {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .build();

    @Test
    void parseComponentShouldProperlyParseHexAndLegacyColorsWithoutItalics() {
        String input = "&#FFFF00◆ &fКонфигурация плагина &#FFFF00GlowResistance &fуспешно перезагружена!";
        Component component = SERIALIZER.deserialize(input.replace('§', '&'))
            .decoration(TextDecoration.ITALIC, false);

        assertThat(component).isNotNull();

        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        assertThat(plainText).contains("◆ Конфигурация плагина GlowResistance успешно перезагружена!");
        assertThat(plainText).doesNotContain("§x");
        assertThat(plainText).doesNotContain("&");
    }

    @Test
    void parseComponentShouldHandleOrangeWarningMarker() {
        String input = "&#FB8808▶ &fНедостаточно &#FB8808прав!";
        Component component = SERIALIZER.deserialize(input.replace('§', '&'))
            .decoration(TextDecoration.ITALIC, false);

        assertThat(component).isNotNull();

        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        assertThat(plainText).contains("▶ Недостаточно прав!");
        assertThat(plainText).doesNotContain("§x");
    }
}
