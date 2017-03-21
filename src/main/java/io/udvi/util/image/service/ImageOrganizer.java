package io.udvi.util.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import io.udvi.util.image.core.MetadataParser;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

/**
 * Created by sureshreddy on 21/03/17.
 */
@Data
public class ImageOrganizer {

    private final String baseDirectoryPath;

    private final File sourceDirectory;

    private final String targetDirectoryPath;

    private final File targetDirectory;

    private boolean recursive = false;

    private long counter = 5000;

    public ImageOrganizer(String baseDirectoryPath, String targetDirectoryPath) {
        this.baseDirectoryPath = baseDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
        this.sourceDirectory = new File(this.baseDirectoryPath);
        this.targetDirectory = new File(this.targetDirectoryPath);
    }

    private Metadata getImageMetadata(File file) {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (ImageProcessingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private void processDirectory(File directory) {
        if (directory.listFiles() == null)
            return;
        System.out.println("Processing: " + directory.getAbsolutePath() + " [ " + directory.list().length + "]");
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                processDirectory(file);
            } else {
                if (getImageMetadata(file) != null) {
                    MetadataParser metadataParser = new MetadataParser(file);
                    if (metadataParser.getDeviceMake() != null) {
                        Calendar calendar = metadataParser.getOriginalDate();
                        String folder = metadataParser.getDeviceMake() + "/" + calendar.get(Calendar.YEAR) + "/";
                        new File(targetDirectoryPath + folder).mkdirs();
                        try {
                            Files.move(
                                    Paths.get(file.getAbsolutePath()),
                                    Paths.get(targetDirectoryPath + folder + "PHOTO_" + counter + "_" + file.getName()),
                                    StandardCopyOption.REPLACE_EXISTING
                            );
                            counter++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
    public void organizeAndCopy() {
        processDirectory(sourceDirectory);
    }

    public static void main(String args[]) {
        ImageOrganizer imageOrganizer = new ImageOrganizer(
                "/Volumes/SURESH/ONE_DRIVE",
                "/Volumes/SURESH-EXT/PHOTOS_ORGANIZED/"
        );
        imageOrganizer.organizeAndCopy();

    }

}
