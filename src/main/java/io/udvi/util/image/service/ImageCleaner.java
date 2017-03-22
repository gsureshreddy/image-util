package io.udvi.util.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import lombok.Data;

import java.io.File;
import java.io.IOException;

/**
 * Created by sureshreddy on 22/03/17.
 */
@Data
public class ImageCleaner {

    private final String baseDirectoryPath;

    private final File sourceDirectory;

    private final String targetDirectoryPath;

    private final File targetDirectory;

    private boolean recursive = false;

    private long counter = 0;

    public ImageCleaner(String baseDirectoryPath, String targetDirectoryPath) {
        this.baseDirectoryPath = baseDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
        this.sourceDirectory = new File(this.baseDirectoryPath);
        this.targetDirectory = new File(this.targetDirectoryPath);
    }

}
