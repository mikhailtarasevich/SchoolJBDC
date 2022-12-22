package com.mikhail.tarasevich.reader.impl;

import com.google.inject.Inject;
import com.mikhail.tarasevich.reader.ConsoleReader;

import java.util.Scanner;

public class ConsoleReaderImpl implements ConsoleReader {

    @Inject
    public ConsoleReaderImpl(){}

    @Override
    public String read() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public int readInt() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
