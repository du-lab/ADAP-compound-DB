package org.dulab.adapcompounddb.site.controllers.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class loadNativeUtils {
    private static final Logger LOGGER = LogManager.getLogger(loadNativeUtils.class);

//    static{
//        try {
//            loadLibrary();
//        } catch (URISyntaxException | UnsatisfiedLinkError  e) {
//            LOGGER.error(e.getMessage(), e);
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
    private static void loadLibrary() throws URISyntaxException {
        //get os name
        //System.out.println("***************" + System.getProperty("java.version"));
        String osname = System.getProperty("os.name");
        osname = osname.toLowerCase();
        URL url = null;
//        if(osname == null)
//            throw new RuntimeException("Couuld not determine os properly");
//        else if(osname.contains("linux")){
//            //url = ConversionsUtils.class.getResource("linux-aarch64/libGraphMolWrap.so");
//            System.loadLibrary("GraphMolWrap.so");
//        }
//
//        else if(osname.contains("mac")){
//           url = ConversionsUtils.class.getResource("mac-amd64/libGraphMolWrap.jnilib");
//            //System.loadLibrary("GraphMolWrap.jnilib");
//        }
//
//        else if(osname.contains("windows"))
//            url = ConversionsUtils.class.getResource("windows-amd64/GraphMolWrap.dll");
//
//
//        File file = new File(url.toURI());
//        String path = file.getAbsolutePath();

        //System.load(path);
        System.load("/Users/tnguy271/Downloads/apache-tomcat-9.0.67/shared/lib/libGraphMolWrap.jnilib");


    }
}
