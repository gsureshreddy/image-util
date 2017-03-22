package io.udvi.util.image;

import io.udvi.util.image.service.ImageOrganizer;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by sureshreddy on 22/03/17.
 */
@Data
public class ImageManager {

    private static String sourcePath = "/Volumes/SURESHREDDY/";
    private static String targetPath = "/Volumes/SURESH-EXT/PHOTOS_ORGANIZED/";

    public static void main(String args[]) {
        deleteEmptyFolders(null);
    }

    public static void deleteEmptyFolders(File file) {
        try {
            Files.walkFileTree(Paths.get(sourcePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (Files.isDirectory(file)) {
                        System.out.println("File::" + file.toFile().getAbsolutePath());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if ( !(dir.startsWith(".") || dir.getFileName().startsWith("."))&& !Files.isHidden(dir) && Files.isWritable(dir) && dir.toFile().listFiles().length == 0) {
                        System.out.println("File::" + dir.toFile().getAbsolutePath());
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void organizePhotos() {
        ImageOrganizer imageOrganizer = new ImageOrganizer(sourcePath, targetPath);
        imageOrganizer.setRecursive(true);
        imageOrganizer.setCounter(10000);
        imageOrganizer.organizeAndCopy();
    }
}
