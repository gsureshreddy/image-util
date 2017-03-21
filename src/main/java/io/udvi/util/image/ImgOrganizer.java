package io.udvi.util.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author sureshreddy
 * @since 1.0.0
 * Created by sureshreddy on 21/03/17.
 */
public class ImgOrganizer {

    private static String directory = "/Volumes/SURESHREDDY/ORGANIZED_PHOTOS/";
    private static String organizedDirectory = "/Volumes/WORKSPACE/ORGANIZED_PHOTOS/";

    public static void main(String args[]) {

        File baseDir = new File(directory);
        organizeByOriginalDate(baseDir.listFiles(), baseDir);
    }

    public static void processDirectory(File file) {
        if (file.isDirectory()) {

        }
    }

    public static void organizeByOriginalDate(File[] files, File baseDir) {
        if (files == null) {
            return;
        }
        long i = 0;
        for(File f : files) {
            System.out.println(baseDir + " :: " + i + "/" + files.length);
            i++;
            if (f.isFile()) {
                Metadata metadata = null;
                try {
                    metadata = ImageMetadataReader.readMetadata(f);
                    ExifSubIFDDirectory directory1 = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                    ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                    if (exifDirectory == null) {
                        return;
                    }
                    String make = exifDirectory.getString(ExifIFD0Directory.TAG_MAKE);
                    Date originalDate = null;
                    if (directory1 != null && directory1.getDateOriginal() != null) {
                        originalDate = directory1.getDateOriginal();

                    } else {
                        FileMetadataDirectory fileMetaDataDirectory = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
                        originalDate = fileMetaDataDirectory.getDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE);
                        if (originalDate == null && f.getName().contains(".jpg")) {
                            System.out.println("F:"+ f.getName());
                        }
                    }

                    if (originalDate != null & make != null) {
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(originalDate);
                        String folderName = make + "/" + calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH);
                        new File(organizedDirectory + folderName).mkdirs();
                        Files.move(Paths.get(f.getAbsolutePath()),
                                Paths.get(organizedDirectory + folderName + "/" + f.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                } catch (ImageProcessingException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            } else if (f.isDirectory()) {
                if (!f.getAbsolutePath().startsWith(organizedDirectory)) {
                    organizeByOriginalDate(f.listFiles(), f);
                }

            }
        }
    }}
