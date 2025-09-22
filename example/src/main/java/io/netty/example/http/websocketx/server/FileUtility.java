/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.netty.example.http.websocketx.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtility {
    private static Logger logger = LoggerFactory.getLogger(FileUtility.class);
    private static FileUtility fileUtil = new FileUtility();

    private FileUtility() {
    }

    public static FileUtility getInstance() {
        return fileUtil;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public String readFileContent(InputStream inputStream, String charset) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toString(charset);
        } catch (IOException e) {
            logger.error("write error", e);
            return "";
        } finally {
            try {
                bos.close();
                inputStream.close();
            } catch (IOException ignore) {
            }
        }
    }
}