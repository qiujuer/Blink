/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/31/2015
 * Changed 04/02/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.blink.core;

import java.io.File;

/**
 * Blink files Resource
 */
public interface Resource<T> {
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

    /**
     * Get the Mark
     *
     * @return T
     */
    T getMark();
}
