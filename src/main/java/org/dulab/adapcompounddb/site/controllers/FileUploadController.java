package org.dulab.adapcompounddb.site.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.site.controllers.forms.FormField;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.site.controllers.forms.FileUploadForm;
import org.dulab.adapcompounddb.site.controllers.forms.MetadataForm;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.dulab.adapcompounddb.site.controllers.utils.MultipartFileUtils;
import org.dulab.adapcompounddb.site.services.CaptchaService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.FILE_UPLOAD_FIELDS_COOKIE_NAME;
import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.META_FIELDS_COOKIE_NAME;

@Controller
public class FileUploadController extends BaseController {

    private static final Logger LOG = LogManager.getLogger(FileUploadController.class);
    private final CaptchaService captchaService;

    private final Boolean integTest = ControllerUtils.INTEG_TEST;

    public FileUploadController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("chromatographyTypeList", ChromatographyType.values());
        model.addAttribute("fileTypeList", FileType.values());
    }

    @RequestMapping(value = "/file/upload/nmdr", method = RequestMethod.GET)
    public String uploadFromNMDR(@RequestParam String archive, @RequestParam String file,
                                 @RequestParam String chromatography,
                                 Model model, HttpSession session, HttpServletResponse httpServletResponse) {

        LOG.info(String.format("Uploading NMDR data from archive '%s' file '%s'", archive, file));
        
        ChromatographyType chromatographyType = null;
        chromatography = chromatography.toLowerCase();
        for (ChromatographyType type : ChromatographyType.values()) {
            if (type.name().toLowerCase().startsWith(chromatography)) {
                chromatographyType = type;
                break;
            }
        }

        if (chromatographyType == null)
            throw new IllegalStateException(
                    String.format("Chromatography of type %s is not supported", chromatography));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.metabolomicsworkbench.org")
                .path("/data/file_extract_7z.php")
                .query(String.format("A=%s", archive))
                .query(String.format("F=%s", file))
                .build().encode();

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(uriComponents.toUriString()).openStream())) {

            MultipartFile multipartFile = new MockMultipartFile(file, file, "multipart/form-data", inputStream);

            FileUploadForm fileUploadForm = new FileUploadForm();
            fileUploadForm.setChromatographyType(chromatographyType);
            fileUploadForm.setFiles(Collections.singletonList(multipartFile));

            Submission.clear(session);
            return upload(model, session, fileUploadForm, null, httpServletResponse, null, null);

        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot read NMDR file %s from archive %s", archive, file), e);
        }
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.GET)
    public String upload(Model model, HttpSession session,
                         @CookieValue(value = FILE_UPLOAD_FIELDS_COOKIE_NAME, defaultValue = "") String metaFieldsInJson) {

        if (Submission.from(session) != null) {
            return "redirect:/file/";
        }

        FileUploadForm form = ConversionsUtils.byteStringToForm(metaFieldsInJson, FileUploadForm.class);

        model.addAttribute("fileUploadForm", form);
        model.addAttribute("loggedInUser", getCurrentUserPrincipal());
        model.addAttribute("integTest", integTest);
        return "submission/upload";
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.POST, consumes = "multipart/form-data")
    public String upload(Model model, HttpSession session, @Valid FileUploadForm form, Errors errors,
                         HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String responseString = request.getParameter(CaptchaService.GOOGLE_CAPTCHA_RESPONSE);



        try{
            if(getCurrentUserPrincipal() == null && !integTest ) {
                captchaService.processResponse(responseString, request.getRemoteAddr());
            }
        }
        catch (Exception e) {
            model.addAttribute("message", "Verify that you are human");
            return  "submission/upload";
        }

        if (Submission.from(session) != null) {
            return "redirect:/file/";
        }

        if (errors != null && errors.hasErrors()) {
            return "submission/upload";
        }

        Submission submission = new Submission();
//        try {
        MultipartFileUtils.readMultipartFile(submission, form.getFiles(), form.getChromatographyType(),
                null, false, form.isRoundMzValues());
//        } catch (IllegalStateException e) {
//            LOG.warn(e.getMessage(), e);
//            model.addAttribute("message", e.getMessage());
//            return "submission/upload";
//        }

        Submission.assign(session, submission);
        session.setAttribute("FileUploadForm", form);
        String byteString = ConversionsUtils.formToByteString(form);
        Cookie metaFieldsCookie = new Cookie(FILE_UPLOAD_FIELDS_COOKIE_NAME, byteString);
        response.addCookie(metaFieldsCookie);


        if(form.isEditMetadata()) {
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/submission/metadata";
        }



        return "redirect:/file/";
    }
    @PostMapping(value ="/testUpload")
    public String upload2(@RequestBody MultipartFile file, @ModelAttribute Submission submission, RedirectAttributes redirectAttributes)
    {
        // Submission submission = new Submission();
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);

        MultipartFileUtils.readMultipartFile(submission, fileList, null, null, false , false);



//        ModelAndView modelAndView = new ModelAndView();
//
//        final SubmissionForm submissionForm = new SubmissionForm(submission);
//        submissionForm.setAuthorized(true);
//        submissionForm.setIsLibrary(submissionService.isLibrary(submission));
//
//        modelAndView.setViewName("submission/view");
//        modelAndView.addObject("submission", submission);
//        modelAndView.addObject("submissionForm", submissionForm);
//        modelAndView.addObject("view_submission", true);
//        modelAndView.addObject("edit_submission", true);
//        modelAndView.addObject("availableTags", submissionService.findUniqueTagStrings());

        // Submission.assign(session, submission);


        //return modelAndView;

        redirectAttributes.addFlashAttribute("submission", submission);
        return "redirect:/file2/";

    }
    @RequestMapping(value = "/submission/metadata", method = RequestMethod.GET)
    public String submitMetadata(Model model, HttpSession session, HttpServletResponse response,
                                 @CookieValue(value = META_FIELDS_COOKIE_NAME, defaultValue = "") String metaFieldsInJson) {
        List<List<SpectrumProperty>> propertyList = new ArrayList<>();
        List<FileType> fileTypes = new ArrayList<>();
        Submission submission = Submission.from(session);
        //MetadataForm form = (MetadataForm) model.getAttribute("form");
        MetadataForm form = ConversionsUtils.byteStringToForm(metaFieldsInJson, MetadataForm.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map cookieMap = objectMapper.convertValue(form, Map.class);

        for(File file : submission.getFiles()) {
            propertyList.add(file.getSpectra().get(0).getProperties());
            fileTypes.add(file.getFileType());

        }
        model.addAttribute("metadataForm", form);
        model.addAttribute("spectrumProperties", propertyList);
        model.addAttribute("cookieForm", cookieMap);
        List<FormField> fields = getRequiredFormFields((FileUploadForm) session.getAttribute("FileUploadForm"));
        model.addAttribute("fieldList", fields);
        model.addAttribute("fileTypes", fileTypes);
        return "submission/metadata";
    }

    @RequestMapping(value = "/submission/metadata", method = RequestMethod.POST)
    public String submitMetadata(Model model, HttpSession session, @Valid @ModelAttribute("metadataForm") MetadataForm form, Errors errors,
                                 HttpServletResponse response, HttpServletRequest request){
        Submission submission = Submission.from(session);



        for(File file : submission.getFiles()) {
            MetaDataMapping metaDataMapping = form.getMetaDataMappings() != null ? form.getMetaDataMappings().get(file.getFileType()) : null;
            for(Spectrum spectrum: file.getSpectra()) {
                spectrum.setProperties(spectrum.getProperties(),metaDataMapping);
            }

        }
        if(form.isMergeFiles()) {
            submission.setFiles(MultipartFileUtils.mergeFiles(submission.getFiles()));
        }
        Submission.assign(session, submission);
        
        String byteString = ConversionsUtils.formToByteString(form);
        Cookie metaFieldsCookie = new Cookie(META_FIELDS_COOKIE_NAME, byteString);
        response.addCookie(metaFieldsCookie);

        return "redirect:/file/";
    }

    private List<FormField> getRequiredFormFields(FileUploadForm form) {
        List<FormField> formFields = new ArrayList<>();
        if(form.isEditMetadata()) {
            if(form.isEditNameField())
                formFields.add(new FormField("NameField", "Name Field"));
            if(form.isEditCanonicalSmilesField())
                formFields.add(new FormField("CanonicalSmilesField", "Canonical Smiles Field"));
            if(form.isEditFormulaField())
                formFields.add(new FormField("FormulaField", "Formula Field"));
            if(form.isEditInChiField())
                formFields.add(new FormField("InChiField", "InChI Field"));
            if (form.isEditInChiKeyField())
                formFields.add(new FormField("InChiKeyField", "InChIKey Field"));
            if(form.isEditIsotopeField())
                formFields.add(new FormField("IsotopeField", "Isotopic Distribution Field"));
            if(form.isEditKeggField())
                formFields.add(new FormField("KeggField", "KEGG ID Field"));
            if(form.isEditCasNoField())
                formFields.add(new FormField("CasNoField","Cas ID Field"));
            if(form.isEditHmdbField())
                formFields.add(new FormField("HmdbField", "HMDB ID Field"));
            if(form.isEditExternalIdField())
                formFields.add(new FormField("ExternalIdField","External ID Field"));
            if(form.isEditMassField())
                formFields.add(new FormField("MassField", "Neutral Mass"));
            if(form.isEditPrecursorMzField())
                formFields.add(new FormField("PrecursorMzField", "Precursor m/z Field"));
            if(form.isEditSynonymField())
                formFields.add(new FormField("SynonymField","Synonym Field"));
            if(form.isEditPubChemField())
                formFields.add(new FormField("PubChemField", "PubChem ID Field"));
            if(form.isEditRetentionTimeField())
                formFields.add(new FormField("RetentionTimeField", "Retention Time Field"));
            if (form.isEditRetentionIndexField())
                formFields.add(new FormField("RetentionIndexField", "Retention Index Field"));


        }
        return formFields;

    }


}
