// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.engine.math;

import org.junit.jupiter.api.Test;
import org.terasology.engine.config.Config;
import org.terasology.engine.context.internal.ContextImpl;
import org.terasology.engine.context.internal.MockContext;
import org.terasology.engine.math.ChunkMath;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.math.geom.Vector3i;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChunkMathTest {

    @Test
    public void testGetEdgeRegion() {
        Region3i region = Region3i.createFromMinAndSize(new Vector3i(16, 0, 16), new Vector3i(16, 128, 16));
        assertEquals(Region3i.createFromMinMax(new Vector3i(16, 0, 16), new Vector3i(16, 127, 31)),
                ChunkMath.getEdgeRegion(region, Side.LEFT));
    }

    @Test
    public void testRegionPositions() {
        CoreRegistry.setContext(new ContextImpl());
        CoreRegistry.put(Config.class, new Config(new MockContext()));

        assertEquals(1, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0),
                new Vector3i(0, 0, 0))).length);
        assertEquals(1, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0), new Vector3i(31, 63,
                31))).length);
        assertEquals(2, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0), new Vector3i(32, 63,
                31))).length);
        assertEquals(4, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0), new Vector3i(32, 63,
                32))).length);
        assertEquals(8, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0), new Vector3i(32, 64,
                32))).length);
        assertEquals(12, ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(-1, 0, 0), new Vector3i(32, 64
                , 32))).length);

        Vector3i[] chunks = ChunkMath.calcChunkPos(Region3i.createFromMinMax(new Vector3i(0, 0, 0), new Vector3i(32,
                63, 31)));
        assertEquals(new Vector3i(0, 0, 0), chunks[0]);
        assertEquals(new Vector3i(1, 0, 0), chunks[1]);
    }

    @Test
    public void testCalcChunk() {
        assertEquals(0, ChunkMath.calcChunkPos(10, 6));
        assertEquals(-1, ChunkMath.calcChunkPos(-1, 6));
        assertEquals(1, ChunkMath.calcChunkPos(100, 6));
        assertEquals(3, ChunkMath.calcChunkPos(200, 6));
    }

    @Test
    public void testCalcChunkPosX() {
        assertEquals(0, ChunkMath.calcChunkPosX(10));
        assertEquals(-1, ChunkMath.calcChunkPosX(-1));
        assertEquals(3, ChunkMath.calcChunkPosX(100));
        assertEquals(6, ChunkMath.calcChunkPosX(200));
        assertEquals(21, ChunkMath.calcChunkPosX(700));
    }

    @Test
    public void testCalcChunkPosY() {
        assertEquals(0, ChunkMath.calcChunkPosY(10));
        assertEquals(-1, ChunkMath.calcChunkPosY(-1));
        assertEquals(1, ChunkMath.calcChunkPosY(100));
        assertEquals(3, ChunkMath.calcChunkPosY(200));
        assertEquals(10, ChunkMath.calcChunkPosY(700));
    }

    @Test
    public void testCalcChunkPosZ() {
        assertEquals(0, ChunkMath.calcChunkPosZ(10));
        assertEquals(-1, ChunkMath.calcChunkPosZ(-1));
        assertEquals(6, ChunkMath.calcChunkPosZ(200));
        assertEquals(21, ChunkMath.calcChunkPosZ(700));
    }

    @Test
    public void testCalcChunkPos() {
        org.joml.Vector3i temp = new org.joml.Vector3i();
        assertTrue(ChunkMath.calcChunkPos(700, 700, 700, temp).equals(21, 10, 21));
        assertTrue(ChunkMath.calcChunkPos(200, 700, -1, temp).equals(6, 10, -1));
        assertTrue(ChunkMath.calcChunkPos(200, 200, 200, temp).equals(6, 3, 6));
        assertTrue(ChunkMath.calcChunkPos(10, 10, 10, temp).equals(0, 0, 0));
    }


    @Test
    public void testFloatingPointCalcChunkPos() {
        org.joml.Vector3i temp = new org.joml.Vector3i();
        assertTrue(ChunkMath.calcChunkPos(31.9f, 64.1f, 32.5f, temp).equals(0, 1, 1), temp.toString());
        assertTrue(ChunkMath.calcChunkPos(32.9f, 63.9f, 32.9f, temp).equals(1, 0, 1), temp.toString());
        assertTrue(ChunkMath.calcChunkPos(31.3f, 63.9f, 31.9f, temp).equals(0, 0, 0), temp.toString());
        assertTrue(ChunkMath.calcChunkPos(31.6f, 64.5f, 32.1f, temp).equals(0, 1, 1), temp.toString());
        assertTrue(ChunkMath.calcChunkPos(.1f, -.2f, -.8f, temp).equals(0, -1, -1), temp.toString());
        assertTrue(ChunkMath.calcChunkPos(-.1f, -.99f, 2f, temp).equals(-1, -1, 0), temp.toString());
    }

}