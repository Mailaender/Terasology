/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.utilities.procedural;

import org.terasology.math.Rect2i;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.math.geom.Vector2f;
import org.terasology.math.geom.Vector3f;

import com.google.common.math.IntMath;

/**
 * @author Immortius
 */
public class SubSampledNoise extends AbstractNoise {

    private Noise source;
    private Vector3f zoom = new Vector3f(1, 1, 1);
    private int sampleRate = 1;

    /**
     * @param source the source noise generator
     * @param zoom the zoom factor for x, y (z will be 1)
     * @param sampleRate the sampling rate of the noise
     */
    public SubSampledNoise(Noise source, Vector2f zoom, int sampleRate) {
        this(source, new Vector3f(zoom.x, zoom.y, 1), sampleRate);
    }

    public SubSampledNoise(Noise source, Vector3f zoom, int sampleRate) {
        this.source = source;
        this.zoom.set(zoom);
        this.sampleRate = sampleRate;
    }

    @Override
    public float noise(float x, float y) {
        float xMod = TeraMath.modulus(x, sampleRate);
        float yMod = TeraMath.modulus(y, sampleRate);

        float x0 = x - xMod;
        float x1 = x0 + sampleRate;
        float y0 = y - yMod;
        float y1 = y0 + sampleRate;

        float q00 = source.noise(x0 * zoom.x, y0 * zoom.y);
        float q10 = source.noise(x1 * zoom.x, y0 * zoom.y);
        float q01 = source.noise(x0 * zoom.x, y1 * zoom.y);
        float q11 = source.noise(x1 * zoom.x, y1 * zoom.y);

        return TeraMath.biLerp(q00, q10, q01, q11, xMod / sampleRate, yMod / sampleRate);
    }

    public float[] noise(Rect2i region) {
        Rect2i fullRegion = determineRequiredRegion(region);
        float[] keyData = getKeyValues(fullRegion);
        float[] fullData = mapExpand(keyData, fullRegion);
        return getSubset(fullData, fullRegion, region);
    }

    private float[] getSubset(float[] fullData, Rect2i fullRegion, Rect2i subRegion) {
        if (subRegion.size().x != fullRegion.size().x || subRegion.size().y != fullRegion.size().y) {
            float[] result = new float[subRegion.size().x * subRegion.size().y];
            Vector2i offset = new Vector2i(subRegion.minX() - fullRegion.minX(), subRegion.minY() - fullRegion.minY());
            for (int y = 0; y < subRegion.size().y; ++y) {
                System.arraycopy(fullData, offset.getX() + fullRegion.size().x * (y + offset.getY()), result, subRegion.size().x * y, subRegion.size().x);
            }
            return result;
        } else {
            return fullData;
        }
    }

    private float[] mapExpand(float[] keyData, Rect2i fullRegion) {
        float[] fullData = new float[fullRegion.size().x * fullRegion.size().y];
        int samplesX = fullRegion.size().x / sampleRate + 1;
        int samplesY = fullRegion.size().y / sampleRate + 1;
        for (int y = 0; y < samplesY - 1; y++) {
            for (int x = 0; x < samplesX - 1; x++) {
                float q11 = keyData[x + y * samplesX];
                float q21 = keyData[x + 1 + y * samplesX];
                float q12 = keyData[x + (y + 1) * samplesX];
                float q22 = keyData[(x + 1) + (y + 1) * samplesX];
                for (int innerY = 0; innerY < sampleRate; ++innerY) {
                    for (int innerX = 0; innerX < sampleRate; ++innerX) {
                        fullData[x * sampleRate + innerX + fullRegion.size().x * (y * sampleRate + innerY)] =
                                TeraMath.biLerp(q11, q21, q12, q22, (float) innerX / sampleRate, (float) innerY / sampleRate);
                    }
                }
            }
        }
        return fullData;
    }

    private float[] getKeyValues(Rect2i fullRegion) {
        int xDim = fullRegion.size().x / sampleRate + 1;
        int yDim = fullRegion.size().y / sampleRate + 1;
        float[] fullData = new float[xDim * yDim];
        for (int y = 0; y < yDim; y++) {
            for (int x = 0; x < xDim; x++) {
                int actualX = x * sampleRate + fullRegion.minX();
                int actualY = y * sampleRate + fullRegion.minY();
                fullData[x + y * xDim] = source.noise(zoom.x * actualX, zoom.y * actualY);
            }
        }
        return fullData;
    }

    private Rect2i determineRequiredRegion(Rect2i region) {
        int newMinX = region.minX() - IntMath.mod(region.minX(), sampleRate);
        int newMinY = region.minY() - IntMath.mod(region.minY(), sampleRate);
        int newMaxX = region.maxX() + 4 - IntMath.mod(region.maxX(), sampleRate) - 1;
        int newMaxY = region.maxY() + 4 - IntMath.mod(region.maxY(), sampleRate) - 1;
        return Rect2i.createFromMinAndMax(newMinX, newMinY, newMaxX, newMaxY);
    }


    @Override
    public float noise(float x, float y, float z) {
        float xMod = TeraMath.modulus(x, sampleRate);
        float yMod = TeraMath.modulus(y, sampleRate);
        float zMod = TeraMath.modulus(z, sampleRate);

        float x0 = x - xMod;
        float x1 = x0 + sampleRate;
        float y0 = y - yMod;
        float y1 = y0 + sampleRate;
        float z0 = z - zMod;
        float z1 = z0 + sampleRate;

        float q000 = source.noise(x0 * zoom.x, y0 * zoom.y, z0 * zoom.z);
        float q100 = source.noise(x1 * zoom.x, y0 * zoom.y, z0 * zoom.z);
        float q010 = source.noise(x0 * zoom.x, y1 * zoom.y, z0 * zoom.z);
        float q110 = source.noise(x1 * zoom.x, y1 * zoom.y, z0 * zoom.z);
        float q001 = source.noise(x0 * zoom.x, y0 * zoom.y, z1 * zoom.z);
        float q101 = source.noise(x1 * zoom.x, y0 * zoom.y, z1 * zoom.z);
        float q011 = source.noise(x0 * zoom.x, y1 * zoom.y, z1 * zoom.z);
        float q111 = source.noise(x1 * zoom.x, y1 * zoom.y, z1 * zoom.z);

        return TeraMath.triLerp(q000, q100, q010, q110, q001, q101, q011, q111, xMod / sampleRate, yMod / sampleRate, zMod / sampleRate);
    }

    public float[] noise(Region3i region) {
        Region3i fullRegion = determineRequiredRegion(region);
        float[] keyData = getKeyValues(fullRegion);
        float[] fullData = mapExpand(keyData, fullRegion);
        return getSubset(fullData, fullRegion, region);
    }

    private float[] getSubset(float[] fullData, Region3i fullRegion, Region3i subRegion) {
        if (subRegion.size().x != fullRegion.size().x || subRegion.size().y != fullRegion.size().y || subRegion.size().z != fullRegion.size().z) {
            float[] result = new float[subRegion.size().x * subRegion.size().y * subRegion.size().z];
            Vector3i offset = new Vector3i(subRegion.minX() - fullRegion.minX(), subRegion.minY() - fullRegion.minY(), subRegion.minZ() - fullRegion.minZ());
            for (int z = 0; z < subRegion.size().z; ++z) {
                for (int y = 0; y < subRegion.size().y; ++y) {
                    System.arraycopy(fullData, offset.x + fullRegion.sizeX() * (y + offset.y + fullRegion.sizeY() * (z + offset.z)),
                            result, subRegion.sizeX() * (y + subRegion.sizeY() * z), subRegion.size().x);
                }
            }
            return result;
        } else {
            return fullData;
        }
    }

    private float[] mapExpand(float[] keyData, Region3i fullRegion) {
        float[] fullData = new float[fullRegion.size().x * fullRegion.size().y * fullRegion.size().z];
        int samplesX = fullRegion.size().x / sampleRate + 1;
        int samplesY = fullRegion.size().y / sampleRate + 1;
        int samplesZ = fullRegion.size().z / sampleRate + 1;
        for (int z = 0; z < samplesZ - 1; z++) {
            for (int y = 0; y < samplesY - 1; y++) {
                for (int x = 0; x < samplesX - 1; x++) {
                    float q000 = keyData[x + samplesX * (y + samplesY * z)];
                    float q100 = keyData[x + 1 + samplesX * (y + samplesY * z)];
                    float q010 = keyData[x + samplesX * (y + 1 + samplesY * z)];
                    float q110 = keyData[(x + 1) + samplesX * (y + 1 + samplesY * z)];
                    float q001 = keyData[x + samplesX * (y + samplesY * (z + 1))];
                    float q101 = keyData[x + 1 + samplesX * (y + samplesY * (z + 1))];
                    float q011 = keyData[x + samplesX * (y + 1 + samplesY * (z + 1))];
                    float q111 = keyData[(x + 1) + samplesX * (y + 1 + samplesY * (z + 1))];
                    for (int innerZ = 0; innerZ < sampleRate; ++innerZ) {
                        for (int innerY = 0; innerY < sampleRate; ++innerY) {
                            for (int innerX = 0; innerX < sampleRate; ++innerX) {
                                fullData[x * sampleRate + innerX + fullRegion.sizeX() * (y * sampleRate + innerY + fullRegion.sizeY() * (z * sampleRate + innerZ))] =
                                        TeraMath.triLerp(q000, q100, q010, q110, q001, q101, q011, q111,
                                                (float) innerX / sampleRate, (float) innerY / sampleRate, (float) innerZ / sampleRate);
                            }
                        }
                    }
                }
            }
        }
        return fullData;
    }

    private float[] getKeyValues(Region3i fullRegion) {
        int xDim = fullRegion.size().x / sampleRate + 1;
        int yDim = fullRegion.size().y / sampleRate + 1;
        int zDim = fullRegion.size().z / sampleRate + 1;
        float[] fullData = new float[xDim * yDim * zDim];
        for (int z = 0; z < zDim; z++) {
            for (int y = 0; y < yDim; y++) {
                for (int x = 0; x < xDim; x++) {
                    int actualX = x * sampleRate + fullRegion.minX();
                    int actualY = y * sampleRate + fullRegion.minY();
                    int actualZ = z * sampleRate + fullRegion.minZ();
                    fullData[x + xDim * (y + yDim * z)] = source.noise(zoom.x * actualX, zoom.y * actualY, zoom.z * actualZ);
                }
            }
        }
        return fullData;
    }

    private Region3i determineRequiredRegion(Region3i region) {
        int newMinX = region.minX() - IntMath.mod(region.minX(), sampleRate);
        int newMinY = region.minY() - IntMath.mod(region.minY(), sampleRate);
        int newMinZ = region.minZ() - IntMath.mod(region.minZ(), sampleRate);
        int newMaxX = region.maxX() + 4 - IntMath.mod(region.maxX(), sampleRate) - 1;
        int newMaxY = region.maxY() + 4 - IntMath.mod(region.maxY(), sampleRate) - 1;
        int newMaxZ = region.maxZ() + 4 - IntMath.mod(region.maxZ(), sampleRate) - 1;
        return Region3i.createFromMinMax(new Vector3i(newMinX, newMinY, newMinZ), new Vector3i(newMaxX, newMaxY, newMaxZ));
    }
}
