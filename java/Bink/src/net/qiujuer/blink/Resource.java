package net.qiujuer.blink;

import java.io.File;

/**
 * Blink files Resource
 */
public interface Resource {
    /**
     * Create a file from resource.
     *
     * @return New file
     */
    File create(long id);

    /**
     * Change a file name
     *
     * @param file    File
     * @param newName New name
     * @return Status
     */
    boolean rename(File file, String newName);


    /**
     * Cut a file to new path
     *
     * @param oldFile File
     * @param newPath New path
     * @return New File
     */
    File cut(File oldFile, String newPath);

    /**
     * Cut a file to new path
     *
     * @param oldFile Old file
     * @param newFile New file
     * @return New file
     */
    File cut(File oldFile, File newFile);


    /**
     * Removes a file from the resource.
     *
     * @param name File name
     */
    void remove(String name);

    /**
     * Removes a file from the resource.
     *
     * @param file File
     */
    void remove(File file);

    /**
     * Empties the resource by oneself
     */
    void clear();

    /**
     * Empties the resource by the path
     */
    void clearAll();
}
