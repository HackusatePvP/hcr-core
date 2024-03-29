package dev.hcr.hcf.factions.claims.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * I did not make this class, all credit goes to the original creator.
 *
 * Credit: https://www.spigotmc.org/threads/region-cuboid.329859/
 */
public class Cuboid {
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;
    private final int zMax;
    private final double xMinCentered;
    private final double xMaxCentered;
    private final double yMinCentered;
    private final double yMaxCentered;
    private final double zMinCentered;
    private final double zMaxCentered;
    private final World world;

    public Cuboid(final Location point1, final Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = 0;
        this.yMax = 250;
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public Cuboid(String world, double x1, double z1, double x2, double z2) {
        Location point1 = new Location(Bukkit.getWorld(world), x1, 0, z1);
        Location point2 = new Location(Bukkit.getWorld(world), x2, 250, z2);
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = 0;
        this.yMax = 250;
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public Iterator<Block> blockList() {
        final ArrayList<Block> bL = new ArrayList<>(this.getTotalBlockSize());
        for(int x = this.xMin; x <= this.xMax; ++x) {
            for(int y = this.yMin; y <= this.yMax; ++y) {
                for(int z = this.zMin; z <= this.zMax; ++z) {
                    final Block b = this.world.getBlockAt(x, y, z);
                    bL.add(b);
                }
            }
        }
        return bL.listIterator();
    }

    public Iterator<Block> edgeBlocks(int buffer) {
        final ArrayList<Block> bL = new ArrayList<>();
        for (int x = xMin, xBuffer = 0; x <= xMax && xBuffer < buffer; x ++, xBuffer++) {
            for (int y = yMin, yBuffer = 0; y <= yMax && yBuffer < buffer; y++, yBuffer++) {
                for (int z = zMin, zBuffer = 0; z <= zMax && zBuffer < buffer; z++, zBuffer++) {
                    Block block = world.getBlockAt(x, y, z);
                    bL.add(block);
                }
            }
        }
        return bL.listIterator();
    }

    public Location getCenter() {
        return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin, (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
    }

    public double getDistance() {
        return this.getPoint1().distance(this.getPoint2());
    }

    public double getDistanceSquared() {
        return this.getPoint1().distanceSquared(this.getPoint2());
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    public Location getPoint1() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    public Location getPoint2() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }

    public List<Chunk> getChunks() {
        List<Chunk> toReturn = new ArrayList<>();
        World w = this.world;
        int x1 = this.xMin & ~0xf;
        int x2 = this.xMax & ~0xf;
        int z1 = this.zMin & ~0xf;
        int z2 = this.zMax & ~0xf;
        for(int x = x1; x <= x2; x += 16) {
            for(int z = z1; z <= z2; z += 16) {
                toReturn.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }
        return toReturn;
    }


    public Location getCorner(int corner) {
        switch (corner) {
            case 1:
                Location point1Low = getPoint1();
                point1Low.setY(0);
                return point1Low;
            case 2:
                Location point2Low = getPoint2();
                point2Low.setY(0);
                return point2Low;
            case 3:
                return new Location(world, xMin, yMin, zMax);
            default:
            case 4:
                return new Location(world, xMax, yMin, zMin);
        }
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc
                .getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
    }

    public boolean isIn(final Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - marge && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge && loc
                .getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge && loc.getZ() <= this.zMaxCentered + marge;
    }

    public int getMAxX() {
        return xMax;
    }

    public int getMinX() {
        return xMin;
    }

    public int getMaxY() {
        return yMax;
    }

    public int getMinY() {
        return yMin;
    }

    public int getMaxZ() {
        return zMax;
    }

    public int getMinZ() {
        return zMin;
    }

    public World getWorld() {
        return world;
    }
}