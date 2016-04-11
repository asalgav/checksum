package main.inps.tools;

import java.io.File;

/**
 * Created by adrian.salas on 26/01/2016.
 */
public class FileHashGenerator {

    /**
     * @param args 0: Hashing option available: MD5, SHA-1, SHA-256
     * @param args 1: Path to mb.properties file to get the checksum value
     * @param args 2: Path to the exclusion.properties file
     * @param args 3: Temporal file to store the result of the checksum value. This is required due to Pascal does not
     *                  retrieve the value directly from the jar file
     */
    public static void main(String[] args) {
        try {

            if (args.length != 4) {
                throw new Exception("Usage: wrong number of parameters");
            }

            String hashOption = args[0];

            String mbPropFilePath = args[1];
            System.out.println("MB.properties file path: " + mbPropFilePath);
            File mbfile = new File(mbPropFilePath);

            String exclusionPropFilePath = args[2];
            System.out.println("exclusion.properties file path: " + exclusionPropFilePath);
            File exclfile = new File(exclusionPropFilePath);

            String hashResultPath = args[3];
            System.out.println("exclusion.properties file path: " + hashResultPath);

            if(hashOption.equals(Constants.MD5EXCLUSIONS)) {
                String md5HashExclusionList =
                        HashGeneratorUtils.generateMD5ExclusionPropList(mbfile, exclfile, hashResultPath);
                System.out.println("MD5 Hash with exclusions: " + md5HashExclusionList);
            } else if(hashOption.equals(Constants.MD5)) {
                String md5Hash = HashGeneratorUtils.generateMD5(mbfile, hashResultPath);
                System.out.println("MD5 Hash: " + md5Hash);
            } else if(hashOption.equals(Constants.SHA1)) {
                String sha1Hash = HashGeneratorUtils.generateSHA1(mbfile, hashResultPath);
                System.out.println("SHA-1 Hash: " + sha1Hash);
            } else if(hashOption.equals(Constants.SHA256)) {
                String sha256Hash = HashGeneratorUtils.generateSHA256(mbfile, hashResultPath);
                System.out.println("SHA-256 Hash: " + sha256Hash);
            } else {
                throw new Exception("Hash option required is not available. "
                        + "Try passing MD5, MD5ExclusionList, SHA-1, SHA-256");
            }

        } catch (HashGenerationException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
