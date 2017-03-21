package io.udvi.util.image.core;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sureshreddy on 21/03/17.
 */
@Data
public class MetadataParser {

    private Metadata metadata;

    private File imageFile;

    private ExifSubIFDDirectory exifSubIFDDirectory;

    private ExifIFD0Directory exifIFD0Directory;

    private GpsDirectory gpsDirectory;

    private FileMetadataDirectory fileMetadataDirectory;

    private Calendar originalCreatedTime;

    public MetadataParser (File imageFile) {
        try {
            this.metadata = ImageMetadataReader.readMetadata(imageFile);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.imageFile = imageFile;
        this.exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        this.exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        this.gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        this.fileMetadataDirectory = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
    }

    public Calendar getOriginalDate() {
        if (originalCreatedTime != null) {
            return this.originalCreatedTime;
        }

        Date originalDate = null;
        if (this.exifSubIFDDirectory != null && this.exifSubIFDDirectory.getDateOriginal() != null) {
            originalDate = this.getExifSubIFDDirectory().getDateOriginal();
        } else {
            try {

                BasicFileAttributes basicFileAttributes = Files.readAttributes(
                        Paths.get(imageFile.getAbsolutePath()),
                        BasicFileAttributes.class);

                originalDate = new Date(basicFileAttributes.creationTime().toMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (originalDate != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(originalDate);
            this.originalCreatedTime = calendar;
        }
        return originalCreatedTime;
    }

    public String getDeviceMake() {
        return exifIFD0Directory != null ? exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE) : null;
    }
}
