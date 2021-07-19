package client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var client = new ClientMachine();
        new UI(client);
        client.close();
        return;
    }
}
