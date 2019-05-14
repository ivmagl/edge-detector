package xyz.imahlatii.edgedetector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.imahlatii.edgedetector.algorithm.CannyAlgorithm;
import xyz.imahlatii.edgedetector.exception.FileStorageException;
import xyz.imahlatii.edgedetector.exception.MyFileNotFoundException;
import xyz.imahlatii.edgedetector.model.DBFile;
import xyz.imahlatii.edgedetector.repository.DBFileRepository;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class DBFileStorageService {

    private static final double CANNY_THRESHOLD_RATIO = .4; //Suggested range .2 - .4
    private static final int CANNY_STD_DEV = 3;

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            DBFile dbFile = new DBFile(fileName, file.getContentType(), LocalDateTime.now(Clock.systemUTC()), prepareFile(file));

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public DBFile getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    public byte[] prepareFile(MultipartFile file) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            String fileName = file.getOriginalFilename();

            ImageIO.write(CannyAlgorithm.cannyEdges(ImageIO.read(file.getInputStream()), CANNY_STD_DEV,
                    CANNY_THRESHOLD_RATIO), fileName.substring(fileName.lastIndexOf(".") + 1), baos);
            return baos.toByteArray();
        }
    }
}
