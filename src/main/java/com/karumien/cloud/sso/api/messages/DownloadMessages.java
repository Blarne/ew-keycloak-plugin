/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.api.messages;

import java.io.IOException;
import java.util.Arrays;

/**
 * Import message sources
 *
 * @since 1.0, 11. 4. 2020 18:41:48 
 */
public class DownloadMessages {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.err.println("USE: java -jar DownloadMessages <api_token> <project_id> <target_folder> <languages> <type>");
            return;
        }
        
        String apiToken = args[0];
        String project = args[1];
        String targetFolder = args[2];
        String lngs = args[3];
        String type = args.length > 4 ? args[4] : "properties";
        
        for (String lng : Arrays.asList(lngs.split(","))) {
        
            String loc = lng;
            if (lng.contains(":")) {
                loc = lng.substring(lng.indexOf(":") + 1) ; 
                lng = lng.substring(0, lng.indexOf(":"));
            } 

            String filename = "messages_"+loc+"."+type;
            
            String data = String.format("api_token=%s&id=%s&language=%s&type="+type, apiToken, project, lng);
            
            String download = HttpDownloadUtility.request("POST", "https://api.poeditor.com/v2/projects/export", data, targetFolder, null);
            
            if (download.indexOf("https:") < 0) {
                System.out.println(filename + " -> IGNORED");
                continue;
            }
            
            String url = download.substring(download.indexOf("https:"), download.length() - download.indexOf("\"") - 2).replaceAll("\\\\/", "/"); 

            System.out.println(url + " -> " + filename);
            HttpDownloadUtility.request("GET", url, null, targetFolder, filename);
        }

    }

}
