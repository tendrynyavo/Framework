package etu2070.framework;

import java.io.FileOutputStream;
import java.io.File;

public class FileUpload {
    
    String name;
    String path;
    byte[] data;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileUpload(String name, String path, byte[] data) {
        this(name, data);
        setPath(path);
    }

    public FileUpload(String name, byte[] data) {
        setName(name);
        setData(data);
    }

    public void upload() throws Exception {
        File outputFile = new File(this.getPath() + this.getName());
        outputFile.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(this.getData());
        }
    }
}
