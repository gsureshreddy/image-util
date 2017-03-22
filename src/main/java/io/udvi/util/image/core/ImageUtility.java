package io.udvi.util.image.core;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by anandabh on 1/24/2016.
 */
public class ImageUtility {
    public static BufferedImage resizeAndGreyImage(BufferedImage image, int width, int height){

        //int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        //BufferedImage resizedImage = new BufferedImage(width, height, type);

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        graphics.setComposite(AlphaComposite.Src);

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    private static BufferedImage greyImage(BufferedImage image){
        BufferedImage greyImage =
                new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = greyImage.createGraphics();
        graphics.drawImage(image,0,0,null);
        return greyImage;
    }

    public static int[][] getGreyPixelArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] greyPixelArray = new int[height][width];
        for(int y=0;y<image.getHeight();y++)
            for(int x=0; x<image.getWidth(); x++)
                greyPixelArray[y][x] = image.getRGB(x, y)& 0xFF;
        return  greyPixelArray;
    }

    public static String generateImageHash(int[][] colorArray){
        StringBuffer sb = new StringBuffer();
        int height = colorArray.length;
        int width = colorArray[0].length - 1;
        for(int y=0; y< width; y++) {
            for (int x = 0; x <  height; x++) {
                sb.append(colorArray[y][x] < colorArray[y][x+1]?"1":"0");
            }
        }
        return sb.toString();
    }
    public static String getImageHash(String filePath, Map<String, Integer> imageAtrributes ){
        String returnValue = null;

        try{
            returnValue = generateImageHash(
                 getGreyPixelArray(
                        resizeAndGreyImage(
                                ReadImage.readFromPath(filePath, imageAtrributes), 9, 8
                        )
                )
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }
    public static String getImageHash(File filePath, Map<String, Integer> imageAtrributes ){
        String returnValue = null;

        try{
            BufferedImage loadedImage = ReadImage.readFromPath(filePath, imageAtrributes);
            if(loadedImage==null || imageAtrributes.get(Constants.IMAGE_HEIGHT)==null
                    || imageAtrributes.get(Constants.IMAGE_WIDTH) == null)
                return null;
            returnValue = generateImageHash(
                    getGreyPixelArray(
                            resizeAndGreyImage(
                                    loadedImage, 9, 8
                            )
                    )
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    public static Metadata getImageMetadata(File file) {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (ImageProcessingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean isSimilar(String hashA, String hashB){
        if(hashA.length() != hashB.length()){
            return false;
        }
        int distance = 0 ;
        for(int x=0;x< hashA.length();x++){
            if(hashA.charAt(x) != hashB.charAt(x))
                distance++;
        }
        if(distance <= 10)
            return true;
        else
            return false;
    }

    public static void collectAllImages(File directory, List<File> filesList, boolean recursive) {
        if (null == directory && null == directory.listFiles())
            return;
        File[] files = directory.listFiles();

        for (File file :  files) {
            if (file.isDirectory()) {
                if (recursive)
                    collectAllImages(file, filesList, recursive);
            }

            if (file.isFile() && null != getImageMetadata(file)) {
                filesList.add(file);
            }
        }
    }
}