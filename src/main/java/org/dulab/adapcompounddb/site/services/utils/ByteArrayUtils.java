package org.dulab.adapcompounddb.site.services.utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ByteArrayUtils {
    
    public static String compressDoubleArrays(double[] mzArray, double[] intensityArray) throws Exception {
        // Step 1: Convert double arrays to byte array
        byte[] byteArray = doubleArraysToByteArray(mzArray, intensityArray);
        
        // Step 2: Compress the byte array
        byte[] compressedBytes = compressByteArray(byteArray);
        
        // Step 3: Encode to Base64
        return Base64.getEncoder().encodeToString(compressedBytes);
    }
    
    private static byte[] doubleArraysToByteArray(double[] mzArray, double[] intensityArray) {
        // Allocate space for 2 integers (array lengths) + all doubles
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 8 * (mzArray.length + intensityArray.length));

        // PUt size of each array
        buffer.putInt(mzArray.length);
        buffer.putInt(intensityArray.length);

        // Put all mz values
        for (double mz : mzArray) {
            buffer.putDouble(mz);
        }
        
        // Put all intensity values
        for (double intensity : intensityArray) {
            buffer.putDouble(intensity);
        }
        
        return buffer.array();
    }
    
    private static byte[] compressByteArray(byte[] input) throws Exception {
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[1024];
        
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        
        outputStream.close();
        return outputStream.toByteArray();
    }
    public static double[][] decompressToDoubleArrays(String encoded) throws Exception {
        // Decode Base64
        byte[] compressedBytes = Base64.getDecoder().decode(encoded);

        // Decompress
        Inflater inflater = new Inflater();
        inflater.setInput(compressedBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        byte[] decompressedBytes = outputStream.toByteArray();
        outputStream.close();

        // Wrap in ByteBuffer and split into mz and intensity
        ByteBuffer byteBuffer = ByteBuffer.wrap(decompressedBytes);
        int length = decompressedBytes.length / 16; // 8 bytes per double, 2 arrays
        double[] mzArray = new double[length];
        double[] intensityArray = new double[length];
        for (int i = 0; i < length; i++) mzArray[i] = byteBuffer.getDouble();
        for (int i = 0; i < length; i++) intensityArray[i] = byteBuffer.getDouble();

        return new double[][]{ mzArray, intensityArray };
    }



} 