package jiconv;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Converter {
    private static void readStdin(ByteArrayOutputStream outputStream) throws IOException {
        int bytesRead;
        while (true) {
            byte[] buffer = new byte[1024];
            bytesRead = System.in.read(buffer);
            if (bytesRead < 0) {
                break;
            } else {
                outputStream.write(buffer, 0, bytesRead);
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
        boolean stripShifts = false;

        for (int i = 0; i < args.length; i++) {
            String argName = args[i];
            if (!argName.startsWith("-")) {
                System.err.printf("%s is not a valid argument.\n", argName);
                System.exit(1);
            }

            String argValue = null;

            if (argName.equals("-f") || argName.equals("-t")) {
                if (args.length <= i + 1) {
                    System.err.printf("Argument %s is missing a value.\n", argName);
                    System.exit(1);
                }

                argValue = args[++i];
            }

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
                case "-stripshifts":
                    stripShifts = true;
                    break;
            }
        }

        String inputString = new String(outputStream.toByteArray(), inputEncoding);
        byte[] output = inputString.getBytes(outputEncoding);

        if (output[output.length - 1] == 0x15) {
            output = Arrays.copyOf(output, output.length - 1);
        }

        if (stripShifts) {
            for (int i = 0; i < output.length; i++) {
                // 0x0e = SHIFT IN, 0x0f = SHIFT OUT
                if (output[i] != 0x0e && output[i] != 0x0f) {
                    System.out.write(output[i]);
                }
            }
        } else {
            System.out.write(output, 0, output.length);
        }
        System.out.flush();
    }
}
