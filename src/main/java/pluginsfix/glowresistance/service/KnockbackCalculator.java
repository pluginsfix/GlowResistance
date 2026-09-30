package pluginsfix.glowresistance.service;

public final class KnockbackCalculator {

    public record Vector3d(double x, double y, double z) {
        public static final Vector3d ZERO = new Vector3d(0, 0, 0);

        public Vector3d multiply(double factor) {
            return new Vector3d(x * factor, y * factor, z * factor);
        }

        public Vector3d add(Vector3d other) {
            return new Vector3d(x + other.x, y + other.y, z + other.z);
        }

        public double lengthSquared() {
            return x * x + y * y + z * z;
        }

        public double length() {
            return Math.sqrt(lengthSquared());
        }
    }

    public static Vector3d calculateWindChargeVector(
        double originX,
        double originY,
        double originZ,
        double targetX,
        double targetY,
        double targetZ,
        double radius,
        double baseScale,
        double knockbackMultiplier,
        double verticalMultiplier,
        double horizontalMultiplier
    ) {
        double maxDistance = radius * 2.0;
        double dx = targetX - originX;
        double dy = targetY - originY;
        double dz = targetZ - originZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > maxDistance || maxDistance <= 0.0) {
            return Vector3d.ZERO;
        }

        double impact = 1.0 - (distance / maxDistance);
        if (impact <= 0.0) {
            return Vector3d.ZERO;
        }

        double dirX = distance > 0.0001 ? dx / distance : 0.0;
        double dirY = distance > 0.0001 ? dy / distance : 1.0;
        double dirZ = distance > 0.0001 ? dz / distance : 0.0;

        double force = impact * baseScale * knockbackMultiplier;

        double vx = dirX * force * horizontalMultiplier;
        double upwardBase = Math.max(dirY * force, impact * 0.85 * baseScale * knockbackMultiplier);
        double vy = upwardBase * verticalMultiplier;
        double vz = dirZ * force * horizontalMultiplier;

        return new Vector3d(vx, vy, vz);
    }

    public static Vector3d restoreKnockback(
        Vector3d currentKb,
        double resistance
    ) {
        if (resistance <= 0.0) {
            return currentKb;
        }

        if (resistance >= 0.999) {
            return Vector3d.ZERO;
        }

        double scale = 1.0 / (1.0 - resistance);
        return currentKb.multiply(scale);
    }
}
