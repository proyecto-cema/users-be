package com.cema.users.constants;

public class Messages {

    public static final String USER_DOES_NOT_EXISTS = "User %s doesn't exits";
    public static final String USER_ALREADY_EXISTS = "The user %s already exists";
    public static final String OUTSIDE_ESTABLISHMENT = "Error trying to access resource from a different establishment %s.";
    public static final String ACTION_NOT_ALLOWED = "Error trying to perform action your rol %s is not authorized for.";

    private Messages() {
        //constants class cannot be built
    }
}
