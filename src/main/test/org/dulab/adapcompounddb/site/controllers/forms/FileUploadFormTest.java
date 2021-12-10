package org.dulab.adapcompounddb.site.controllers.forms;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUploadFormTest {

    @Test
    public void toJsonBytes() {

        FileUploadForm fileUploadForm = new FileUploadForm();
        byte[] data = fileUploadForm.toJsonBytes();
    }

    @Test
    public void fromJsonBytes() {
    }
}