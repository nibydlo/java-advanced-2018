package ru.ifmo.rain.krylov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.String.join;

public class RecursiveWalk {

    public static void main(String[] args) {
        RecursiveWalk recWalk = new RecursiveWalk();
        recWalk.start(args);
    }

    public void start(String[] args)  {

        try {

            if (args != null && args[0] != null && args[1] != null && args.length == 2) {

                String inputName = args[0];
                String outputName = args[1];

                try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputName), StandardCharsets.UTF_8)) {

                    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputName), StandardCharsets.UTF_8)) {

                        String str;

                        while ((str = reader.readLine()) != null && str.length() != 0) {

                            try {
                                File file = new File(str);

                                if (file.isDirectory()) {
                                    try (Stream<Path> dt = Files.walk(file.toPath())) {
                                        dt.forEach((Path e) -> {
                                            if (!e.toFile().isDirectory()) {
                                                try {
                                                    writer.write(fnvl(e.toString()) + " " + e.toString());
                                                    writer.newLine();
                                                } catch (IOException el) {
                                                    try {
                                                        writer.write(join(" ", format("%08x", 0), e.toString()));
                                                        writer.newLine();
                                                    } catch (IOException j) {
                                                        System.err.println("Can't write to output file");
                                                    }
                                                }
                                            }
                                        });
                                    } catch (SecurityException e) {
                                        System.err.println("No access to some file");
                                    } catch (IOException e) {
                                        System.err.println("Exception with walking on files");
                                        try {
                                            writer.write(join(" ", format("%08x", 0), str, "\n"));
                                        } catch (IOException k) {
                                            System.err.println("Can't write to output file");
                                        }
                                    } catch (InvalidPathException e) {
                                        System.err.println("Path is invalid");
                                        try {
                                            writer.write(join(" ", format("%08x", 0), str, "\n"));
                                        } catch (IOException k) {
                                            System.err.println("Can't write to output file");
                                        }
                                    }
                                } else {
                                    writer.write(fnvl(file.getPath()) + " " + str + "\n");
                                }
                            } catch (IOException | InvalidPathException e) {
                                try {
                                    writer.write(join(" ", format("%08x", 0), str, "\n"));
                                } catch (IOException k) {
                                    System.err.println("Can't write to output file");
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("There is exception in output");
                    } catch (InvalidPathException e) {
                        System.err.println("Incorrect name of output file, don't use forbidden symbols");
                    } catch (SecurityException e) {
                        System.out.println("There is no access to output file");
                    } catch (UnsupportedOperationException e) {
                        System.out.println("Exception with unsupported operations");
                    }

                } catch (IOException e) {
                    System.err.println("There is exception in input");
                } catch (InvalidPathException e) {
                    System.err.println("Incorrect name of input file, don't use forbidden symbols");
                } catch (SecurityException e) {
                    System.err.println("There is no access to input file");
                }
            } else {
                System.out.println("check null's existing in arguments");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("not enough arguments, use format <input file> <output file>");
        }
    }

    private String fnvl(String filePath) throws IOException {

        int hash = 0x811c9dc5;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] bar = new byte[1024];
            int x;
            while ((x = fis.read(bar)) != -1) {
                for (int i = 0; i < x; i++) {
                    hash = (hash * 0x01000193) ^ (bar[i] & 0xff);
                }
            }
        } catch (FileNotFoundException e) {
            hash = 0;
            System.err.println("file not found");
        } catch (UnsupportedOperationException e) {
            hash = 0;
            System.err.println("Path isn't associated with the default provider");
        } catch (IOException e) {
            hash = 0;
            System.err.println("io exceptions");
        } catch (SecurityException e) {
            hash = 0;
            System.err.println("Haven't access to read file");
        }

        return format("%08x", hash);
    }
}
