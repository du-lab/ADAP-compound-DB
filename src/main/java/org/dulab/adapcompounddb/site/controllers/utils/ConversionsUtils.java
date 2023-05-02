package org.dulab.adapcompounddb.site.controllers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.entities.Peak;
//rdkit java wrapper
import org.RDKit.*;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ConversionsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsUtils.class);
    static{
        try {
            loadLibrary();
        } catch (Exception e) {
            LOGGER.error("****RDKIT ERROR: " + e.getMessage(), e) ;
        }
    }


    public static String peaksToJson(Collection<Peak> peaks) {
        return String.format("[%s]", peaks.stream()
                .map(p -> String.format("{'mz':%f,'intensity':%f}", p.getMz(), p.getIntensity()))
                .collect(Collectors.joining(",")));
    }

    public static List<Peak> jsonToPeaks(String json) {
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        List<Peak> peaks = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); ++i) {
            Peak peak = new Peak();
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            peak.setMz(jsonObject.get("mz").getAsDouble());
            peak.setIntensity(jsonObject.get("intensity").getAsDouble());
            peaks.add(peak);
        }
        return peaks;
    }

    public static String formatDouble(Double x) {
        if (x == null) return null;
        return String.format("%.3f", x);
    }
    public static String toImageJava(@Nullable String smiles, @Nullable String inchi)  {

        RWMol mol = null;

        if (smiles != null)
        {
            mol = RWMol.MolFromSmiles(smiles);
        }
        else if (inchi !=null){
            RDKFuncs f = new RDKFuncs();
            ExtraInchiReturnValues rv = new ExtraInchiReturnValues();
            mol = f.InchiToMol(inchi, rv);
        }
        else return null;

        mol.compute2DCoords();
        MolDraw2DSVG drawer = new MolDraw2DSVG(300,200);

        drawer.drawMolecule(mol);
        drawer.finishDrawing();
        return drawer.getDrawingText();

    }
    public static String toImagePython(@Nullable String smiles, @Nullable String inchi)  {

        String parameters;
        if (smiles != null && !smiles.isEmpty())
            parameters = "--smiles " + smiles;
        else if (inchi != null && !inchi.isEmpty())
            parameters = "--inchi " + inchi;
        else
            return null;

        try {
            // using the Runtime exec method:
            String command = String.format("python3 generate_image_for_smiles.py %s", parameters);
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String read;
            while ((read = stdInput.readLine()) != null) {
                output.append(read);
            }

            String image = output.toString();
            if (!image.isEmpty())
                return image;

            // read any errors from the attempted command
            StringBuilder errorOutput = new StringBuilder();
            String line;
            while ((line = stdError.readLine()) != null) {
                errorOutput.append(line);
                errorOutput.append('\n');
            }

            String error = errorOutput.toString();
            if (!error.isEmpty())
                LOGGER.warn(error);

        } catch (IOException e) {
            LOGGER.warn("Error while plotting a structure", e);
        }
        return null;
    }

    public static <T> T byteStringToForm(String jsonString, Class<T> formClass) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            byte[] jsonBytes = Base64.getDecoder().decode(jsonString);
            return objectMapper.readValue(jsonBytes, formClass);
        } catch (IOException e) {
            try {
                return formClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                throw new IllegalStateException("Cannot initialize a form: " + ex.getMessage(), ex);
            }
        }
    }

    public static <T> String formToByteString(T form) {

        ObjectMapper objectMapper = new ObjectMapper();

        byte[] jsonBytes;
        try {
            jsonBytes = objectMapper.writeValueAsBytes(form);
            return Base64.getEncoder().encodeToString(jsonBytes);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Cannot convert Form to Json: " + e.getMessage(), e);
            return "";
        }
    }
    private static void loadLibrary() throws URISyntaxException, IOException {
        //get os name
        //System.out.println("***************" + System.getProperty("java.version"));
        String osname = System.getProperty("os.name");
        String architecture = System.getProperty("os.arch");
        String libName = null;
        osname = osname.toLowerCase();
        InputStream in = null;
        if(osname == null)
            throw new RuntimeException("Couuld not determine os properly");
        else if(osname.contains("linux")){
            if(architecture == null || architecture.isEmpty())
                throw new RuntimeException("Could not determine architecture properly");
            else if(architecture.contains("aarch64")){
                in = ConversionsUtils.class.getResourceAsStream("linux-aarch64/libGraphMolWrap.so");
            }
            else if (architecture.contains("amd64")){
                in = ConversionsUtils.class.getResourceAsStream("linux-amd64/libGraphMolWrap.so");
            }
            else{
                throw new RuntimeException("Could not determine architecture properly: "  + osname + ", " + architecture);
            }


            libName = "libGraphMolWrap.so";
        }

        else if(osname.contains("mac") )
        {
            if(architecture == null || architecture.isEmpty())
                throw new RuntimeException("Could not determine architecture properly");
            else if(architecture.contains("x86_64")) {
                in = ConversionsUtils.class.getResourceAsStream("mac-amd64/libGraphMolWrap.jnilib");
            }
            else if(architecture.contains("aarch64")) {
                in = ConversionsUtils.class.getResourceAsStream("mac-arm/libGraphMolWrap.jnilib");
            }
            else
                throw new RuntimeException("Could not determine architecture properly: "  + osname + ", " + architecture);

            libName = "libGraphMolWrap.jnilib";
        }
        else if(osname.contains("windows")) {
            in = ConversionsUtils.class.getResourceAsStream("windows-amd64/GraphMolWrap.dll");
            libName = "libGraphMolWrap.dll";
        }

        //get path to native library, create duplicate and load it then delete
        File tmpDir = Files.createTempDirectory("my-native-lib").toFile();
        tmpDir.deleteOnExit();
        File nativeLibTmpFile = new File(tmpDir, libName);
        nativeLibTmpFile.deleteOnExit();

        Files.copy(in, nativeLibTmpFile.toPath());

        System.load(nativeLibTmpFile.getAbsolutePath());


    }






}
