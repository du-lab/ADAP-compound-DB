package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileUploadFormTest {

    @Test
    public void toJsonBytes() {

        FileUploadForm fileUploadForm = new FileUploadForm();
        String byteString = ConversionsUtils.formToByteString(fileUploadForm);
    }

    @Test
    public void fromJsonBytes() {
    }
}