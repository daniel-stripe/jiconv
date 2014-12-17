package jiconv;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class Converter {
    private static void readStdin(ByteArrayOutputStream outputStream) throws IOException {
        int bytesRead;
        while (true) {
            byte[] buffer = new byte[1024];
            bytesRead = System.in.read(buffer);
            if (bytesRead < 0) {
                break;
            } else {
                outputStream.write(buffer);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        readStdin(outputStream);
        
        String inputEncoding = null;
        String outputEncoding = null;
        
        for (int i = 0; i < args.length; i++) {
            String argName = args[i];
            if (!argName.startsWith("-")) {
                System.err.printf("%s is not a valid argument.\n", argName);
                System.exit(1);
            }
            
            if (args.length <= i + 1) {
                System.err.printf("Argument %s is missing a value.\n", argName);
                System.exit(1);
            }
            
            String argValue = args[++i];
            switch (argName) {
                case "-f":
                    if (inputEncoding != null && !inputEncoding.equals(argValue)) {
                        System.err.printf("Found multiple values for %s.\n", argName);
                        System.exit(1);
                    }
                    inputEncoding = argValue;
                    break;
                case "-t":
                    if (outputEncoding != null && !outputEncoding.equals(argValue)) {
                        System.err.printf("Found multiple values for %s.\n", argName);
                        System.exit(1);
                    }
                    outputEncoding = argValue;
                    break;
            }
        }
        
        String inputString = new String(outputStream.toByteArray(), inputEncoding);
        byte[] output = inputString.getBytes(outputEncoding);
        System.out.write(output);
    }
}
