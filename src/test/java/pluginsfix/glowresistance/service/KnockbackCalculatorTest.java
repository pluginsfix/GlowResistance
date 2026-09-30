package pluginsfix.glowresistance.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class KnockbackCalculatorTest {

    @Test
    void calculateWindChargeVectorShouldReturnUpwardVelocityWhenDirectlyAbove() {
        KnockbackCalculator.Vector3d result = KnockbackCalculator.calculateWindChargeVector(
            0.0, 0.0, 0.0,
            0.0, 0.5, 0.0,
            1.2,
            1.22,
            1.0,
            1.0,
            1.0
        );

        assertThat(result.y()).isGreaterThan(0.5);
        assertThat(result.x()).isCloseTo(0.0, within(0.001));
        assertThat(result.z()).isCloseTo(0.0, within(0.001));
    }

    @Test
    void calculateWindChargeVectorShouldReturnZeroWhenOutOfRange() {
        KnockbackCalculator.Vector3d result = KnockbackCalculator.calculateWindChargeVector(
            0.0, 0.0, 0.0,
            10.0, 10.0, 10.0,
            1.2,
            1.22,
            1.0,
            1.0,
            1.0
        );

        assertThat(result.x()).isEqualTo(0.0);
        assertThat(result.y()).isEqualTo(0.0);
        assertThat(result.z()).isEqualTo(0.0);
    }

    @Test
    void restoreKnockbackShouldScaleByInverseOfOneMinusResistance() {
        KnockbackCalculator.Vector3d initial = new KnockbackCalculator.Vector3d(0.4, 0.8, 0.4);
        double resistance = 0.6;

        KnockbackCalculator.Vector3d restored = KnockbackCalculator.restoreKnockback(initial, resistance);

        assertThat(restored.x()).isCloseTo(1.0, within(0.001));
        assertThat(restored.y()).isCloseTo(2.0, within(0.001));
        assertThat(restored.z()).isCloseTo(1.0, within(0.001));
    }

    @Test
    void restoreKnockbackShouldReturnZeroWhenResistanceIsMaximized() {
        KnockbackCalculator.Vector3d initial = new KnockbackCalculator.Vector3d(0.0, 0.0, 0.0);
        double resistance = 1.0;

        KnockbackCalculator.Vector3d restored = KnockbackCalculator.restoreKnockback(initial, resistance);

        assertThat(restored.x()).isEqualTo(0.0);
        assertThat(restored.y()).isEqualTo(0.0);
        assertThat(restored.z()).isEqualTo(0.0);
    }
}
