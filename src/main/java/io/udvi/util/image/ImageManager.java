package io.udvi.util.image;

import io.udvi.util.image.service.ImageOrganizer;
import lombok.Data;

/**
 * Created by sureshreddy on 22/03/17.
 */
@Data
public class ImageManager {

    private static String sourcePath = "";
    private static String targetPath = "";

    public static void main(String args[]) {
        ImageOrganizer imageOrganizer = new ImageOrganizer(sourcePath, targetPath);
        imageOrganizer.setRecursive(true);
        imageOrganizer.organizeAndCopy();
    }
}
