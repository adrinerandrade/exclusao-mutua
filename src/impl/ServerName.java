package impl;

import java.util.Collections;
import java.util.Random;

public class ServerName {

    public ServerNameResponse notifyCreation() {
        return new ServerNameResponse(Collections.emptyList(), new Random().nextInt());
    }

}
