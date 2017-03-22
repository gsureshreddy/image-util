package io.udvi.util.image.service;

import io.udvi.util.image.core.Constants;
import io.udvi.util.image.core.ImageUtility;
import lombok.Data;

import java.io.File;
import java.util.*;

/**
 * Created by sureshreddy on 22/03/17.
 */
@Data
public class DuplicateImageFinder {

    private final String baseDirectoryPath;

    private final File sourceDirectory;

    private boolean recursive = false;

    private long counter = 0;

    private List<String> allUniqueImages = new ArrayList<>();

    private List<String> allImagesForDeletion = new ArrayList<>();

    List<File> imagesFileList = new ArrayList<File>();

    private Map<String, Integer> imagesMap = new HashMap<>();

    private Map<String, String> imagesHashMap = new LinkedHashMap<>();

    private List<Map<String, Integer>> imagePairs = new ArrayList<Map<String, Integer>>();

    public DuplicateImageFinder(String baseDirectoryPath, boolean recursive) {
        this.baseDirectoryPath = baseDirectoryPath;
        this.sourceDirectory = new File(this.baseDirectoryPath);
        this.recursive = recursive;
        processImages();
    }

    private void processImages () {
        System.out.println("Collecting All Images");
        ImageUtility.collectAllImages(this.sourceDirectory, imagesFileList, true);
        if(imagesFileList.isEmpty())
            return;
        System.out.println("Found " + imagesFileList.size() + " images");

        Map<String, Integer> imageAttributes = new HashMap<String, Integer>();
        for(File imagesFile:imagesFileList){
            imageAttributes.clear();
            imagesMap.put(imagesFile.getAbsolutePath(),
                    imageAttributes.get(Constants.IMAGE_WIDTH) * imageAttributes.get(Constants.IMAGE_WIDTH));
            imagesHashMap.put(imagesFile.getAbsolutePath(), ImageUtility.getImageHash(imagesFile, imageAttributes));
        }
    }

    private List<Map<String, Integer>> findImagePairs(){
        List<Map<String, Integer>> output = new ArrayList<Map<String, Integer>>();

        Set<String> allImagesFoundDuplicates = new HashSet<String>();

        for(Map.Entry<String, String> entry1: imagesHashMap.entrySet())
            for(Map.Entry<String, String> entry2: imagesHashMap.entrySet()) {

                if(!entry1.getKey().equalsIgnoreCase(entry2.getKey()) &&
                        ImageUtility.isSimilar(entry1.getValue(), entry2.getValue())) {

                    boolean isImageFoundInPair = false;
                    for(Map outputMap: output){
                        if(outputMap.containsKey(entry1.getKey()) || outputMap.containsKey(entry2.getKey())){
                            outputMap.put(entry1.getKey(), imagesMap.get(entry1.getKey()));
                            outputMap.put(entry2.getKey(), imagesMap.get(entry2.getKey()));
                            isImageFoundInPair = true;
                            break;
                        }
                    }
                    if(!isImageFoundInPair){
                        Map<String, Integer> outputMap = new HashMap<String, Integer>();
                        outputMap.put(entry1.getKey(), imagesMap.get(entry1.getKey()));
                        outputMap.put(entry2.getKey(), imagesMap.get(entry2.getKey()));
                        output.add(outputMap);
                    }
                    allImagesFoundDuplicates.add(entry1.getKey());
                    allImagesFoundDuplicates.add(entry2.getKey());
                }
            }

        Set<String> allImages = new HashSet<String>(imagesMap.keySet());
        allImages.removeAll(allImagesFoundDuplicates);
        for(String outputElement:allImages){
            Map<String, Integer> outputMap = new HashMap<String, Integer>();
            outputMap.put(outputElement, imagesMap.get(outputElement));
            output.add(outputMap);
        }
        return output;
    }

    public List<List<String>>findDuplicatePairs(String directoryPath){
        List<Map<String, Integer>> pairs = this.imagePairs;
        List<List<String>> output = new ArrayList<List<String>>();
        if(null == pairs || pairs.isEmpty())
            return null;
        for(Map<String, Integer> pair:pairs){
            if(!pair.isEmpty() && pair.keySet().size() > 1) {
                List<String> outputElement = new ArrayList<String>(pair.keySet());
                output.add(outputElement);
            }
        }
        return output;
    }

    public List<String> findDuplicatesForDeletion(String directoryPath){
        List<Map<String, Integer>> pairs = this.imagePairs;
        List<String> output = new ArrayList<String>();
        boolean isFirstElementInPair = true;
        if(null == pairs || pairs.isEmpty())
            return null;
        for(Map<String, Integer> pair:pairs){
            if(!pair.isEmpty() && pair.keySet().size() > 1){
                List<Map.Entry<String, Integer>> pairEntryList =
                        new ArrayList<Map.Entry<String, Integer>>(pair.entrySet());
                Collections.sort(pairEntryList, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return (o2.getValue()).compareTo(o1.getValue());
                    }
                });
                isFirstElementInPair = true;
                for(Map.Entry<String, Integer> entry:pairEntryList){
                    if(isFirstElementInPair){
                        isFirstElementInPair = false;
                    }else{
                        output.add(entry.getKey());
                    }
                }
            }
        }
        return output;
    }
    public List<String> findAllUniqueImages(String directoryPath){
        List<Map<String, Integer>> pairs = this.imagePairs;

        List<String> output = new ArrayList<String>();
        if(null == pairs || pairs.isEmpty())

            return null;
        for(Map<String, Integer> pair:pairs){
            if(!pair.isEmpty() && pair.keySet().size() == 1) {
                output.add(String.valueOf(pair.keySet().toArray()[0]));
            }else if(!pair.isEmpty() && pair.keySet().size() > 1){

                List<Map.Entry<String, Integer>> pairEntryList =
                        new ArrayList<Map.Entry<String, Integer>>(pair.entrySet());
                Collections.sort(pairEntryList, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return (o2.getValue()).compareTo(o1.getValue());
                    }
                });
                output.add(pairEntryList.get(0).getKey());
            }
        }
        return output;
    }
}
