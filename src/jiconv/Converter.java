package jiconv;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Converter {
    static final byte EBCDICShiftIn = 0x0e;
    static final byte EBCDICShiftOut = 0x0f;

    private static void readStdin(ByteArrayOutputStream outputStream) throws IOException {
        int bytesRead;
        while (true) {
            byte[] buffer = new byte[1024];
            if (System.in.available() == 0) {
                break;
            }

            bytesRead = System.in.read(buffer);
            if (bytesRead < 0) {
                break;
            } else {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void error(String reason) {
        error(reason, false);
    }

    private static void error(String reason, boolean showUsage) {
        System.err.println("Error: " + reason);
        if (showUsage) {
            System.err.println("Usage: java jiconv/Converter [options]");
            System.err.println();
            System.err.println("A Java iconv.");
            System.err.println();
            System.err.println("Options:");
            System.err.printf("    %-20s%s%s", "-f ENCODING", "Set input encoding", System.lineSeparator());
            System.err.printf("    %-20s%s%s", "-t ENCODING", "Set output encoding", System.lineSeparator());
            System.err.printf("    %-20s%s%s", "-addshifts", "Surround input with EBCDIC shift bytes", System.lineSeparator());
            System.err.printf("    %-20s%s%s", "-stripshifts", "Strip EBCDIC shift bytes from output", System.lineSeparator());
        }
        System.exit(1);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String inputEncoding = null;
        String outputEncoding = null;
        boolean addShifts = true;
        boolean stripShifts = false;

        for (int i = 0; i < args.length; i++) {
            String argName = args[i];
            if (!argName.startsWith("-")) {
                error(String.format("%s is not a valid argument.\n", argName), true);
            }

            String argValue = null;

            if (argName.equals("-f") || argName.equals("-t")) {
                if (args.length <= i + 1) {
                    error(String.format("Argument %s is missing a value.\n", argName), true);
                }

                argValue = args[++i];
            }

            switch (argName) {
                case "-f":
                    if (inputEncoding != null && !inputEncoding.equals(argValue)) {
                        error(String.format("Found multiple values for %s.\n", argName), true);
                    }
                    inputEncoding = argValue;
                    break;
                case "-t":
                    if (outputEncoding != null && !outputEncoding.equals(argValue)) {
                        error(String.format("Found multiple values for %s.\n", argName), true);
                    }
                    outputEncoding = argValue;
                    break;
                case "-addshifts":
                    addShifts = true;
                    break;
                case "-stripshifts":
                    stripShifts = true;
                    break;
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (addShifts) {
            outputStream.write(EBCDICShiftIn);
        }

        readStdin(outputStream);

        if (addShifts) {
            outputStream.write(EBCDICShiftOut);
        }

        String inputString = new String(outputStream.toByteArray(), inputEncoding);
        byte[] output = inputString.getBytes(outputEncoding);

        if (output[output.length - 1] == 0x15) {
            output = Arrays.copyOf(output, output.length - 1);
        }

        if (stripShifts) {
            for (int i = 0; i < output.length; i++) {
                if (output[i] != EBCDICShiftIn && output[i] != EBCDICShiftOut) {
                    System.out.write(output[i]);
                }
            }
        } else {
            System.out.write(output, 0, output.length);
        }
        System.out.flush();
    }
}
