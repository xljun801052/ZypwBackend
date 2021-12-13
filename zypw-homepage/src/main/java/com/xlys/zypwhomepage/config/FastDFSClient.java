package com.xlys.zypwhomepage.config;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FastDFSClient {

    private static final String CONF_FILENAME = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "fdfs_client.conf";

    private static StorageClient storageClient;

    static {
        try {
            // load configuration file
            ClientGlobal.init(CONF_FILENAME);
            // initialize Tracker client
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            // initialize Tracker server
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            // initialize Storage server
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            // initialize Storage client
            storageClient = new StorageClient(trackerServer, storageServer);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }

    public static String uploadFile(InputStream inputStream, String fileName) {
        try {
            byte[] fileBuff = null;
            NameValuePair[] metaList = null;
            if (inputStream != null) {
                int len = inputStream.available();
                metaList = new NameValuePair[2];
                metaList[0] = new NameValuePair("file_name", fileName);
                metaList[1] = new NameValuePair("file_length", String.valueOf(len));
                fileBuff = new byte[len];
                inputStream.read(fileBuff);
            }

            //upload file
            return Arrays.stream(storageClient.upload_file(fileBuff, getFileExt(fileName), metaList)).collect(Collectors.joining("/", "", ""));
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uploadFile(File file, String fileName) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return uploadFile(fis, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
    }

    public static FileInfo getFileInfo(String groupName, String remoteFileName) {
        try {
            return storageClient.get_file_info(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NameValuePair[] getMetaData(String groupName, String remoteFileName) {
        try {
            // get the metadata of the specified file by Storage client according to the group name and file name.
            return storageClient.get_metadata(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream downloadFile(String groupName, String remoteFileName) {
        try {
            // get the byte stream of the specified file by Storage client according to the group name and file name.
            byte[] bytes = storageClient.download_file(groupName == null ? "group1" : groupName, remoteFileName);
            // return the byte stream
            return new ByteArrayInputStream(bytes);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * delete file
     *
     * @param groupName      gropup name. default: group1
     * @param remoteFileName file name. e.g.："M00/00/00/wKgKZl9tkTCAJAanAADhaCZ_RF0495.jpg"
     * @return zero stands for success，non-zero stands for failure
     */
    public static int deleteFile(String groupName, String remoteFileName) {
        int result = -1;
        try {
            // delete file by Storage client according to the group name and file name
            result = storageClient.delete_file(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * modify an existing file.
     *
     * @param oldGroupName old group name
     * @param oldFileName  old file name
     * @param file         the new file
     * @param fileName     the new file name
     */
    public static String modifyFile(String oldGroupName, String oldFileName, File file, String fileName) {
        // upload first
        String fileIds = uploadFile(file, fileName);
        if (fileIds == null) {
            return null;
        }
        // then perform delete
        int delResult = deleteFile(oldGroupName, oldFileName);
        if (delResult != 0) {
            return null;
        }
        return fileIds;
    }

    public static void main(String[] args) {
        //test Upload
        String uploadFile = FastDFSClient.uploadFile(new File("e:\\0.webp"), "hahaha.jpg");
        System.out.println("uploadFile = " + uploadFile);
    }

}
